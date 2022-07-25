package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.*;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.ForgetShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.MaidEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.RememberShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.*;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.ForgetHomeTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.ForgetShouldSleepTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.RememberHomeTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.RememberShouldSleepTask;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import java.util.Set;

public final class MaidFarmerBrainManager {
	private static final Set<MemoryModuleType<?>> MEMORY_MODULES;
	private static final Set<SensorType<? extends Sensor<? super LittleMaidEntity>>> SENSORS;

	public static Brain.Profile<LittleMaidEntity> createProfile() {
		return Brain.createProfile(MaidFarmerBrainManager.MEMORY_MODULES, MaidFarmerBrainManager.SENSORS);
	}

	public static void initializeBrain(Brain<LittleMaidEntity> brain) {
		MaidFarmerBrainManager.addCoreTasks(brain);
		MaidFarmerBrainManager.addIdleTasks(brain);
		MaidFarmerBrainManager.addSitTasks(brain);
		MaidFarmerBrainManager.addEatTasks(brain);
		MaidFarmerBrainManager.addFarmTasks(brain);
		MaidFarmerBrainManager.addSleepTasks(brain);

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
	}

	public static void tickBrain(Brain<LittleMaidEntity> brain) {
		brain.resetPossibleActivities(ImmutableList.of(ModEntities.ACTIVITY_SIT, ModEntities.ACTIVITY_EAT, Activity.REST, ModEntities.ACTIVITY_FARM, Activity.IDLE));
	}

	private static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, ImmutableList.of(
				Pair.of(0, new StayAboveWaterTask(0.8F)),
				Pair.of(0, new OpenDoorsTask()),
				Pair.of(1, new LookAroundTask(45, 90)),
				Pair.of(2, new WanderAroundTask()),
				Pair.of(3, new KeepAroundFarmSiteTask<>()),
				Pair.of(98, new RememberShouldEatTask()),
				Pair.of(98, new RememberShouldSleepTask<>(12000L)),
				Pair.of(98, new RememberHomeTask<>()),
				Pair.of(98, new RememberFarmPosTask<>()),
				Pair.of(98, new RememberFarmSiteTask<>()),
				Pair.of(99, new ForgetShouldEatTask()),
				Pair.of(99, new ForgetShouldSleepTask<>(12000L)),
				Pair.of(99, new ForgetHomeTask<>()),
				Pair.of(99, new ForgetFarmSiteTask<>())
		));
	}

	private static void addIdleTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.IDLE, ImmutableList.of(
				Pair.of(0, new FarmerFollowOwnerTask<>(10.0F, 2.0F)),
				Pair.of(1, new TimeLimitedTask<LivingEntity>(new FollowMobTask(EntityType.PLAYER, 6.0F), UniformIntProvider.create(30, 60))),
				Pair.of(2, new RandomTask<>(ImmutableList.of(
						Pair.of(new StrollTask(0.8F), 2),
						Pair.of(new GoTowardsLookTarget(0.8F, 3), 2),
						Pair.of(new WaitTask(30, 60), 1)
				)))
		));
	}

	private static void addSitTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_SIT, ImmutableList.of(
				Pair.of(0, new SitTask<>())
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_IS_SITTING, MemoryModuleState.VALUE_PRESENT)
		));
	}

	private static void addEatTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_EAT, ImmutableList.of(
				Pair.of(0, new MaidEatTask())
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_SHOULD_EAT, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
		), ImmutableSet.of(ModEntities.MEMORY_SHOULD_EAT));
	}

	private static void addFarmTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_FARM, ImmutableList.of(
				Pair.of(0, new MaidFarmTask()),
				Pair.of(1, new WalkToFarmPosTask<>(0.8F))
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_FARM_POS, MemoryModuleState.VALUE_PRESENT),
				Pair.of(ModEntities.MEMORY_FARM_COOLDOWN, MemoryModuleState.VALUE_ABSENT),
				Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
		), ImmutableSet.of(ModEntities.MEMORY_FARM_POS, ModEntities.MEMORY_FARM_COOLDOWN));
	}

	public static void addSleepTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.REST, ImmutableList.of(
				Pair.of(0, new SleepTask()),
				Pair.of(1, new WalkToHomeTask<>(0.8F))
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_SHOULD_SLEEP, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_PRESENT)
		));
	}

	private MaidFarmerBrainManager() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	static {
		MEMORY_MODULES = ImmutableSet.of(
				MemoryModuleType.WALK_TARGET,
				MemoryModuleType.PATH,
				MemoryModuleType.DOORS_TO_CLOSE,
				MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleType.MOBS,
				MemoryModuleType.VISIBLE_MOBS,
				MemoryModuleType.IS_PANICKING,
				ModEntities.MEMORY_IS_SITTING,
				ModEntities.MEMORY_SHOULD_EAT,
				ModEntities.MEMORY_FARMABLE_POS,
				ModEntities.MEMORY_FARM_POS,
				ModEntities.MEMORY_FARM_COOLDOWN,
				ModEntities.MEMORY_FARM_SITE,
				ModEntities.MEMORY_FARM_SITE_CANDIDATE,
				ModEntities.MEMORY_SHOULD_SLEEP,
				MemoryModuleType.NEAREST_BED,
				MemoryModuleType.HOME,
				MemoryModuleType.LAST_WOKEN,
				MemoryModuleType.LAST_SLEPT
		);
		SENSORS = ImmutableSet.of(
				SensorType.NEAREST_LIVING_ENTITIES,
				ModEntities.SENSOR_SHOULD_EAT,
				ModEntities.SENSOR_MAID_FARMABLE_POSES,
				ModEntities.SENSOR_FARM_SITE_CANDIDATE,
				ModEntities.SENSOR_HOME_CANDIDATE
		);
	}
}
