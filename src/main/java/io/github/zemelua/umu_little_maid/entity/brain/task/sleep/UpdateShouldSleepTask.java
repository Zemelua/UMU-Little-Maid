package io.github.zemelua.umu_little_maid.entity.brain.task.sleep;

import io.github.zemelua.umu_little_maid.api.EveryTickTask;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.server.world.ServerWorld;

public class UpdateShouldSleepTask<E extends LivingEntity> extends EveryTickTask<E> {
	@Override
	public void tick(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		if (ModUtils.Worlds.getTimeOfDay(world) >= 12000L) {
			ModUtils.Livings.remember(brain, ModMemories.SHOULD_SLEEP);
		} else {
			brain.forget(ModMemories.SHOULD_SLEEP);
		}
	}
}
