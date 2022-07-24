package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.*;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.SleepTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;

import java.util.Set;

public final class MaidHealerBrainManager {
	private static final Set<MemoryModuleType<?>> MEMORY_MODULES;
	private static final Set<SensorType<? extends Sensor<? super LittleMaidEntity>>> SENSORS;

	public static Brain.Profile<LittleMaidEntity> createProfile() {
		return Brain.createProfile(MaidHealerBrainManager.MEMORY_MODULES, MaidHealerBrainManager.SENSORS);
	}

	public static void initializeBrain(Brain<LittleMaidEntity> brain) {
		MaidHealerBrainManager.addCoreTasks(brain);
		MaidNoneBrainManager.addIdleTasks(brain);
		MaidNoneBrainManager.addSitTasks(brain);
		MaidNoneBrainManager.addEatTasks(brain);
		MaidHealerBrainManager.addHealTasks(brain);
		MaidNoneBrainManager.addSleepTasks(brain);

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
	}

	public static void tickBrain(Brain<LittleMaidEntity> brain) {
		brain.resetPossibleActivities(ImmutableList.of(ModEntities.ACTIVITY_SIT, ModEntities.ACTIVITY_EAT, Activity.REST, ModEntities.ACTIVITY_HEAL, Activity.IDLE));
	}

	public static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, ImmutableList.of(
				Pair.of(0, new StayAboveWaterTask(0.8F)),
				Pair.of(1, new LookAroundTask(45, 90)),
				Pair.of(2, new WanderAroundTask()),
				Pair.of(3, new UpdateShouldEatTask(living -> false)),
				Pair.of(4, new UpdateShouldHealTask<>()),
				Pair.of(5, new UpdateShouldSleepTask<>()),
				Pair.of(5, new RememberHomeTask<>()),
				Pair.of(6, new ForgetHomeTask<>())
		));
	}

	public static void addHealTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_HEAL, ImmutableList.of(
				Pair.of(0, new MaidHealOwnerTask()),
				Pair.of(1, new ApproachToHealTask<>(1.0F))
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_SHOULD_HEAL, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
		), ImmutableSet.of(ModEntities.MEMORY_SHOULD_HEAL));
	}

	public static void addSleepTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.REST, ImmutableList.of(
				Pair.of(0, new WalkToHomeTask<>(0.8F)),
				Pair.of(1, new SleepTask())
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_SHOULD_SLEEP, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_PRESENT)
		));
	}

	private MaidHealerBrainManager() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	static {
		MEMORY_MODULES = ImmutableSet.of(
				MemoryModuleType.WALK_TARGET,
				MemoryModuleType.PATH,
				MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleType.MOBS,
				MemoryModuleType.VISIBLE_MOBS,
				MemoryModuleType.IS_PANICKING,
				ModEntities.MEMORY_IS_SITTING,
				ModEntities.MEMORY_SHOULD_EAT,
				ModEntities.MEMORY_SHOULD_HEAL,
				ModEntities.MEMORY_SHOULD_SLEEP,
				MemoryModuleType.NEAREST_BED,
				MemoryModuleType.HOME,
				MemoryModuleType.LAST_WOKEN,
				MemoryModuleType.LAST_SLEPT
		);
		SENSORS = ImmutableSet.of(
				SensorType.NEAREST_LIVING_ENTITIES,
				ModEntities.SENSOR_SHOULD_EAT,
				ModEntities.SENSOR_HOME_CANDIDATE
		);
	}
}
