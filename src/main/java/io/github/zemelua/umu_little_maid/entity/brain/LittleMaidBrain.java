package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.FollowOwnerTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.SitTask;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;

public final class LittleMaidBrain {
	public static Brain<LittleMaidEntity> create(Brain<LittleMaidEntity> brain) {
		LittleMaidBrain.addCoreTasks(brain);
		LittleMaidBrain.addSitTasks(brain);
		LittleMaidBrain.addIdleTasks(brain);
		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();

		return brain;
	}

	private static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, ImmutableList.<Pair<Integer, ? extends Task<? super LittleMaidEntity>>>builder()
				.add(Pair.of(0, new StayAboveWaterTask(0.8F))) // 水に浮く
				.add(Pair.of(1, new WalkTask(1.0F))) // パニックのとき移動先を設定する
				.add(Pair.of(2, new LookAroundTask(45, 90)))
				.add(Pair.of(3, new WanderAroundTask()))
				.build());
	}

	private static void addSitTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.SIT, ImmutableList.<Pair<Integer, ? extends Task<? super LittleMaidEntity>>>builder()
				.add(Pair.of(0, new SitTask()))
				.build(), ImmutableSet.<Pair<MemoryModuleType<?>, MemoryModuleState>>builder()
				.add(Pair.of(ModEntities.IS_SITTING, MemoryModuleState.VALUE_PRESENT))
				.build());
	}

	private static void addIdleTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.IDLE, ImmutableList.<Pair<Integer, ? extends Task<? super LittleMaidEntity>>>builder()
				.add(Pair.of(0, new FollowOwnerTask<>(10.0F, 2.0F)))
				.build());
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
		// UMULittleMaid.LOGGER.info(littleMaid.getBrain().getPossibleActivities().stream().map(Activity::getId).toList());

		Brain<?> brain = littleMaid.getBrain();
		Activity activity = brain.getFirstPossibleNonCoreActivity().orElse(null);
		littleMaid.getBrain().resetPossibleActivities(ImmutableList.of(ModEntities.SIT, Activity.IDLE));
	}
}
