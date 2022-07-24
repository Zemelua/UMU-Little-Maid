package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

import java.util.Map;

public class UpdateShouldSleepTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of();

	public UpdateShouldSleepTask() {
		super(UpdateShouldSleepTask.REQUIRED_MEMORIES, 1);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		if (this.shouldSleep(world)) {
			brain.remember(ModEntities.MEMORY_SHOULD_SLEEP, Unit.INSTANCE);
		} else {
			brain.forget(ModEntities.MEMORY_SHOULD_SLEEP);
		}
	}

	protected boolean shouldSleep(ServerWorld world) {
		return world.getTimeOfDay() >= 12000L;
	}
}
