package io.github.zemelua.umu_little_maid.entity.brain.task.guard;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;
import java.util.Optional;

public class ForgetGuardTargetTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModMemories.GUARD_AGAINST, MemoryModuleState.VALUE_PRESENT
	);

	public ForgetGuardTargetTask() {
		super(ForgetGuardTargetTask.REQUIRED_MEMORIES, 0);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		Optional<LivingEntity> target = brain.getOptionalMemory(ModMemories.GUARD_AGAINST);
		if (target.isEmpty()) {
			brain.forget(ModMemories.GUARD_AGAINST);
		} else {
			if (!target.get().isAlive() || target.get().getWorld() != living.getWorld() || target.get().distanceTo(living) > 16.0D) {
				brain.forget(ModMemories.GUARD_AGAINST);
			}
		}
	}
}
