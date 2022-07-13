package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.*;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.MaidJob;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public final class LittleMaidBrain {
	private static final float RUN_SPEED = 1.0F;
	private static final float WALK_SPEED = 0.8F;
	private static final float FLOAT_CHANCE = 0.8F;
	private static final int COMPLETION_RANGE = 3;
	private static final IntProvider LOOK_TIME = UniformIntProvider.create(45, 90);
	private static final UniformIntProvider FOLLOW_MOB_TIME = UniformIntProvider.create(30, 60);
	private static final float FOLLOW_MOB_DISTANCE = 6.0F;
	private static final FloatProvider FOLLOW_OWNER_RANGE = UniformFloatProvider.create(2.0F, 10.0F);
	private static final UniformIntProvider WAIT_TIME = UniformIntProvider.create(30, 60);
	private static final int FENCER_ATTACK_INTERVAL = 20;
	private static final int CRACKER_ATTACK_INTERVAL = 30;
	private static final double BOW_ATTACK_RANGE = 15.0D;
	private static final int BOW_ATTACK_INTERVAL = 20;
	private static final MaidJob[] PASSIVE_JOBS = new MaidJob[]{ModEntities.JOB_NONE};
	private static final MaidJob[] ATTACK_JOBS = new MaidJob[]{ModEntities.JOB_FENCER, ModEntities.JOB_CRACKER, ModEntities.JOB_ARCHER};
	private static final MaidJob[] NOT_ATTACK_JOBS = new MaidJob[]{ModEntities.JOB_GUARD, ModEntities.JOB_NONE};
	private static final MaidJob[] MELEE_ATTACK_JOBS = new MaidJob[]{ModEntities.JOB_FENCER, ModEntities.JOB_CRACKER};
	private static final MaidJob[] FENCER_JOBS = new MaidJob[]{ModEntities.JOB_FENCER};
	private static final MaidJob[] CRACKER_JOBS = new MaidJob[]{ModEntities.JOB_CRACKER};
	private static final MaidJob[] GUARD_JOBS = new MaidJob[]{ModEntities.JOB_GUARD};
	private static final MaidJob[] NOT_GUARD_JOBS = new MaidJob[]{ModEntities.JOB_FENCER, ModEntities.JOB_CRACKER, ModEntities.JOB_ARCHER, ModEntities.JOB_NONE};

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
				Pair.of(0, new StayAboveWaterTask(LittleMaidBrain.FLOAT_CHANCE)),
				Pair.of(1, new MaidJobTask(new WalkTask(LittleMaidBrain.RUN_SPEED), LittleMaidBrain.PASSIVE_JOBS)),
				Pair.of(2, new LookAroundTask(LittleMaidBrain.LOOK_TIME.getMin(), LittleMaidBrain.LOOK_TIME.getMax())),
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
				Pair.of(0, new FollowOwnerTask<>(LittleMaidBrain.FOLLOW_OWNER_RANGE.getMax(), LittleMaidBrain.FOLLOW_OWNER_RANGE.getMin())),
				Pair.of(1, new TimeLimitedTask<LivingEntity>(
						new FollowMobTask(EntityType.PLAYER, LittleMaidBrain.FOLLOW_MOB_DISTANCE), LittleMaidBrain.FOLLOW_MOB_TIME)),
				Pair.of(2, new MaidJobTask(
						new UpdateAttackTargetTask<>(maid -> maid.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE)),
						LittleMaidBrain.ATTACK_JOBS)),
				Pair.of(2, new MaidJobTask(
						new UpdateGuardTargetTask(), LittleMaidBrain.GUARD_JOBS)),
				Pair.of(2, new MaidJobTask(
						new UpdateAttractTargetsTask(), LittleMaidBrain.GUARD_JOBS)),
				Pair.of(3, new RandomTask<>(ImmutableList.of(
						Pair.of(new StrollTask(LittleMaidBrain.WALK_SPEED), 2),
						Pair.of(new GoTowardsLookTarget(LittleMaidBrain.WALK_SPEED, LittleMaidBrain.COMPLETION_RANGE), 2),
						Pair.of(new WaitTask(LittleMaidBrain.WAIT_TIME.getMin(), LittleMaidBrain.WAIT_TIME.getMax()), 1)
				)))
		));
	}

	private static void addFightTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.FIGHT, ImmutableList.of(
				Pair.of(0, new MaidJobTask(
						new ForcedForgetMemoryTask<>(MemoryModuleType.ATTACK_TARGET),
						LittleMaidBrain.NOT_ATTACK_JOBS)),
				Pair.of(0, new MaidJobTask(
						new ForgetAttackTargetTask<>(),
						LittleMaidBrain.ATTACK_JOBS)),
				Pair.of(1, new MaidJobTask(
						new PounceAtTargetTask<>(), LittleMaidBrain.FENCER_JOBS)),
				Pair.of(2, new MaidJobTask(
						new RangedApproachTask(LittleMaidBrain.RUN_SPEED), LittleMaidBrain.MELEE_ATTACK_JOBS)),
				Pair.of(3, new MaidJobTask(
						new MeleeAttackTask(LittleMaidBrain.FENCER_ATTACK_INTERVAL), LittleMaidBrain.FENCER_JOBS)),
				Pair.of(3, new MaidJobTask(
						new MeleeAttackTask(LittleMaidBrain.CRACKER_ATTACK_INTERVAL), LittleMaidBrain.CRACKER_JOBS)),
				Pair.of(3, new MaidJobTask(
						new MaidBowAttackTask(LittleMaidBrain.BOW_ATTACK_RANGE, LittleMaidBrain.RUN_SPEED, LittleMaidBrain.BOW_ATTACK_INTERVAL),
						ModEntities.JOB_ARCHER))
		), ImmutableSet.of(
				Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT)
		));
	}

	private static void addGuardTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_GUARD, ImmutableList.of(
				Pair.of(0, new MaidJobTask(
						new ForcedForgetMemoryTask<>(ModEntities.MEMORY_GUARD_TARGET),
						LittleMaidBrain.NOT_GUARD_JOBS)),
				Pair.of(0, new MaidJobTask(
						new ForcedForgetMemoryTask<>(ModEntities.MEMORY_ATTRACT_TARGETS),
						LittleMaidBrain.NOT_GUARD_JOBS)),
				Pair.of(0, new MaidJobTask(
						new ForgetGuardTargetTask(), LittleMaidBrain.GUARD_JOBS)),
				Pair.of(1, new MaidJobTask(
						new MaidGuardTask(6.0D, 2.0D, 1.5F), LittleMaidBrain.GUARD_JOBS))
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_ATTRACT_TARGETS, MemoryModuleState.VALUE_PRESENT),
				Pair.of(ModEntities.MEMORY_GUARD_TARGET, MemoryModuleState.VALUE_PRESENT)
		));
	}

	private static void addEatTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_EAT, ImmutableList.of(
				Pair.of(0, new MaidEatTask())
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_SHOULD_EAT, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
		));
	}

	public static void updateActivities(LittleMaidEntity littleMaid) {
		Brain<?> brain = littleMaid.getBrain();
		brain.resetPossibleActivities(ImmutableList.of(
				ModEntities.ACTIVITY_SIT, ModEntities.ACTIVITY_EAT, Activity.FIGHT, ModEntities.ACTIVITY_GUARD, Activity.IDLE));
	}

	private LittleMaidBrain() throws IllegalAccessException {
		throw new IllegalAccessException();
	}
}
