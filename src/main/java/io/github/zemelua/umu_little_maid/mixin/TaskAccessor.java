package io.github.zemelua.umu_little_maid.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Task.class)
public interface TaskAccessor<E extends LivingEntity> {
	@Accessor long getEndTime();

	@Invoker boolean callHasRequiredMemoryState(E entity);
}
