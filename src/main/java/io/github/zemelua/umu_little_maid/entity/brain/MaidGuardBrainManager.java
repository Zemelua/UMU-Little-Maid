package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.MaidGuardTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.UpdateAttractTargetsTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.UpdateGuardTargetTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.UpdateShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.StayAboveWaterTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;

import java.util.Set;

public final class MaidGuardBrainManager {
	private static final Set<MemoryModuleType<?>> MEMORY_MODULES;
	private static final Set<SensorType<? extends Sensor<? super LittleMaidEntity>>> SENSORS;

	public static Brain.Profile<LittleMaidEntity> createProfile() {
		return Brain.createProfile(MaidGuardBrainManager.MEMORY_MODULES, MaidGuardBrainManager.SENSORS);
	}

	public static void initializeBrain(Brain<LittleMaidEntity> brain) {
		MaidGuardBrainManager.addCoreTasks(brain);
		MaidNoneBrainManager.addIdleTasks(brain);
		MaidNoneBrainManager.addSitTasks(brain);
		MaidFencerBrainManager.addEatTasks(brain);
		MaidGuardBrainManager.addGuardTasks(brain);

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
	}

	public static void tickBrain(Brain<LittleMaidEntity> brain) {
		brain.resetPossibleActivities(ImmutableList.of(ModEntities.ACTIVITY_SIT, ModEntities.ACTIVITY_EAT, ModEntities.ACTIVITY_GUARD, Activity.IDLE));
	}

	public static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, ImmutableList.of(
				Pair.of(0, new StayAboveWaterTask(0.8F)),
				Pair.of(1, new LookAroundTask(45, 90)),
				Pair.of(2, new WanderAroundTask()),
				Pair.of(3, new UpdateShouldEatTask<>(living -> living.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET))),
				Pair.of(4, new UpdateAttractTargetsTask()),
				Pair.of(4, new UpdateGuardTargetTask())
				// Pair.of(4, new ForgetGuardTargetTask())
		));
	}

	public static void addGuardTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_GUARD, ImmutableList.of(
				Pair.of(0, new MaidGuardTask<>(6.0D, 2.0D, 1.5F))
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_ATTRACT_TARGETS, MemoryModuleState.VALUE_PRESENT),
				Pair.of(ModEntities.MEMORY_GUARD_TARGET, MemoryModuleState.VALUE_PRESENT)
		), ImmutableSet.of(
				ModEntities.MEMORY_ATTRACT_TARGETS, ModEntities.MEMORY_GUARD_TARGET
		));
	}

	private MaidGuardBrainManager() throws IllegalAccessException {
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
				ModEntities.MEMORY_IS_SITTING,
				ModEntities.MEMORY_SHOULD_EAT,
				ModEntities.MEMORY_ATTRACTABLE_LIVINGS,
				ModEntities.MEMORY_GUARDABLE_LIVING,
				ModEntities.MEMORY_ATTRACT_TARGETS,
				ModEntities.MEMORY_GUARD_TARGET,
				ModEntities.MEMORY_OWNER
		);
		SENSORS = ImmutableSet.of(
				SensorType.NEAREST_LIVING_ENTITIES,
				ModEntities.SENSOR_SHOULD_EAT,
				ModEntities.SENSOR_MAID_ATTRACTABLE_LIVINGS,
				ModEntities.SENSOR_MAID_GUARDABLE_LIVING
		);
	}
}
