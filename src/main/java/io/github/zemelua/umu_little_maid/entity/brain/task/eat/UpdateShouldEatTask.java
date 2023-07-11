package io.github.zemelua.umu_little_maid.entity.brain.task.eat;

import io.github.zemelua.umu_little_maid.api.EveryTickTask;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.ILittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.server.world.ServerWorld;

public class UpdateShouldEatTask<E extends LivingEntity & ILittleMaidEntity> extends EveryTickTask<E> {
	@Override
	public void tick(ServerWorld world, E maid, long time) {
		Brain<?> brain = maid.getBrain();

		if (!maid.hasSugar()) {
			brain.forget(ModMemories.SHOULD_EAT);
			return;
		}

		if (this.shouldEat(maid)) {
			ModUtils.Brains.remember(brain, ModMemories.SHOULD_EAT);
		} else {
			brain.forget(ModMemories.SHOULD_EAT);
		}
	}

	protected boolean shouldEat(E maid) {
		return maid.getHealth() < maid.getMaxHealth();
	}
}
