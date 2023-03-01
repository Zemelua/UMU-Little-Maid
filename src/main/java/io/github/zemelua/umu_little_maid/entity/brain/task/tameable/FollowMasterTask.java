package io.github.zemelua.umu_little_maid.entity.brain.task.tameable;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.util.IHasMaster;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;
import java.util.Optional;

import static net.minecraft.entity.ai.brain.MemoryModuleType.*;


public class FollowMasterTask<E extends PathAwareEntity & IHasMaster> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
	);

	private final float startDistance;

	public FollowMasterTask(float startDistance) {
		super(REQUIRED_MEMORIES, 15);

		this.startDistance = startDistance;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E pet) {
		if (pet.isLeashed()) return false;
		if (pet.hasVehicle()) return false;

		Optional<PlayerEntity> master = pet.getMaster();

		return master
				.filter(masterValue -> !masterValue.isSpectator() && pet.distanceTo(masterValue) >= this.startDistance)
				.isPresent();
	}

	@Override
	protected void run(ServerWorld world, E pet, long time) {
		Optional<PlayerEntity> master = pet.getMaster();

		master.ifPresent(masterValue -> {
			Brain<?> brain = pet.getBrain();

			brain.remember(WALK_TARGET, new WalkTarget(masterValue, 1.2F, 2));
			brain.remember(LOOK_TARGET, new EntityLookTarget(masterValue, true));
		});
	}
}
