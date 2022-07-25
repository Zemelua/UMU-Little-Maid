package io.github.zemelua.umu_little_maid.entity.brain.task.heal;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

import javax.annotation.Nullable;
import java.util.Map;

public class ApproachToHealTask<E extends LivingEntity & Tameable> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT,
			MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED,
			ModEntities.MEMORY_SHOULD_HEAL, MemoryModuleState.VALUE_PRESENT
	);

	private final float speed;

	public ApproachToHealTask(float speed) {
		super(ApproachToHealTask.REQUIRED_MEMORIES);

		this.speed = speed;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E tameable) {
		@Nullable Entity owner = tameable.getOwner();
		if (owner == null) return false;

		return tameable.distanceTo(owner) > 4.0D;
	}

	@Override
	protected void run(ServerWorld world, E tameable, long time) {
		Brain<?> brain = tameable.getBrain();
		@Nullable Entity owner = tameable.getOwner();

		if (owner != null) {
			brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(owner, this.speed, 3));
			brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(owner, true));
		}
	}
}
