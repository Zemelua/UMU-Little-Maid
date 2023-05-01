package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.KeepAroundHomeOrAnchorTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.ShelterFromRainTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.ForgetShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.MaidEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.RememberShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.heal.ForgetShouldHealTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.heal.RememberShouldHealTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.look.LookAtBlockTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.look.LookAtEntityTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.*;
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
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.math.intprovider.UniformIntProvider;

import static io.github.zemelua.umu_little_maid.entity.ModEntities.*;

public final class MaidShepherdBrainManager {
	public static void initializeBrain(Brain<LittleMaidEntity> brain) {
		addCoreTasks(brain);
		addIdleTasks(brain);
		addSitTasks(brain);
		addEatTasks(brain);
		addSleepTasks(brain);

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
	}

	public static void tickBrain(Brain<LittleMaidEntity> brain) {
		brain.resetPossibleActivities(ImmutableList.of(ModEntities.ACTIVITY_SIT, ModEntities.ACTIVITY_EAT, Activity.REST, Activity.IDLE));
	}

	public static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, ImmutableList.of(
				Pair.of(0, new StayAboveWaterTask(0.8F)),
				Pair.of(0, new OpenDoorsTask()),
				Pair.of(0, new WakeUpTask()),
				Pair.of(0, new KeepAroundHomeOrAnchorTask()),
				Pair.of(1, new LookAroundTask(45, 90)),
				Pair.of(2, new WanderAroundTask()),
				Pair.of(98, new RememberShouldEatTask()),
				Pair.of(98, new RememberShouldSleepTask<>(12000L)),
				Pair.of(98, new RememberShouldHealTask<>()),
				Pair.of(99, new ForgetShouldEatTask()),
				Pair.of(99, new ForgetShouldSleepTask<>(12000L)),
				Pair.of(99, new ForgetShouldHealTask<>())
		));
	}

	public static void addIdleTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.IDLE, ImmutableList.of(
				Pair.of(0, new FollowMasterTask<>(10.0F)),
				Pair.of(0, new TeleportToMasterTask<>(15.0F)),
				Pair.of(1, new ShelterFromRainTask<>()),
				Pair.of(2, new TimeLimitedTask<LivingEntity>(new FollowMobTask(EntityType.PLAYER, 6.0F), UniformIntProvider.create(30, 60))),
				Pair.of(3, new RandomTask<>(ImmutableList.of(
						Pair.of(new AvoidRainStrollTask(0.8F), 2),
						Pair.of(new GoTowardsLookTarget(0.8F, 3), 2),
						Pair.of(new WaitTask(30, 60), 4)
				))),
				Pair.of(3, new RandomTask<>(ImmutableList.of(
						Pair.of(new LookAtEntityTask<>((self, target) -> target.equals(self.getMaster().orElse(null))), 1),
						Pair.of(new LookAtEntityTask<>((self, target) -> target instanceof SheepEntity), 1),
						Pair.of(new LookAtBlockTask<>(POI_BANNER), 2),
						Pair.of(new WaitTask(30, 60), 4)
				)))
		));
	}

	public static void addShearSheepTasks(Brain<LittleMaidEntity> brain) {

	}

	public static void addSitTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_SIT, ImmutableList.of(
				Pair.of(0, new SitTask())
		), ImmutableSet.of(
				Pair.of(ModMemories.IS_SITTING, MemoryModuleState.VALUE_PRESENT)
		));
	}

	public static void addEatTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_EAT, ImmutableList.of(
				Pair.of(0, new MaidEatTask())
		), ImmutableSet.of(
				Pair.of(ModMemories.SHOULD_EAT, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT)
		), ImmutableSet.of(ModMemories.SHOULD_EAT));
	}

	public static void addSleepTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.REST, ImmutableList.of(
				Pair.of(0, new SleepTask()),
				Pair.of(1, new WalkToHomeTask<>(0.8F))
		), ImmutableSet.of(
				Pair.of(ModMemories.SHOULD_SLEEP, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.HOME, MemoryModuleState.VALUE_PRESENT),
				Pair.of(ModMemories.SHOULD_HEAL, MemoryModuleState.VALUE_ABSENT)
		));
	}

	@Deprecated
	private MaidShepherdBrainManager() {
		UMULittleMaid.LOGGER.warn(ModEntities.MARKER, "MaidShepherdBrainManager はインスタンス化しても意味ないよ！");
	}
}
