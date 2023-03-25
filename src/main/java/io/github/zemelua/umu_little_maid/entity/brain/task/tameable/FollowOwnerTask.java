package io.github.zemelua.umu_little_maid.entity.brain.task.tameable;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Map;

@SuppressWarnings("unused")
public class FollowOwnerTask extends Task<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
	);

	private final float minDistance;
	private final float maxDistance;

	public FollowOwnerTask(float minDistance, float maxDistance) {
		super(FollowOwnerTask.REQUIRED_MEMORIES);

		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		@Nullable Entity owner = maid.getOwner();

		if (owner != null) {
			return !owner.isSpectator() && maid.distanceTo(owner) > this.minDistance;
		}

		return false;
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		@Nullable Entity owner = maid.getOwner();

		if (owner != null) {
			return !owner.isSpectator() && maid.distanceTo(owner) > this.maxDistance;
		}

		return false;
	}

	@Override
	protected void keepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		@Nullable Entity owner = maid.getOwner();

		if (owner != null) {
			maid.getLookControl().lookAt(owner, 10.0F, (float) maid.getMaxLookPitchChange());

			if (maid.isLeashed()) return;
			if (maid.hasVehicle()) return;

			if (maid.distanceTo(owner) >= 12.0F) {
				this.tryTeleport(maid, owner);
			} else {
				maid.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(owner, 0.9F, 0));
			}
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, LittleMaidEntity entity, long time) {
		entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
	}

	private void tryTeleport(LittleMaidEntity maid, Entity owner) {
		BlockPos blockPos = owner.getBlockPos();
		for (int i = 0; i < 10; i++) {
			int xOffset = maid.getRandom().nextInt(7) - 3;
			int yOffset = maid.getRandom().nextInt(3) - 1;
			int zOffset = maid.getRandom().nextInt(7) - 3;
			boolean teleported = this.tryTeleportTo(
					maid, owner, blockPos.getX() + xOffset, blockPos.getY() + yOffset, blockPos.getZ() + zOffset);

			if (teleported) return;
		}
	}

	private boolean tryTeleportTo(LittleMaidEntity maid, Entity owner, int x, int y, int z) {
		if (Math.abs((double)x - owner.getX()) < 2.0 && Math.abs((double)z - owner.getZ()) < 2.0) {
			return false;
		}
		if (!this.canTeleportTo(maid, new BlockPos(x, y, z))) {
			return false;
		}

		maid.refreshPositionAndAngles((double)x + 0.5, y, (double)z + 0.5, maid.getYaw(), maid.getPitch());
		maid.getNavigation().stop();

		return true;
	}

	protected boolean canTeleportTo(LittleMaidEntity maid, BlockPos pos) {
		PathNodeType pathType = LandPathNodeMaker.getLandNodeType(maid.getWorld(), pos.mutableCopy());
		if (pathType != PathNodeType.WALKABLE) return false;
		if (maid.getWorld().getBlockState(pos.down()).getBlock() instanceof LeavesBlock) return false;

		return maid.getWorld().isSpaceEmpty(maid, maid.getBoundingBox().offset(pos.subtract(maid.getBlockPos())));
	}
}
