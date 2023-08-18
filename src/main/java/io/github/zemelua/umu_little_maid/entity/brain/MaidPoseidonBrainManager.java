package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.brain.task.KeepAroundHomeOrAnchorTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.attack.trident.*;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.MaidEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.UpdateAttackerShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.look.LookAtEntityTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.sleep.UpdateSleepPosTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.swim.BreathAirTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.swim.ForgetShouldBreathTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.swim.RememberShouldBreathTask;
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

public final class MaidPoseidonBrainManager {
	public static void initBrain(Brain<LittleMaidEntity> brain, LittleMaidEntity maid) {
		MaidPoseidonBrainManager.addCoreTasks(brain);
		MaidPoseidonBrainManager.addIdleTasks(brain);
		MaidPoseidonBrainManager.addSitTasks(brain);
		MaidPoseidonBrainManager.addBreathTasks(brain);
		MaidPoseidonBrainManager.addEatTasks(brain);
		MaidPoseidonBrainManager.addGoGetTridentTasks(brain);
		MaidPoseidonBrainManager.addFightTasks(brain);

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
	}

	public static void tickBrain(Brain<LittleMaidEntity> brain, LittleMaidEntity maid) {
		brain.resetPossibleActivities(ImmutableList.of(
				ModActivities.SIT,
				ModActivities.BREATH,
				ModActivities.EAT,
				ModActivities.GO_GET_TRIDENT,
				Activity.FIGHT,
				Activity.IDLE
		));
	}

	public static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, ImmutableList.of(
				Pair.of(0, OpenDoorsTask.create()),
				Pair.of(0, WakeUpTask.create()),
				Pair.of(0, new KeepAroundHomeOrAnchorTask()),
				Pair.of(2, new LookAroundTask(45, 90)),
				Pair.of(3, new WanderAroundTask()),
				Pair.of(98, new RememberShouldBreathTask<>(100)),
				Pair.of(98, UpdateAttackTargetTask.create(living -> living.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
				Pair.of(99, new ForgetShouldBreathTask<>()),
				Pair.of(99, ForgetAttackTargetTask.create()),
				Pair.of(99, new UpdateSleepPosTask()),
				Pair.of(99, new UpdateAttackerShouldEatTask<>())
		));
	}

	@SuppressWarnings("deprecation")
	public static void addIdleTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.IDLE, ImmutableList.of(
				Pair.of(2, new RandomTask<>(ImmutableList.of(
						Pair.of(StrollTask.create(0.8F), 2),
						Pair.of(StrollTask.createDynamicRadius(0.75f), 2),
						Pair.of(new WaitTask(30, 60), 1)
				))),
				Pair.of(0, LookAtMobWithIntervalTask.follow(EntityType.PLAYER, 6.0f, UniformIntProvider.create(30, 60))),
				Pair.of(2, new FollowMasterTask<>(10.0F)),
				Pair.of(3, new TeleportToMasterTask<>(15.0F))
		));
	}

	public static void addSitTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.SIT, ImmutableList.of(
				Pair.of(0, new SitTask()),
				Pair.of(1, new StayAboveWaterTask(0.8F)),
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

	public static void addBreathTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.BREATH, ImmutableList.of(
				Pair.of(0, new BreathAirTask<>())
		), ImmutableSet.of(
				Pair.of(ModMemories.SHOULD_BREATH, MemoryModuleState.VALUE_PRESENT)
		));
	}

	public static void addEatTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.EAT, ImmutableList.of(
				Pair.of(0, new MaidEatTask())
		), ImmutableSet.of(
				Pair.of(ModMemories.SHOULD_EAT, MemoryModuleState.VALUE_PRESENT)
		), ImmutableSet.of(ModMemories.SHOULD_EAT));
	}

	public static void addGoGetTridentTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModActivities.GO_GET_TRIDENT, ImmutableList.of(
				Pair.of(0, new GoGetTridentTask<>())
		), ImmutableSet.of(
				Pair.of(ModMemories.THROWN_TRIDENT, MemoryModuleState.VALUE_PRESENT),
				Pair.of(ModMemories.THROWN_TRIDENT_COOLDOWN, MemoryModuleState.VALUE_PRESENT)
		));
	}

	public static void addFightTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.FIGHT, ImmutableList.of(
				Pair.of(0, new SpearTask()),
				Pair.of(1, new ThrowTridentTask<>(10.0D, 0.8D, 20)),
				Pair.of(1, new RiptideTridentTask<>(10.0D, 0.8D, 10)),
				Pair.of(2, TridentApproachTargetTask.create(1.0F))
		), ImmutableSet.of(
				Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT),
				Pair.of(MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleState.VALUE_ABSENT)
		), ImmutableSet.of(
				MemoryModuleType.ATTACK_TARGET
		));
	}

	private MaidPoseidonBrainManager() throws IllegalAccessException {
		throw new IllegalAccessException();
	}
}
