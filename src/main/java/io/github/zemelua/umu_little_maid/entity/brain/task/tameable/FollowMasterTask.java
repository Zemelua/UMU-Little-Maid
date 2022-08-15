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

import static io.github.zemelua.umu_little_maid.entity.ModEntities.*;
import static net.minecraft.entity.ai.brain.MemoryModuleState.*;
import static net.minecraft.entity.ai.brain.MemoryModuleType.*;


public class FollowMasterTask<E extends PathAwareEntity & IHasMaster> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			// WALK_TARGET, VALUE_ABSENT,
			MEMORY_JOB_SITE, VALUE_ABSENT
	);

	private final float startDistance;

	public FollowMasterTask(float startDistance) {
		super(REQUIRED_MEMORIES, 15);

		this.startDistance = startDistance;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E mob) {
		if (mob.isLeashed()) return false;
		if (mob.hasVehicle()) return false;

		Optional<PlayerEntity> master = mob.getMaster();

		return master
				.filter(masterValue -> !masterValue.isSpectator() && mob.distanceTo(masterValue) >= this.startDistance)
				.isPresent();
	}

	@Override
	protected void run(ServerWorld world, E mob, long time) {
		Optional<PlayerEntity> master = mob.getMaster();

		master.ifPresent(masterValue -> {
			Brain<?> brain = mob.getBrain();

			brain.remember(WALK_TARGET, new WalkTarget(masterValue, 1.2F, 2));
			brain.remember(LOOK_TARGET, new EntityLookTarget(masterValue, true));
		});
	}
}
