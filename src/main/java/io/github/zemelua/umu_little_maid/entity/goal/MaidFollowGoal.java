package io.github.zemelua.umu_little_maid.entity.goal;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class MaidFollowGoal extends Goal {
	private final LittleMaidEntity maid;

	private int recalculationTicks;
	private float waterCostCache;

	public MaidFollowGoal(LittleMaidEntity maid) {
		this.maid = maid;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
	}

	@Override
	public boolean canStart() {
		Entity owner = this.maid.getOwner();

		if (owner == null) return false;
		if (owner.isSpectator()) return false;
		if (this.maid.isSitting()) return false;

		return this.maid.distanceTo(owner) >= this.maid.getPersonality().getMinFollowDistance(this.maid);
	}

	@Override
	public boolean shouldContinue() {
		Entity owner = this.maid.getOwner();

		if (owner == null) return false;
		if (this.maid.getNavigation().isIdle()) return false;
		if (owner.isSpectator()) return false;
		if (this.maid.isSitting()) return false;

		return this.maid.distanceTo(owner) >= this.maid.getPersonality().getMaxFollowDistance(this.maid);
	}

	@Override
	public void start() {
		this.recalculationTicks = 0;
		this.waterCostCache = this.maid.getPathfindingPenalty(PathNodeType.WATER);
		this.maid.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
	}

	@Override
	public void tick() {
		Entity owner = this.maid.getOwner();
		if (owner == null) return;

		this.maid.getLookControl().lookAt(this.maid.getOwner(), 10.0F, (float)this.maid.getMaxLookPitchChange());

		if (--this.recalculationTicks > 0) return;
		this.recalculationTicks = this.getTickCount(10);

		if (this.maid.isLeashed()) return;
		if (this.maid.hasVehicle()) return;

		if (this.maid.distanceTo(owner) >= 12.0F) {
			this.tryTeleport();
		} else {
			this.maid.getNavigation().startMovingTo(owner, 1.0D);
		}
	}

	@Override
	public void stop() {
		this.maid.getNavigation().stop();
		this.maid.setPathfindingPenalty(PathNodeType.WATER, this.waterCostCache);
	}

	private void tryTeleport() {
		Entity owner = this.maid.getOwner();
		if (owner == null) return;

		BlockPos blockPos = owner.getBlockPos();
		for (int i = 0; i < 10; i++) {
			int xOffset = this.getRandomInt(-3, 3);
			int yOffset = this.getRandomInt(-1, 1);
			int zOffset = this.getRandomInt(-3, 3);
			boolean teleported = this.tryTeleportTo(blockPos.getX() + xOffset, blockPos.getY() + yOffset, blockPos.getZ() + zOffset);

			if (teleported) return;
		}
	}

	private boolean tryTeleportTo(int x, int y, int z) {
		Entity owner = this.maid.getOwner();
		if (owner == null) return false;

		if (Math.abs((double)x - owner.getX()) < 2.0 && Math.abs((double)z - owner.getZ()) < 2.0) {
			return false;
		}
		if (!this.canTeleportTo(new BlockPos(x, y, z))) {
			return false;
		}

		this.maid.refreshPositionAndAngles((double)x + 0.5, y, (double)z + 0.5, this.maid.getYaw(), this.maid.getPitch());
		this.maid.getNavigation().stop();

		return true;
	}


	/**
	 * @param pos テレポート先の座標
	 * @return {@code true} のときテレポートでき、{@code false} のときテレポートできません
	 */
	private boolean canTeleportTo(BlockPos pos) {
		PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(this.maid.getWorld(), pos.mutableCopy());
		if (pathNodeType != PathNodeType.WALKABLE) return false;

		if (this.maid.getWorld().getBlockState(pos.down()).getBlock() instanceof LeavesBlock) return false;

		return this.maid.getWorld().isSpaceEmpty(this.maid, this.maid.getBoundingBox().offset(pos.subtract(this.maid.getBlockPos())));
	}

	private int getRandomInt(int min, int max) {
		return this.maid.getRandom().nextInt(max - min + 1) + min;
	}
}
