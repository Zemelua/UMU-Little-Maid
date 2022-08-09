package io.github.zemelua.umu_little_maid.entity.brain.task.heal;

import io.github.zemelua.umu_little_maid.data.tag.ModTags;
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
	protected void keepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		super.keepRunning(world, maid, time);

		if (this.cooldown < 10 && maid.getPersonality().isIn(ModTags.PERSONALITY_DEVOTE_WHEN_HEAL_OWNERS)) {
			this.cooldown += maid.getIntimacy() * 1.0D / 30.0D;
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		super.finishRunning(world, maid, time);

		maid.setPose(EntityPose.STANDING);
	}
}
