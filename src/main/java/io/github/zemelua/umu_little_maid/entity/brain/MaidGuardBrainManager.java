package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.brain.task.KeepAroundHomeOrAnchorTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.ShelterFromRainTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.ForgetShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.MaidEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.RememberShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.guard.ForgetGuardTargetTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.guard.MaidGuardTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.guard.MaidHeadbuttTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.guard.RememberGuardTargetTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.ForgetShouldSleepTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.RememberShouldSleepTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.UpdateSleepPosTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.tameable.FollowMasterTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.tameable.SitTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.tameable.TeleportToMasterTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.wander.AvoidRainStrollTask;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public final class MaidGuardBrainManager {
	public static void initBrain(Brain<LittleMaidEntity> brain) {
		MaidGuardBrainManager.addCoreTasks(brain);
		MaidGuardBrainManager.addIdleTasks(brain);
		MaidGuardBrainManager.addSitTasks(brain);
		MaidGuardBrainManager.addEatTasks(brain);
		MaidGuardBrainManager.addGuardTasks(brain);

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
	}

	public static void tickBrain(Brain<LittleMaidEntity> brain) {
		brain.resetPossibleActivities(ImmutableList.of(ModActivities.ACTIVITY_SIT, ModActivities.ACTIVITY_EAT, Activity.REST, ModActivities.ACTIVITY_GUARD, Activity.IDLE));
	}

	public static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, ImmutableList.of(
				Pair.of(0, new StayAboveWaterTask(0.8F)),
				Pair.of(0, new OpenDoorsTask()),
				Pair.of(0, new WakeUpTask()),
				Pair.of(0, new KeepAroundHomeOrAnchorTask()),
				Pair.of(1, new LookAroundTask(45, 90)),
				Pair.of(2, new WanderAroundTask()),
				Pair.of(98, new RememberShouldEatTask(living -> living.getBrain().hasMemoryModule(ModMemories.GUARD_AGAINST))),
				Pair.of(98, new RememberShouldSleepTask<>(12000L)),
				Pair.of(98, new RememberGuardTargetTask()),
				Pair.of(99, new ForgetShouldEatTask(living -> living.getBrain().hasMemoryModule(ModMemories.GUARD_AGAINST))),
				Pair.of(99, new ForgetShouldSleepTask<>(12000L)),
				Pair.of(99, new ForgetGuardTargetTask<>()),
				Pair.of(99, new UpdateSleepPosTask())
		));
	}

	public static void addIdleTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.IDLE, ImmutableList.of(
				Pair.of(0, new FollowMasterTask<>(10.0F)),
				Pair.of(0, new TeleportToMasterTask<>(15.0F)),
				Pair.of(0, new ShelterFromRainTask<>()),
				Pair.of(1, new TimeLimitedTask<LivingEntity>(new FollowMobTask(EntityType.PLAYER, 6.0F), UniformIntProvider.create(30, 60))),
				Pair.of(2, new RandomTask<>(ImmutableList.of(
						Pair.of(new AvoidRainStrollTask(0.8F), 2),
						Pair.of(new GoTowardsLookTarget(0.8F, 3), 2),
						Pair.of(new WaitTask(30, 60), 1)
				)))
		));
	}

	public static void addSitTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.ACTIVITY_SIT, ImmutableList.of(
				Pair.of(0, new SitTask())
		), ImmutableSet.of(
				Pair.of(ModMemories.IS_SITTING, MemoryModuleState.VALUE_PRESENT)
		));
	}

	public static void addEatTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.ACTIVITY_EAT, ImmutableList.of(
				Pair.of(0, new MaidEatTask())
		), ImmutableSet.of(
				Pair.of(ModMemories.SHOULD_EAT, MemoryModuleState.VALUE_PRESENT)
		), ImmutableSet.of(ModMemories.SHOULD_EAT));
	}

	public static void addGuardTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.ACTIVITY_GUARD, ImmutableList.of(
				Pair.of(1, new MaidGuardTask<>(6.0D, 2.0D, 1.5F)),
				Pair.of(0, new MaidHeadbuttTask())
		), ImmutableSet.of(
				Pair.of(ModMemories.GUARD_AGAINST, MemoryModuleState.VALUE_PRESENT)
		), ImmutableSet.of(
				ModMemories.GUARD_AGAINST
		));
	}

	private MaidGuardBrainManager() throws IllegalAccessException {
		throw new IllegalAccessException();
	}
}
