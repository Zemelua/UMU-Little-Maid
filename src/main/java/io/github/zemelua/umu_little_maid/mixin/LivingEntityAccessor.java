package io.github.zemelua.umu_little_maid.mixin;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
	@Accessor
	void setRiptideTicks(int riptideTicks);
}
