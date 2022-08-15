package io.github.zemelua.umu_little_maid.entity.brain.task.tameable;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("unused")
public class PoseidonFollowOwnerTask extends FollowOwnerTask {
	public PoseidonFollowOwnerTask(float minDistance, float maxDistance) {
		super(minDistance, maxDistance);
	}

	@Override
	protected boolean canTeleportTo(LittleMaidEntity maid, BlockPos pos) {
		PathNodeType pathType = maid.getNavigation().getNodeMaker()
				.getDefaultNodeType(maid.getWorld(), pos.getX(), pos.getY(), pos.getZ());
		if (pathType != PathNodeType.WALKABLE && pathType != PathNodeType.WATER) return false;
		if (pathType != PathNodeType.WATER && maid.getWorld().getBlockState(pos.down()).getBlock() instanceof LeavesBlock) return false;

		return maid.getWorld().isSpaceEmpty(maid, maid.getBoundingBox().offset(pos.subtract(maid.getBlockPos())));
	}
}
