package io.github.zemelua.umu_little_maid.entity.brain.task.tameable;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.util.ITameable;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

import java.util.Map;
import java.util.Optional;

public class TeleportToMasterTask<E extends PathAwareEntity & ITameable> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
	);

	private final float startDistance;

	public TeleportToMasterTask(float startDistance) {
		super(REQUIRED_MEMORIES, 60);

		this.startDistance = startDistance;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E pet) {
		Optional<PlayerEntity> master = pet.getMaster();

		return master
				.filter(masterValue -> this.shouldTeleport(pet, masterValue))
				.isPresent();
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E pet, long time) {
		Optional<PlayerEntity> master = pet.getMaster();

		return master
				.filter(masterValue -> this.shouldTeleport(pet, masterValue))
				.isPresent();
	}

	@Override
	protected void finishRunning(ServerWorld world, E pet, long time) {
		Optional<PlayerEntity> master = pet.getMaster();

		if (this.isTimeLimitExceeded(time)) {
			master.ifPresent(masterValue -> {
				for (int i = 0; i < 16; i++) {
					int xOffset = pet.getRandom().nextInt(7) - 3;
					int yOffset = pet.getRandom().nextInt(3) - 1;
					int zOffset = pet.getRandom().nextInt(7) - 3;
					BlockPos oldPos = new BlockPos(pet.getBlockPos());
					BlockPos newPos = masterValue.getBlockPos().add(xOffset, yOffset, zOffset);

					boolean teleported = pet.teleport(newPos.getX(), newPos.getY(), newPos.getZ(), true);
					if (teleported) {
						world.emitGameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Emitter.of(pet));
						world.playSound(null, new BlockPos(oldPos), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
						world.playSound(null, pet.getBlockPos(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.NEUTRAL, 1.0F, 1.0F);

						return;
					}
				}
			});
		}
	}

	private boolean shouldTeleport(E pet, PlayerEntity master) {
		if (pet.isLeashed()) return false;
		if (pet.hasVehicle()) return false;

		return !master.isSpectator() && pet.distanceTo(master) >= this.startDistance && pet.isFollowingMaster();
	}
}
