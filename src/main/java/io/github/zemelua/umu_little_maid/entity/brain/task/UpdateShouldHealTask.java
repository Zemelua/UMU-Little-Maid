package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

import java.util.Map;

public class UpdateShouldHealTask<E extends PathAwareEntity & Tameable> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_SHOULD_HEAL, MemoryModuleState.REGISTERED
	);

	public UpdateShouldHealTask() {
		super(UpdateShouldHealTask.REQUIRED_MEMORIES);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E tameable, long time) {
		return true;
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	protected void keepRunning(ServerWorld world, E tameable, long time) {
		Brain<?> brain = tameable.getBrain();

		if (HealOwnerTask.shouldHeal(tameable)) {
			brain.remember(ModEntities.MEMORY_SHOULD_HEAL, Unit.INSTANCE);
		} else {
			brain.forget(ModEntities.MEMORY_SHOULD_HEAL);
		}
	}
}
