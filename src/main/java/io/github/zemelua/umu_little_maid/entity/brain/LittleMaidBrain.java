package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.FollowOwnerTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.SitTask;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.util.math.intprovider.UniformIntProvider;

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
		brain.setTaskList(Activity.CORE, ImmutableList.of(
				Pair.of(0, new StayAboveWaterTask(0.8F)),
				Pair.of(1, new WalkTask(1.0F)),
				Pair.of(2, new LookAroundTask(45, 90)),
				Pair.of(3, new WanderAroundTask())
		));
	}

	private static void addSitTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.SIT, ImmutableList.of(
				Pair.of(0, new SitTask())
		), ImmutableSet.of(
				Pair.of(ModEntities.IS_SITTING, MemoryModuleState.VALUE_PRESENT)
		));
	}

	private static void addIdleTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.IDLE, ImmutableList.of(
				Pair.of(0, new FollowOwnerTask<>(10.0F, 2.0F)),
				Pair.of(1, new TimeLimitedTask<LivingEntity>(
						new FollowMobTask(EntityType.PLAYER, 6.0f), UniformIntProvider.create(30, 60))),
				Pair.of(2, new RandomTask<>(ImmutableList.of(
						Pair.of(new StrollTask(0.7F), 2),
						Pair.of(new GoTowardsLookTarget(0.7F, 3), 2),
						Pair.of(new WaitTask(30, 60), 1)
				)))
		));
	}

	public static void updateActivities(LittleMaidEntity littleMaid) {
		Brain<?> brain = littleMaid.getBrain();
		brain.resetPossibleActivities(ImmutableList.of(ModEntities.SIT, Activity.IDLE));
	}

	private LittleMaidBrain() throws IllegalAccessException {
		throw new IllegalAccessException();
	}
}
