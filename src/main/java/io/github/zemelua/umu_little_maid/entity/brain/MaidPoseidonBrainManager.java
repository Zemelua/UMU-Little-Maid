package io.github.zemelua.umu_little_maid.entity.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.PoseidonFollowOwnerTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.SitTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.attack.trident.GoGetTridentTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.attack.trident.RiptideTridentTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.attack.trident.ThrowTridentTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.attack.trident.TridentApproachTargetTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.ForgetShouldEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.MaidEatTask;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.RememberShouldEatTask;
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
	public static void initializeBrain(Brain<LittleMaidEntity> brain) {
		MaidPoseidonBrainManager.addCoreTasks(brain);
		MaidPoseidonBrainManager.addIdleTasks(brain);
		MaidPoseidonBrainManager.addSitTasks(brain);
		MaidPoseidonBrainManager.addEatTasks(brain);
		MaidPoseidonBrainManager.addGoGetTridentTasks(brain);
		MaidPoseidonBrainManager.addFightTasks(brain);

		brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
		brain.setDefaultActivity(Activity.IDLE);
		brain.resetPossibleActivities();
	}

	public static void tickBrain(Brain<LittleMaidEntity> brain) {
		UMULittleMaid.LOGGER.info(brain.getFirstPossibleNonCoreActivity());

		brain.resetPossibleActivities(ImmutableList.of(
				ModEntities.ACTIVITY_SIT,
				ModEntities.ACTIVITY_EAT,
				ModEntities.ACTIVITY_GO_GET_TRIDENT,
				Activity.FIGHT,
				Activity.IDLE
		));
	}

	public static void addCoreTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(Activity.CORE, ImmutableList.of(
				// Pair.of(0, new StayAboveWaterTask(0.8F)),
				Pair.of(0, new OpenDoorsTask()),
				Pair.of(0, new WakeUpTask()),
				Pair.of(1, new LookAroundTask(45, 90)),
				Pair.of(2, new WanderAroundTask()),
				Pair.of(98, new RememberShouldEatTask(living -> living.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET))),
				Pair.of(98, new UpdateAttackTargetTask<>(living -> living.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
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
				Pair.of(0, new PoseidonFollowOwnerTask<>(10.0F, 2.0F))
		));
	}

	public static void addSitTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_SIT, ImmutableList.of(
				Pair.of(0, new SitTask<>())
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_IS_SITTING, MemoryModuleState.VALUE_PRESENT)
		));
	}

	public static void addEatTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_EAT, ImmutableList.of(
				Pair.of(0, new MaidEatTask())
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_SHOULD_EAT, MemoryModuleState.VALUE_PRESENT)
		), ImmutableSet.of(ModEntities.MEMORY_SHOULD_EAT));
	}

	public static void addGoGetTridentTasks(Brain<LittleMaidEntity> brain) {
		brain.setTaskList(ModEntities.ACTIVITY_GO_GET_TRIDENT, ImmutableList.of(
				Pair.of(0, new GoGetTridentTask<>())
		), ImmutableSet.of(
				Pair.of(ModEntities.MEMORY_THROWN_TRIDENT, MemoryModuleState.VALUE_PRESENT),
				Pair.of(ModEntities.MEMORY_THROWN_TRIDENT_COOLDOWN, MemoryModuleState.VALUE_PRESENT)
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
