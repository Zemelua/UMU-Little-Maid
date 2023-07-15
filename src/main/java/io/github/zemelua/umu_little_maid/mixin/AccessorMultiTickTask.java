package io.github.zemelua.umu_little_maid.mixin;

import net.minecraft.entity.ai.brain.task.MultiTickTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MultiTickTask.class)
public interface AccessorMultiTickTask {
	@Accessor
	long getEndTime();
}
