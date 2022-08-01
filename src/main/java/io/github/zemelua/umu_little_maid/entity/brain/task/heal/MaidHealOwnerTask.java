package io.github.zemelua.umu_little_maid.entity.brain.task.heal;

import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.EntityPose;
import net.minecraft.server.world.ServerWorld;

public class MaidHealOwnerTask extends HealOwnerTask<LittleMaidEntity> {
	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		super.run(world, maid, time);

		maid.setPose(ModEntities.POSE_HEALING);
	}

	@Override
	protected void finishRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		super.finishRunning(world, maid, time);

		maid.setPose(EntityPose.STANDING);
	}
}
