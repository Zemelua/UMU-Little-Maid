package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ForgetGuardTargetTask extends Task<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_ATTRACT_TARGETS, MemoryModuleState.VALUE_PRESENT,
			ModEntities.MEMORY_GUARD_TARGET, MemoryModuleState.VALUE_PRESENT
	);

	public ForgetGuardTargetTask() {
		super(ForgetGuardTargetTask.REQUIRED_MEMORIES);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		Optional<UUID> ownerUUID = brain.getOptionalMemory(ModEntities.MEMORY_OWNER);
		if (ownerUUID.isEmpty()) {
			brain.forget(ModEntities.MEMORY_ATTRACT_TARGETS);
			brain.forget(ModEntities.MEMORY_GUARD_TARGET);
		} else {
			@Nullable PlayerEntity owner = world.getPlayerByUuid(ownerUUID.get());
			if (owner == null) {
				brain.forget(ModEntities.MEMORY_ATTRACT_TARGETS);
				brain.forget(ModEntities.MEMORY_GUARD_TARGET);
			}
		}

		Optional<List<LivingEntity>> attractTargets = brain.getOptionalMemory(ModEntities.MEMORY_ATTRACT_TARGETS);
		if (attractTargets.isEmpty()) {
			brain.forget(ModEntities.MEMORY_ATTRACT_TARGETS);
		} else {
			attractTargets.get().removeIf(living -> !living.isAlive() || living.getWorld() != maid.getWorld());
		}

		Optional<LivingEntity> guardTarget = brain.getOptionalMemory(ModEntities.MEMORY_GUARD_TARGET);
		if (guardTarget.isEmpty()) {
			brain.forget(ModEntities.MEMORY_GUARD_TARGET);
		} else {
			if (!guardTarget.get().isAlive() || guardTarget.get().getWorld() != maid.getWorld()) {
				brain.forget(ModEntities.MEMORY_GUARD_TARGET);
			}
		}
	}
}
