package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public class ForcedForgetMemoryTask<E extends LivingEntity, M> extends Task<E> {
	private final MemoryModuleType<M> memory;

	public ForcedForgetMemoryTask(MemoryModuleType<M> memory) {
		super(ImmutableMap.of(memory, MemoryModuleState.VALUE_PRESENT));

		this.memory = memory;
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		Brain<?> brain = entity.getBrain();

		brain.forget(this.memory);
	}
}
