package io.github.zemelua.umu_little_maid.entity.brain.task.farm;

import io.github.zemelua.umu_little_maid.entity.brain.task.engage.ForgetJobSiteTask;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

public class ForgetFarmSiteTask extends ForgetJobSiteTask {
	@Override
	protected boolean isNotSite(ServerWorld world, BlockPos pos, LittleMaidEntity maid) {
		if (world.getBlockState(pos).isIn(BlockTags.WOODEN_FENCES)) {
			return !RememberFarmSiteTask.isScarecrow(world, pos);
		}

		return super.isNotSite(world, pos, maid);
	}
}
