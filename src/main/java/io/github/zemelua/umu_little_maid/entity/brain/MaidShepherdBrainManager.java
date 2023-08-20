package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.entity.brain.task.KeepAroundHomeOrAnchorTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.ShelterFromRainTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.MaidEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.UpdateShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.deliver.MaidDeliverTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.deliver.MaidWalkToBoxTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.look.LookAtEntityTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.shear.MaidShearTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.shear.UpdateShearTargetTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.shear.WalkToShearTargetTask;
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

public final class MaidShepherdBrainManager {
    public static void initBrain(Brain<LittleMaidEntity> brain, LittleMaidEntity maid) {
        addCoreTasks(brain);
        addIdleTasks(brain);
        addSitTasks(brain);
        addEatTasks(brain);
        addShearTasks(brain);
        addSleepTasks(brain);

        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
    }

    public static void tickBrain(Brain<LittleMaidEntity> brain, LittleMaidEntity maid) {
        ImmutableList.Builder<Activity> activities = ImmutableList.builder();
        activities.add(ModActivities.SIT);
        activities.add(ModActivities.EAT);
        activities.add(Activity.REST);
        activities.add(ModActivities.SHEAR);
        if (maid.hasHarvests()) {
            activities.add(ModActivities.SHEAR);
        }
        activities.add(Activity.IDLE);

        brain.resetPossibleActivities(activities.build());
    }

    private static void addCoreTasks(Brain<LittleMaidEntity> brain) {
        brain.setTaskList(Activity.CORE, ImmutableList.of(
                Pair.of(0, new StayAboveWaterTask(0.8F)),
                Pair.of(0, OpenDoorsTask.create()),
                Pair.of(0, WakeUpTask.create()),
                Pair.of(1, new LookAroundTask(45, 90)),
                Pair.of(1, new WanderAroundTask()),
                Pair.of(99, new UpdateShouldEatTask<>()),
                Pair.of(99, new UpdateShouldSleepTask<>()),
                Pair.of(99, new UpdateSleepPosTask()),
                Pair.of(99, new UpdateShearTargetTask<>())
                // Pair.of(99, new UpdateDeliveryBoxTask<>())
        ));
    }

    private static void addIdleTasks(Brain<LittleMaidEntity> brain) {
        brain.setTaskList(Activity.IDLE, ImmutableList.of(
                Pair.of(0, new FollowMasterTask<>(10.0F)),
                Pair.of(0, new TeleportToMasterTask<>(15.0F)),
                Pair.of(0, new KeepAroundHomeOrAnchorTask()),
                Pair.of(1, new ShelterFromRainTask<>()),
                Pair.of(2, new RandomTask<>(ImmutableList.of(
                        Pair.of(StrollTask.create(0.8F), 2),
                        Pair.of(GoTowardsLookTargetTask.create(0.8F, 3), 2),
                        Pair.of(new WaitTask(30, 60), 1)
                ))),
                Pair.of(2, new RandomTask<>(ImmutableList.of(
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
        ));
    }

    private static void addSitTasks(Brain<LittleMaidEntity> brain) {
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

    private static void addEatTasks(Brain<LittleMaidEntity> brain) {
        brain.setTaskList(ModActivities.EAT, ImmutableList.of(
                Pair.of(0, new MaidEatTask())
        ), ImmutableSet.of(
                Pair.of(ModMemories.SHOULD_EAT, MemoryModuleState.VALUE_PRESENT),
                Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
        ), ImmutableSet.of(ModMemories.SHOULD_EAT));
    }

    private static void addShearTasks(Brain<LittleMaidEntity> brain) {
        brain.setTaskList(ModActivities.SHEAR, ImmutableList.of(
                Pair.of(0, new MaidShearTask<>()),
                Pair.of(1, new WalkToShearTargetTask<>())
        ), ImmutableSet.of(
                Pair.of(ModMemories.SHEAR_TARGET, MemoryModuleState.VALUE_PRESENT),
                Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
        ));
    }

    private static void addDeliverTasks(Brain<LittleMaidEntity> brain) {
        brain.setTaskList(ModActivities.DELIVER, ImmutableList.of(
                Pair.of(0, new MaidDeliverTask<>(ModTags.ITEM_MAID_SHEPHERD_DELIVERS)),
                Pair.of(1, new MaidWalkToBoxTask<>())
        ), ImmutableSet.of(
                Pair.of(ModMemories.SHEAR_TARGET, MemoryModuleState.VALUE_ABSENT),
                Pair.of(ModMemories.DELIVERY_BOX, MemoryModuleState.VALUE_PRESENT),
                Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
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

    private MaidShepherdBrainManager() {}
}
