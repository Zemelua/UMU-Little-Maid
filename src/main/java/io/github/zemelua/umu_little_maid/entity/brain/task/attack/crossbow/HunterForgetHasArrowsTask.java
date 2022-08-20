package io.github.zemelua.umu_little_maid.entity.brain.task.attack.crossbow;

import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.attack.bow.ForgetHasArrowsTask;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.server.world.ServerWorld;

public class HunterForgetHasArrowsTask<E extends LivingEntity> extends ForgetHasArrowsTask<E> {
	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		if (!HunterRememberHasArrowsTask.hasArrow(living)) {
			brain.forget(ModEntities.MEMORY_HAS_ARROWS);
		}
	}
}
