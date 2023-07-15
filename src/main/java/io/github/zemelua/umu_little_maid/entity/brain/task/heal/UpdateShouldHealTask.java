package io.github.zemelua.umu_little_maid.entity.brain.task.heal;

import io.github.zemelua.umu_little_maid.api.EveryTickTask;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.server.world.ServerWorld;

public class UpdateShouldHealTask extends EveryTickTask<LittleMaidEntity> {
	@Override
	public void tick(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<?> brain = maid.getBrain();

		if (HealOwnerTask.shouldHeal(maid)) {
			ModUtils.Brains.remember(brain, ModMemories.SHOULD_HEAL);
		} else {
			brain.forget(ModMemories.SHOULD_HEAL);
		}
	}
}
