package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.brain.task.KeepAroundHomeOrAnchorTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.ShelterFromRainTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.MaidEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.UpdateShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.heal.ApproachToHealTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.heal.HealMasterTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.heal.UpdateShouldHealTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.look.LookAtEntityTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.MaidSleepTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.UpdateShouldSleepTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.UpdateSleepPosTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.WalkToSleepPosTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.tameable.FollowMasterTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.tameable.SitTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.tameable.TeleportToMasterTask;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public final class MaidHealerBrainManager {
	public static void initBrain(Brain<LittleMaidEntity> brain, LittleMaidEntity maid) {
		MaidHealerBrainManager.addCoreTasks(brain);
		MaidHealerBrainManager.addIdleTasks(brain);
		MaidHealerBrainManager.addSitTasks(brain);
		MaidHealerBrainManager.addEatTasks(brain);
		MaidHealerBrainManager.addHealTasks(brain);
		MaidHealerBrainManager.addSleepTasks(brain);

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
	}

	public static void tickBrain(Brain<LittleMaidEntity> brain, LittleMaidEntity maid) {
		brain.resetPossibleActivities(ImmutableList.of(ModActivities.SIT, ModActivities.EAT, Activity.REST, ModActivities.HEAL, Activity.IDLE));
	}

	public static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, ImmutableList.of(
				Pair.of(0, new StayAboveWaterTask(0.8F)),
				Pair.of(0, OpenDoorsTask.create()),
				Pair.of(0, WakeUpTask.create()),
				Pair.of(0, new KeepAroundHomeOrAnchorTask()),
				Pair.of(1, new FleeTask(1.2F)), // パニック。攻撃されたときのはHURT_BYで判定されています
				Pair.of(2, new LookAroundTask(45, 90)),
				Pair.of(3, new WanderAroundTask()),
				Pair.of(99, new UpdateShouldEatTask<>()),
				Pair.of(99, new UpdateShouldSleepTask<>()),
				Pair.of(99, new UpdateSleepPosTask()),
				Pair.of(99, new UpdateShouldHealTask())
		));
	}

	@SuppressWarnings("deprecation")
	public static void addIdleTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.IDLE, ImmutableList.of(
				Pair.of(0, new FollowMasterTask<>(10.0F)),
				Pair.of(0, new TeleportToMasterTask<>(15.0F)),
				Pair.of(1, new ShelterFromRainTask<>()),
				Pair.of(2, LookAtMobWithIntervalTask.follow(EntityType.PLAYER, 6.0f, UniformIntProvider.create(30, 60))),
				Pair.of(3, new RandomTask<>(ImmutableList.of(
						Pair.of(StrollTask.create(0.8F), 2),
						Pair.of(GoTowardsLookTargetTask.create(0.8F, 3), 2),
						Pair.of(new WaitTask(30, 60), 1)
				))),
				Pair.of(3, new RandomTask<>(ImmutableList.of(
						Pair.of(new LookAtEntityTask<>((self, target) -> target.equals(self.getMaster().orElse(null))), 1),
						Pair.of(new WaitTask(30, 60), 4)
				)))
		));
	}

	public static void addSitTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.SIT, ImmutableList.of(
				Pair.of(0, new SitTask()),
				Pair.of(5, new RandomTask<>(ImmutableList.of(
						Pair.of(new LookAtEntityTask<>((self, target) -> target.equals(self.getMaster().orElse(null))), 8),
						Pair.of(LookAtMobTask.create(EntityType.VILLAGER, 8.0f), 1),
						Pair.of(LookAtMobTask.create(EntityType.PLAYER, 8.0f), 1),
						Pair.of(LookAtMobTask.create(SpawnGroup.CREATURE, 8.0f), 3),
						Pair.of(LookAtMobTask.create(SpawnGroup.WATER_CREATURE, 8.0f), 1),
						Pair.of(LookAtMobTask.create(SpawnGroup.AXOLOTLS, 8.0f), 2),
						Pair.of(LookAtMobTask.create(SpawnGroup.UNDERGROUND_WATER_CREATURE, 8.0f), 1),
						Pair.of(LookAtMobTask.create(SpawnGroup.WATER_AMBIENT, 8.0f), 1),
						Pair.of(new WaitTask(30, 60), 5)
				)))
		), ImmutableSet.of(
				Pair.of(ModMemories.IS_SITTING, MemoryModuleState.VALUE_PRESENT)
		));
	}

	public static void addEatTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.EAT, ImmutableList.of(
				Pair.of(0, new MaidEatTask())
		), ImmutableSet.of(
				Pair.of(ModMemories.SHOULD_EAT, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
		), ImmutableSet.of(ModMemories.SHOULD_EAT));
	}

	public static void addHealTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.HEAL, ImmutableList.of(
				Pair.of(0, new HealMasterTask()),
				Pair.of(1, new ApproachToHealTask<>(1.0F))
		), ImmutableSet.of(
				Pair.of(ModMemories.SHOULD_HEAL, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
		), ImmutableSet.of(ModMemories.SHOULD_HEAL));
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

	private MaidHealerBrainManager() throws IllegalAccessException {
		throw new IllegalAccessException();
	}
}
