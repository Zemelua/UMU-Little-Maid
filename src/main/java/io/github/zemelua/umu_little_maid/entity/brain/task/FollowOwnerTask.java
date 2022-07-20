package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Map;

public class FollowOwnerTask<E extends PathAwareEntity & Tameable> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of();

	private final float minDistance;
	private final float maxDistance;

	public FollowOwnerTask(float minDistance, float maxDistance) {
		super(FollowOwnerTask.REQUIRED_MEMORIES);

		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E tameable) {
		@Nullable Entity owner = tameable.getOwner();

		if (owner != null) {
			return !owner.isSpectator() && tameable.distanceTo(owner) > this.minDistance;
		}

		return false;
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E tameable, long time) {
		@Nullable Entity owner = tameable.getOwner();

		if (owner != null) {
			return !owner.isSpectator() && tameable.distanceTo(owner) > this.maxDistance;
		}

		return false;
	}

	@Override
	protected void keepRunning(ServerWorld world, E tameable, long time) {
		@Nullable Entity owner = tameable.getOwner();

		if (owner != null) {
			tameable.getLookControl().lookAt(owner, 10.0F, (float) tameable.getMaxLookPitchChange());

			if (tameable.isLeashed()) return;
			if (tameable.hasVehicle()) return;

			if (tameable.distanceTo(owner) >= 12.0F) {
				this.tryTeleport(tameable, owner);
			} else {
				tameable.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(owner, 0.9F, 0));
			}
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, E entity, long time) {
		entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
	}

	private void tryTeleport(E tameable, Entity owner) {
		BlockPos blockPos = owner.getBlockPos();
		for (int i = 0; i < 10; i++) {
			int xOffset = tameable.getRandom().nextInt(7) - 3;
			int yOffset = tameable.getRandom().nextInt(3) - 1;
			int zOffset = tameable.getRandom().nextInt(7) - 3;
			boolean teleported = this.tryTeleportTo(
					tameable, owner, blockPos.getX() + xOffset, blockPos.getY() + yOffset, blockPos.getZ() + zOffset);

			if (teleported) return;
		}
	}

	private boolean tryTeleportTo(E tameable, Entity owner, int x, int y, int z) {
		if (Math.abs((double)x - owner.getX()) < 2.0 && Math.abs((double)z - owner.getZ()) < 2.0) {
			return false;
		}
		if (!this.canTeleportTo(tameable, new BlockPos(x, y, z))) {
			return false;
		}

		tameable.refreshPositionAndAngles((double)x + 0.5, y, (double)z + 0.5, tameable.getYaw(), tameable.getPitch());
		tameable.getNavigation().stop();

		return true;
	}

	private boolean canTeleportTo(E tameable, BlockPos pos) {
		PathNodeType pathType = LandPathNodeMaker.getLandNodeType(tameable.getWorld(), pos.mutableCopy());
		if (pathType != PathNodeType.WALKABLE) return false;
		if (tameable.getWorld().getBlockState(pos.down()).getBlock() instanceof LeavesBlock) return false;

		return tameable.getWorld().isSpaceEmpty(tameable, tameable.getBoundingBox().offset(pos.subtract(tameable.getBlockPos())));
	}
}
