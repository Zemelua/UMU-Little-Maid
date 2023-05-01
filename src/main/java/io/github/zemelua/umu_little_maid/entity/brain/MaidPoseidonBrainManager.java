package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.KeepAroundHomeOrAnchorTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.attack.trident.GoGetTridentTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.attack.trident.RiptideTridentTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.attack.trident.ThrowTridentTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.attack.trident.TridentApproachTargetTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.ForgetShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.MaidEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.RememberShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.swim.BreathAirTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.swim.ForgetShouldBreathTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.swim.RememberShouldBreathTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.tameable.FollowMasterTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.tameable.SitTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.tameable.TeleportToMasterTask;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public final class MaidPoseidonBrainManager {
	public static void initBrain(Brain<LittleMaidEntity> brain) {
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

	public static void tickBrain(Brain<LittleMaidEntity> brain) {
		brain.resetPossibleActivities(ImmutableList.of(
				ModEntities.ACTIVITY_SIT,
				ModEntities.ACTIVITY_BREATH,
				ModEntities.ACTIVITY_EAT,
				ModEntities.ACTIVITY_GO_GET_TRIDENT,
				Activity.FIGHT,
				Activity.IDLE
		));
	}

	public static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, ImmutableList.of(
				Pair.of(0, new OpenDoorsTask()),
				Pair.of(0, new WakeUpTask()),
				Pair.of(0, new KeepAroundHomeOrAnchorTask()),
				Pair.of(2, new LookAroundTask(45, 90)),
				Pair.of(3, new WanderAroundTask()),
				Pair.of(98, new RememberShouldBreathTask<>(100)),
				Pair.of(98, new RememberShouldEatTask(living -> living.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET))),
				Pair.of(98, new UpdateAttackTargetTask<>(living -> living.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
				Pair.of(99, new ForgetShouldBreathTask<>()),
				Pair.of(99, new ForgetShouldEatTask(living -> living.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET))),
				Pair.of(99, new ForgetAttackTargetTask<>())
		));
	}

	public static void addIdleTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.IDLE, ImmutableList.of(
				Pair.of(1, new TimeLimitedTask<LivingEntity>(new FollowMobTask(EntityType.PLAYER, 6.0F), UniformIntProvider.create(30, 60))),
				Pair.of(2, new RandomTask<>(ImmutableList.of(
						Pair.of(new StrollTask(0.8F), 2),
						Pair.of(new AquaticStrollTask(0.8F), 2),
						Pair.of(new GoTowardsLookTarget(0.8F, 3), 2),
						Pair.of(new WaitTask(30, 60), 1)
				))),
				Pair.of(0, new FollowMasterTask<>(10.0F)),
				Pair.of(0, new TeleportToMasterTask<>(15.0F))
		));
	}

	public static void addSitTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_SIT, ImmutableList.of(
				Pair.of(0, new SitTask()),
				Pair.of(1, new StayAboveWaterTask(0.8F))
		), ImmutableSet.of(
				Pair.of(ModMemories.IS_SITTING, MemoryModuleState.VALUE_PRESENT)
		));
	}

	public static void addBreathTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_BREATH, ImmutableList.of(
				Pair.of(0, new BreathAirTask<>())
		), ImmutableSet.of(
				Pair.of(ModMemories.SHOULD_BREATH, MemoryModuleState.VALUE_PRESENT)
		));
	}

	public static void addEatTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_EAT, ImmutableList.of(
				Pair.of(0, new MaidEatTask())
		), ImmutableSet.of(
				Pair.of(ModMemories.SHOULD_EAT, MemoryModuleState.VALUE_PRESENT)
		), ImmutableSet.of(ModMemories.SHOULD_EAT));
	}

	public static void addGoGetTridentTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_GO_GET_TRIDENT, ImmutableList.of(
				Pair.of(0, new GoGetTridentTask<>())
		), ImmutableSet.of(
				Pair.of(ModMemories.THROWN_TRIDENT, MemoryModuleState.VALUE_PRESENT),
				Pair.of(ModMemories.THROWN_TRIDENT_COOLDOWN, MemoryModuleState.VALUE_PRESENT)
		));
	}

	public static void addFightTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.FIGHT, ImmutableList.of(
				Pair.of(0, new MeleeAttackTask(20)),
				Pair.of(1, new ThrowTridentTask<>(10.0D, 0.8D, 20)),
				Pair.of(1, new RiptideTridentTask<>(10.0D, 0.8D, 10)),
				Pair.of(2, new TridentApproachTargetTask(1.0F))
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
