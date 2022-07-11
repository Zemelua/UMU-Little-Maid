package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public final class LittleMaidBrain {
	public static Brain<LittleMaidEntity> create(Brain<LittleMaidEntity> brain) {
		LittleMaidBrain.addCoreTasks(brain);
		LittleMaidBrain.addSitTasks(brain);
		LittleMaidBrain.addIdleTasks(brain);
		LittleMaidBrain.addFightTasks(brain);
		LittleMaidBrain.addGuardTasks(brain);
		LittleMaidBrain.addEatTasks(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();

		return brain;
	}

	private static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, ImmutableList.of(
				Pair.of(0, new StayAboveWaterTask(0.8F)),
				Pair.of(1, new MaidJobTask(
						new WalkTask(1.0F), ModEntities.NONE)),
				Pair.of(2, new LookAroundTask(45, 90)),
				Pair.of(3, new WanderAroundTask())
		));
	}

	private static void addSitTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_SIT, ImmutableList.of(
				Pair.of(0, new SitTask())
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_IS_SITTING, MemoryModuleState.VALUE_PRESENT)
		));
	}

	private static void addIdleTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.IDLE, ImmutableList.of(
				Pair.of(0, new FollowOwnerTask<>(10.0F, 2.0F)),
				Pair.of(1, new TimeLimitedTask<LivingEntity>(
						new FollowMobTask(EntityType.PLAYER, 6.0f), UniformIntProvider.create(30, 60))),
				Pair.of(2, new MaidJobTask(
						new UpdateAttackTargetTask<>(maid -> maid.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE)),
						ModEntities.FENCER, ModEntities.CRACKER, ModEntities.ARCHER)),
				Pair.of(2, new MaidJobTask(
						new UpdateGuardTargetTask(), ModEntities.GUARD)),
				Pair.of(2, new MaidJobTask(
						new UpdateAttractTargetsTask(), ModEntities.GUARD)),
				Pair.of(3, new RandomTask<>(ImmutableList.of(
						Pair.of(new StrollTask(0.7F), 2),
						Pair.of(new GoTowardsLookTarget(0.7F, 3), 2),
						Pair.of(new WaitTask(30, 60), 1)
				)))
		));
	}

	private static void addFightTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.FIGHT, ImmutableList.of(
				Pair.of(0, new MaidJobTask(
						new ForcedForgetMemoryTask<>(MemoryModuleType.ATTACK_TARGET),
						ModEntities.GUARD, ModEntities.NONE)),
				Pair.of(0, new MaidJobTask(
						new ForgetAttackTargetTask<>(),
						ModEntities.FENCER, ModEntities.CRACKER, ModEntities.ARCHER)),
				Pair.of(1, new MaidJobTask(
						new RangedApproachTask(1.0F), ModEntities.FENCER, ModEntities.CRACKER)),
				Pair.of(2, new MaidJobTask(
						new MeleeAttackTask(20), ModEntities.FENCER, ModEntities.CRACKER)),
				Pair.of(2, new MaidJobTask(
						new MaidBowAttackTask(15.0D, 1.0F, 20), ModEntities.ARCHER))
		), ImmutableSet.of(
				Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT)
		));
	}

	private static void addGuardTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_GUARD, ImmutableList.of(
				Pair.of(0, new MaidJobTask(
						new ForcedForgetMemoryTask<>(ModEntities.MEMORY_GUARD_TARGET),
						ModEntities.FENCER, ModEntities.CRACKER, ModEntities.ARCHER, ModEntities.NONE)),
				Pair.of(0, new MaidJobTask(
						new ForcedForgetMemoryTask<>(ModEntities.MEMORY_ATTRACT_TARGETS),
						ModEntities.FENCER, ModEntities.CRACKER, ModEntities.ARCHER, ModEntities.NONE)),
				Pair.of(0, new MaidJobTask(
						new ForgetGuardTargetTask(), ModEntities.GUARD)),
				Pair.of(1, new MaidJobTask(
						new MaidGuardTask(6.0D, 2.0D, 1.5F), ModEntities.GUARD))
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_ATTRACT_TARGETS, MemoryModuleState.VALUE_PRESENT),
				Pair.of(ModEntities.MEMORY_GUARD_TARGET, MemoryModuleState.VALUE_PRESENT)
		));
	}

	private static void addEatTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_EAT, ImmutableList.of(
				Pair.of(0, new MaidEatTask())
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_SHOULD_EAT, MemoryModuleState.VALUE_PRESENT)
		));
	}

	public static void updateActivities(LittleMaidEntity littleMaid) {
		Brain<?> brain = littleMaid.getBrain();
		brain.resetPossibleActivities(ImmutableList.of(
				ModEntities.ACTIVITY_SIT, ModEntities.ACTIVITY_EAT, Activity.FIGHT, ModEntities.ACTIVITY_GUARD, Activity.IDLE));

		if (brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).isPresent()) {
			UMULittleMaid.LOGGER.info(littleMaid.isInAttackRange(brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get()));
		}
	}

	private LittleMaidBrain() throws IllegalAccessException {
		throw new IllegalAccessException();
	}
}
