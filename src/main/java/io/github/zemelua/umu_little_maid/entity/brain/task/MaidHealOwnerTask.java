package io.github.zemelua.umu_little_maid.entity.brain.task;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.MaidPose;
import net.minecraft.server.world.ServerWorld;

public class MaidHealOwnerTask extends HealOwnerTask<LittleMaidEntity> {
	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		super.run(world, maid, time);

		maid.setAnimationPose(MaidPose.HEAL);
	}

	@Override
	protected void finishRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		super.finishRunning(world, maid, time);

		maid.setAnimationPose(MaidPose.NONE);
	}
}
