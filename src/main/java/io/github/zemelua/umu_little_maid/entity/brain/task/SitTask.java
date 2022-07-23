package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class SitTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_IS_SITTING, MemoryModuleState.VALUE_PRESENT
	);

	public SitTask() {
		super(SitTask.REQUIRED_MEMORIES);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E living, long time) {
		return living.getBrain().hasMemoryModule(ModEntities.MEMORY_IS_SITTING);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		brain.forget(MemoryModuleType.WALK_TARGET);
		brain.forget(MemoryModuleType.ATTACK_TARGET);
	}
}
