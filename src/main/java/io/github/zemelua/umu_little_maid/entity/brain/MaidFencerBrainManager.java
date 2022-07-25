package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.FollowOwnerTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.MaidEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.PounceAtTargetTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.SitTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.ForgetShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.RememberShouldEatTask;
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

public final class MaidFencerBrainManager {
	private static final Set<MemoryModuleType<?>> MEMORY_MODULES;
	private static final Set<SensorType<? extends Sensor<? super LittleMaidEntity>>> SENSORS;

	public static Brain.Profile<LittleMaidEntity> createProfile() {
		return Brain.createProfile(MaidFencerBrainManager.MEMORY_MODULES, MaidFencerBrainManager.SENSORS);
	}

	public static void initializeBrain(Brain<LittleMaidEntity> brain) {
		MaidFencerBrainManager.addCoreTasks(brain);
		MaidFencerBrainManager.addIdleTasks(brain);
		MaidFencerBrainManager.addSitTasks(brain);
		MaidFencerBrainManager.addEatTasks(brain);
		MaidFencerBrainManager.addFightTasks(brain);

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
	}

	public static void tickBrain(Brain<LittleMaidEntity> brain) {
		brain.resetPossibleActivities(ImmutableList.of(ModEntities.ACTIVITY_SIT, ModEntities.ACTIVITY_EAT, Activity.REST, Activity.FIGHT, Activity.IDLE));
	}

	public static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, ImmutableList.of(
				Pair.of(0, new StayAboveWaterTask(0.8F)),
				Pair.of(0, new OpenDoorsTask()),
				Pair.of(1, new LookAroundTask(45, 90)),
				Pair.of(2, new WanderAroundTask()),
				Pair.of(98, new RememberShouldEatTask(living -> living.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET))),
				Pair.of(98, new UpdateAttackTargetTask<>(living -> living.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
				Pair.of(99, new ForgetShouldEatTask(living -> living.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET))),
				Pair.of(99, new ForgetAttackTargetTask<>())
		));
	}

	public static void addIdleTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.IDLE, ImmutableList.of(
				Pair.of(0, new FollowOwnerTask<>(10.0F, 2.0F)),
				Pair.of(1, new TimeLimitedTask<LivingEntity>(new FollowMobTask(EntityType.PLAYER, 6.0F), UniformIntProvider.create(30, 60))),
				Pair.of(2, new RandomTask<>(ImmutableList.of(
						Pair.of(new StrollTask(0.8F), 2),
						Pair.of(new GoTowardsLookTarget(0.8F, 3), 2),
						Pair.of(new WaitTask(30, 60), 1)
				)))
		));
	}

	public static void addSitTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_SIT, ImmutableList.of(
				Pair.of(0, new SitTask<>())
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_IS_SITTING, MemoryModuleState.VALUE_PRESENT)
		));
	}

	public static void addEatTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_EAT, ImmutableList.of(
				Pair.of(0, new MaidEatTask())
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_SHOULD_EAT, MemoryModuleState.VALUE_PRESENT)
		), ImmutableSet.of(ModEntities.MEMORY_SHOULD_EAT));
	}

	public static void addFightTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.FIGHT, ImmutableList.of(
				Pair.of(0, new MeleeAttackTask(20)),
				Pair.of(1, new PounceAtTargetTask<>()),
				Pair.of(2, new RangedApproachTask(1.0F))
		), ImmutableSet.of(
				Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT)
		), ImmutableSet.of(
				MemoryModuleType.ATTACK_TARGET
		));
	}

	private MaidFencerBrainManager() throws IllegalAccessException {
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
				ModEntities.MEMORY_IS_SITTING,
				ModEntities.MEMORY_SHOULD_EAT,
				MemoryModuleType.NEAREST_ATTACKABLE,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleType.ATTACK_COOLING_DOWN
		);
		SENSORS = ImmutableSet.of(
				SensorType.NEAREST_LIVING_ENTITIES,
				ModEntities.SENSOR_SHOULD_EAT,
				ModEntities.SENSOR_MAID_ATTACKABLE
		);
	}
}
