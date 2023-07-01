package io.github.zemelua.umu_little_maid.entity.brain.task.tameable;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;

import java.util.Map;
import java.util.Optional;

import static io.github.zemelua.umu_little_maid.entity.ModEntities.*;
import static net.minecraft.entity.ai.brain.MemoryModuleType.*;

@SuppressWarnings("unused")
public class EatChorusFruitTask extends MultiTickTask<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
	);

	private final float startDistance;
	private final BlockPos.Mutable teleportTo = new BlockPos.Mutable();

	public EatChorusFruitTask(float startDistance) {
		super(REQUIRED_MEMORIES);

		this.startDistance = startDistance;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		if (maid.isLeashed()) return false;
		if (maid.hasVehicle()) return false;
		if (maid.getHasChorusFruit().isEmpty()) return false;
		if (maid.getPose() == POSE_EATING) return false;

		Optional<PlayerEntity> master = maid.getMaster();

		return master.filter(masterValue -> {
			if (masterValue.isSpectator()) return false;
			if (maid.distanceTo(masterValue) < this.startDistance) return false;

			for (int i = 0; i < 16; i++) {
				int xOffset = maid.getRandom().nextInt(7) - 3;
				int yOffset = maid.getRandom().nextInt(3) - 1;
				int zOffset = maid.getRandom().nextInt(7) - 3;
				BlockPos pos = masterValue.getBlockPos().add(xOffset, yOffset, zOffset);

				if (maid.canTeleport(pos)) {
					this.teleportTo.set(pos);

					return true;
				}
			}

			return false;
		}).isPresent();
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<?> brain = maid.getBrain();

		brain.forget(WALK_TARGET);

		maid.eatFood(maid.getHasChorusFruit().split(1), food -> {
			Optional<PlayerEntity> master = maid.getMaster();

			master.ifPresent(masterValue -> {
				Vec3d oldPos = maid.getPos();

				boolean teleported = maid.teleport(this.teleportTo.getX(), this.teleportTo.getY(), this.teleportTo.getZ(), true);
				if (teleported) {
					world.emitGameEvent(GameEvent.TELEPORT, oldPos, GameEvent.Emitter.of(maid));
					world.playSound(null, BlockPos.ofFloored(oldPos), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
					world.playSound(null, maid.getBlockPos(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.NEUTRAL, 1.0F, 1.0F);
				}
			});
		});
	}
}
