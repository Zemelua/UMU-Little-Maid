package io.github.zemelua.umu_little_maid.entity.brain.task.farm;

import io.github.zemelua.umu_little_maid.entity.brain.task.engage.RememberJobSiteTask;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RememberFarmSiteTask extends RememberJobSiteTask {
	@Override
	protected boolean isSite(World world, BlockPos pos) {
		if (world.getBlockState(pos).isIn(BlockTags.WOODEN_FENCES)) {
			return RememberFarmSiteTask.isScarecrow(world, pos);
		}

		return true;
	}

	public static boolean isScarecrow(World world, BlockPos pos) {
		return world.getBlockState(pos).isIn(BlockTags.WOODEN_FENCES)
				&& world.getBlockState(pos.up()).isOf(Blocks.HAY_BLOCK)
				&& world.getBlockState(pos.up(2)).isIn(ModTags.BLOCK_SCARECROW_HEAD);
	}
}
