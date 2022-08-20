package io.github.zemelua.umu_little_maid.mixin;

import net.minecraft.entity.ai.brain.task.CrossbowAttackTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CrossbowAttackTask.class)
public interface CrossbowAttackTaskAccessor {
	@Accessor
	void setState(CrossbowAttackTask.CrossbowState value);

	@Accessor
	void setChargingCooldown(int value);
}
