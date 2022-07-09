package io.github.zemelua.umu_little_maid.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Task.class)
public interface TaskAccessor<E extends LivingEntity> {
	@Invoker
	boolean callShouldRun(ServerWorld world, E entity);

	@Invoker
	boolean callShouldKeepRunning(ServerWorld world, E entity, long time);

	@Invoker
	void callRun(ServerWorld world, E entity, long time);

	@Invoker
	void callKeepRunning(ServerWorld world, E entity, long time);

	@Invoker
	void callFinishRunning(ServerWorld world, E entity, long time);

	@Invoker
	boolean callIsTimeLimitExceeded(long time);
}
