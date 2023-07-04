package io.github.zemelua.umu_little_maid.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.MultiTickTask.Status;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public abstract class EveryTickTask<E extends LivingEntity> implements Task<E> {
	@Override
	public abstract void tick(ServerWorld world, E entity, long time);

	@Override
	public Status getStatus() {
		return Status.RUNNING;
	}

	@Override
	public boolean tryStarting(ServerWorld world, E entity, long time) {
		return true;
	}

	@Override
	public void stop(ServerWorld world, E entity, long time) {}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
}
