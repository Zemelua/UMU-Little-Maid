package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.brain.task.KeepAroundHomeOrAnchorTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.MaidEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.UpdateGuardShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.UpdateShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.guard.ForgetGuardTargetTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.guard.MaidGuardTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.guard.HeadbuttTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.guard.RememberGuardTargetTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.MaidSleepTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.UpdateShouldSleepTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.UpdateSleepPosTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.WalkToSleepPosTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.tameable.SitTask;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.task.*;

public final class MaidGuardBrainManager {
	public static void initBrain(Brain<LittleMaidEntity> brain, LittleMaidEntity maid) {
		addCoreTasks(brain);
		addIdleTasks(brain);
		addSitTasks(brain);
		addEatTasks(brain);
		addGuardTasks(brain);
		addSleepTasks(brain);

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
	}

	public static void tickBrain(Brain<LittleMaidEntity> brain, LittleMaidEntity maid) {
		brain.resetPossibleActivities(ImmutableList.of(ModActivities.SIT, ModActivities.EAT, Activity.REST, ModActivities.GUARD, Activity.IDLE));
	}

	public static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, ImmutableList.of(
				Pair.of(0, new StayAboveWaterTask(0.8F)),
				Pair.of(0, OpenDoorsTask.create()),
				Pair.of(0, WakeUpTask.create()),
				Pair.of(0, new KeepAroundHomeOrAnchorTask()),
				Pair.of(1, new LookAroundTask(45, 90)),
				Pair.of(2, new WanderAroundTask()),
				Pair.of(99, new UpdateShouldEatTask<>()),
				Pair.of(99, new UpdateShouldSleepTask<>()),
				Pair.of(99, new UpdateSleepPosTask()),
				Pair.of(98, new RememberGuardTargetTask()),
				Pair.of(99, new ForgetGuardTargetTask<>()),
				Pair.of(99, new UpdateSleepPosTask()),
				Pair.of(99, new UpdateGuardShouldEatTask<>())
		));
	}

	public static void addIdleTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.IDLE, ImmutableList.of(
				Pair.of(0, new StayAboveWaterTask(0.8F)),
				Pair.of(0, OpenDoorsTask.create()),
				Pair.of(0, WakeUpTask.create()),
				Pair.of(0, new KeepAroundHomeOrAnchorTask()),
				Pair.of(1, new FleeTask(1.2F)), // パニック。攻撃されたときのはHURT_BYで判定されています
				Pair.of(2, new LookAroundTask(45, 90)),
				Pair.of(3, new WanderAroundTask()),
				Pair.of(99, new UpdateShouldEatTask<>()),
				Pair.of(99, new UpdateShouldSleepTask<>()),
				Pair.of(99, new UpdateSleepPosTask())
		));
	}

	public static void addSitTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.SIT, ImmutableList.of(
				Pair.of(0, new SitTask())
		), ImmutableSet.of(
				Pair.of(ModMemories.IS_SITTING, MemoryModuleState.VALUE_PRESENT)
		));
	}

	public static void addEatTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.EAT, ImmutableList.of(
				Pair.of(0, new MaidEatTask())
		), ImmutableSet.of(
				Pair.of(ModMemories.SHOULD_EAT, MemoryModuleState.VALUE_PRESENT)
		), ImmutableSet.of(ModMemories.SHOULD_EAT));
	}

	public static void addGuardTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.GUARD, ImmutableList.of(
				Pair.of(1, new MaidGuardTask<>(6.0D, 2.0D, 1.5F)),
				Pair.of(0, new HeadbuttTask())
		), ImmutableSet.of(
				Pair.of(ModMemories.GUARD_AGAINST, MemoryModuleState.VALUE_PRESENT)
		), ImmutableSet.of(
				ModMemories.GUARD_AGAINST
		));
	}

	public static void addSleepTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.REST, ImmutableList.of(
				Pair.of(0, new MaidSleepTask()),
				Pair.of(1, new WalkToSleepPosTask<>())
		), ImmutableSet.of(
				Pair.of(ModMemories.SHOULD_SLEEP, MemoryModuleState.VALUE_PRESENT),
				Pair.of(ModMemories.SLEEP_POS, MemoryModuleState.VALUE_PRESENT)
		));
	}

	private MaidGuardBrainManager() throws IllegalAccessException {
		throw new IllegalAccessException();
	}
}
