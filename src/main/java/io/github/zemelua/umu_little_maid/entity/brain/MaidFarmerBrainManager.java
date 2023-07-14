package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.brain.task.KeepAroundHomeOrAnchorTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.ShelterFromRainTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.MaidEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.UpdateShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.MaidFarmTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.UpdateFarmPosTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.WalkToFarmPosTask;
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
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public final class MaidFarmerBrainManager {
	public static void initBrain(Brain<LittleMaidEntity> brain) {
		addCoreTasks(brain);
		addIdleTasks(brain);
		addSitTasks(brain);
		addEatTasks(brain);
		addFarmTasks(brain);
		addSleepTasks(brain);

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
	}

	public static void tickBrain(Brain<LittleMaidEntity> brain) {
		brain.resetPossibleActivities(ImmutableList.of(ModActivities.ACTIVITY_SIT, ModActivities.ACTIVITY_EAT, Activity.REST, ModActivities.ACTIVITY_FARM, Activity.IDLE));
	}

	private static void addCoreTasks(Brain<LittleMaidEntity> brain) {
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
				Pair.of(99, new UpdateFarmPosTask<>())
		));
	}

	@SuppressWarnings("deprecation")
	private static void addIdleTasks(Brain<LittleMaidEntity> brain) {
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

	private static void addSitTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.ACTIVITY_SIT, ImmutableList.of(
				Pair.of(0, new SitTask())
		), ImmutableSet.of(
				Pair.of(ModMemories.IS_SITTING, MemoryModuleState.VALUE_PRESENT)
		));
	}

	private static void addEatTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.ACTIVITY_EAT, ImmutableList.of(
				Pair.of(0, new MaidEatTask())
		), ImmutableSet.of(
				Pair.of(ModMemories.SHOULD_EAT, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
		), ImmutableSet.of(ModMemories.SHOULD_EAT));
	}

	private static void addFarmTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.ACTIVITY_FARM, ImmutableList.of(
				Pair.of(0, new MaidFarmTask()),
				Pair.of(1, new WalkToFarmPosTask<>(0.8F))
		), ImmutableSet.of(
				Pair.of(ModMemories.FARM_POS, MemoryModuleState.VALUE_PRESENT),
				// Pair.of(ModEntities.MEMORY_FARM_COOLDOWN, MemoryModuleState.VALUE_ABSENT),
				Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
		), ImmutableSet.of(ModMemories.FARM_POS, ModMemories.FARM_COOLDOWN));
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

	private MaidFarmerBrainManager() throws IllegalAccessException {
		throw new IllegalAccessException();
	}
}
