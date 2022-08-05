package io.github.zemelua.umu_little_maid.mixin;

import net.minecraft.entity.ai.control.MoveControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MoveControl.class)
public interface MoveControlAccessor {
	@Accessor
	void setSpeed(double value);
}
