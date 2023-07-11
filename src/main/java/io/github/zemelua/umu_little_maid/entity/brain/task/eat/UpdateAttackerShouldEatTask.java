package io.github.zemelua.umu_little_maid.entity.brain.task.eat;

import io.github.zemelua.umu_little_maid.entity.maid.ILittleMaidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;

public class UpdateAttackerShouldEatTask<E extends LivingEntity & ILittleMaidEntity> extends UpdateShouldEatTask<E> {
	@Override
	protected boolean shouldEat(E maid) {
		if (maid.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) {
			return maid.getHealth() < maid.getMaxHealth() * 0.4F;
		} else {
			return maid.getHealth() < maid.getMaxHealth();
		}
	}
}
