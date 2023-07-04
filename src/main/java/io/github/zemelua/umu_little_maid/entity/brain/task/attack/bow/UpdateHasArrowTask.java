package io.github.zemelua.umu_little_maid.entity.brain.task.attack.bow;

import io.github.zemelua.umu_little_maid.api.EveryTickTask;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.server.world.ServerWorld;

public class UpdateHasArrowTask<E extends LittleMaidEntity> extends EveryTickTask<E> {
	@Override
	public void tick(ServerWorld world, E entity, long time) {
		Brain<?> brain = entity.getBrain();

		if (entity.hasArrow()) {
			ModUtils.Brains.remember(brain, ModMemories.HAS_ARROWS);
		} else {
			brain.forget(ModMemories.HAS_ARROWS);
		}
	}
}
