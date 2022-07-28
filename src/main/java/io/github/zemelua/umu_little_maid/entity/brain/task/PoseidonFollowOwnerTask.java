package io.github.zemelua.umu_little_maid.entity.brain.task;

import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;

public class PoseidonFollowOwnerTask<E extends PathAwareEntity & Tameable> extends FollowOwnerTask<E> {
	public PoseidonFollowOwnerTask(float minDistance, float maxDistance) {
		super(minDistance, maxDistance);
	}

	@Override
	protected boolean canTeleportTo(E tameable, BlockPos pos) {
		PathNodeType pathType = tameable.getNavigation().getNodeMaker()
				.getDefaultNodeType(tameable.getWorld(), pos.getX(), pos.getY(), pos.getZ());
		if (pathType != PathNodeType.WALKABLE && pathType != PathNodeType.WATER) return false;
		if (pathType != PathNodeType.WATER && tameable.getWorld().getBlockState(pos.down()).getBlock() instanceof LeavesBlock) return false;

		return tameable.getWorld().isSpaceEmpty(tameable, tameable.getBoundingBox().offset(pos.subtract(tameable.getBlockPos())));
	}
}
