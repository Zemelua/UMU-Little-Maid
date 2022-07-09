package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FollowOwnerTask<E extends PathAwareEntity & Tameable> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.OWNER, MemoryModuleState.VALUE_PRESENT
	);

	private final float minDistance;
	private final float maxDistance;

	public FollowOwnerTask(float minDistance, float maxDistance) {
		super(FollowOwnerTask.REQUIRED_MEMORIES);

		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		Optional<UUID> ownerUUID = entity.getBrain().getOptionalMemory(ModEntities.OWNER);
		if (ownerUUID.isEmpty()) return false;

		PlayerEntity owner = world.getPlayerByUuid(ownerUUID.get());

		if (owner != null) {
			return !owner.isSpectator() && entity.distanceTo(owner) > this.minDistance;
		}

		return false;
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
		Optional<UUID> ownerUUID = entity.getBrain().getOptionalMemory(ModEntities.OWNER);
		if (ownerUUID.isEmpty()) return false;

		PlayerEntity owner = world.getPlayerByUuid(ownerUUID.get());

		if (owner != null) {
			return !owner.isSpectator() && entity.distanceTo(owner) > this.maxDistance;
		}

		return false;
	}

	@Override
	protected void keepRunning(ServerWorld world, E entity, long time) {
		Optional<UUID> ownerUUID = entity.getBrain().getOptionalMemory(ModEntities.OWNER);
		if (ownerUUID.isEmpty()) return;

		PlayerEntity owner = world.getPlayerByUuid(ownerUUID.get());
		if (owner == null) return;

		entity.getLookControl().lookAt(owner, 10.0F, (float) entity.getMaxLookPitchChange());

		if (entity.isLeashed()) return;
		if (entity.hasVehicle()) return;

		if (entity.distanceTo(owner) >= 12.0F) {
			this.tryTeleport(entity, owner);
		} else {
			entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(owner, 0.9F, 0));
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, E entity, long time) {
		entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
	}

	private void tryTeleport(E entity, PlayerEntity owner) {
		BlockPos blockPos = owner.getBlockPos();
		for (int i = 0; i < 10; i++) {
			int xOffset = entity.getRandom().nextInt(7) - 3;
			int yOffset = entity.getRandom().nextInt(3) - 1;
			int zOffset = entity.getRandom().nextInt(7) - 3;
			boolean teleported = this.tryTeleportTo(
					entity, owner, blockPos.getX() + xOffset, blockPos.getY() + yOffset, blockPos.getZ() + zOffset);

			if (teleported) return;
		}
	}

	private boolean tryTeleportTo(E entity, PlayerEntity owner, int x, int y, int z) {
		if (Math.abs((double)x - owner.getX()) < 2.0 && Math.abs((double)z - owner.getZ()) < 2.0) {
			return false;
		}
		if (!this.canTeleportTo(entity, new BlockPos(x, y, z))) {
			return false;
		}

		entity.refreshPositionAndAngles((double)x + 0.5, y, (double)z + 0.5, entity.getYaw(), entity.getPitch());
		entity.getNavigation().stop();

		return true;
	}

	private boolean canTeleportTo(E entity, BlockPos pos) {
		PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(entity.getWorld(), pos.mutableCopy());
		if (pathNodeType != PathNodeType.WALKABLE) return false;

		if (entity.getWorld().getBlockState(pos.down()).getBlock() instanceof LeavesBlock) return false;

		return entity.getWorld().isSpaceEmpty(entity, entity.getBoundingBox().offset(pos.subtract(entity.getBlockPos())));
	}
}
