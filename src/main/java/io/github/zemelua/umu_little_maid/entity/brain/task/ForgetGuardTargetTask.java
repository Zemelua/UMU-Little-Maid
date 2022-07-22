package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ForgetGuardTargetTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_ATTRACT_TARGETS, MemoryModuleState.VALUE_PRESENT,
			ModEntities.MEMORY_GUARD_TARGET, MemoryModuleState.VALUE_PRESENT
	);

	public ForgetGuardTargetTask() {
		super(ForgetGuardTargetTask.REQUIRED_MEMORIES);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		Optional<List<LivingEntity>> attractTargets = brain.getOptionalMemory(ModEntities.MEMORY_ATTRACT_TARGETS);
		if (attractTargets.isEmpty()) {
			brain.forget(ModEntities.MEMORY_ATTRACT_TARGETS);
		} else {
			attractTargets.get().removeIf(target -> !target.isAlive() || target.getWorld() != target.getWorld());
		}

		Optional<LivingEntity> guardTarget = brain.getOptionalMemory(ModEntities.MEMORY_GUARD_TARGET);
		if (guardTarget.isEmpty()) {
			brain.forget(ModEntities.MEMORY_GUARD_TARGET);
		} else {
			if (!guardTarget.get().isAlive() || guardTarget.get().getWorld() != living.getWorld()) {
				brain.forget(ModEntities.MEMORY_GUARD_TARGET);
			}
		}
	}
}
