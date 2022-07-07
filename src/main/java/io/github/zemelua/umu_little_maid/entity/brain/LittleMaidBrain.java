package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;

public final class LittleMaidBrain {

	public static Brain<?> create(Brain<LittleMaidEntity> brain) {
		LittleMaidBrain.addCoreTasks(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();

//		ImmutableList.of(
//				Pair.of(0, new TimeLimitedTask<>(new FollowMobTask(EntityType.PLAYER, 6.0f), UniformIntProvider.create(30, 60))),
//				Pair.of(0, new BreedTask(EntityType.GOAT, 1.0f)),
//				Pair.of(1, new TemptTask(goat -> Float.valueOf(1.25f))),
//				Pair.of(2, new WalkTowardClosestAdultTask(WALKING_SPEED, 1.25f)),
//				Pair.of(3, new RandomTask(
//						ImmutableList.of(
//								Pair.of(new StrollTask(1.0f), 2),
//								Pair.of(new GoTowardsLookTarget(1.0f, 3), 2),
//								Pair.of(new WaitTask(30, 60), 1)
//						)
//				))),
//
//				ImmutableSet.of(Pair.of(
//						MemoryModuleType.RAM_TARGET,
//						MemoryModuleState.VALUE_ABSENT),
//						Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_ABSENT)
//				);

		return brain;
	}

	private static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, 0, ImmutableList.<Task<? super LittleMaidEntity>>builder().add(
				new StayAboveWaterTask(0.8f),
				new WalkTask(2.0f),
				new LookAroundTask(45, 90),
				new WanderAroundTask(),
				new TemptationCooldownTask(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS)
		).build());
	}

//	private static void addIdleTasks(Brain<LittleMaidEntity> brain) {
//		brain.setTaskList(Activity.IDLE, ImmutableList.<Pair<Integer, ? extends Task<? super LittleMaidEntity>>>builder().add(
//				Pair.of(0, new TimeLimitedTask<>(new FollowMobTask(EntityType.PLAYER, 6.0f), UniformIntProvider.create(30, 60))),
//				Pair.of(1, new TemptTask(goat -> 1.25f)),
//						(Pair<Integer, ? extends Task<? super LittleMaidEntity>>) Pair.of(2, new RandomTask(
//								ImmutableList.of(
//										Pair.of(new StrollTask(1.0f), 2),
//										Pair.of(new GoTowardsLookTarget(1.0f, 3), 2),
//										Pair.of(new WaitTask(30, 60), 1)
//								)
//						))).build(),
//
//				ImmutableSet.of(Pair.of(
//						MemoryModuleType.RAM_TARGET,
//						MemoryModuleState.VALUE_ABSENT),
//						Pair.of(MemoryModuleType.LONG_JUMP_MID_JUMP, MemoryModuleState.VALUE_ABSENT)
//				));
//	}

	private LittleMaidBrain() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	public static void updateActivities(LittleMaidEntity littleMaid) {
		littleMaid.getBrain().resetPossibleActivities(ImmutableList.of(Activity.IDLE));
	}
}