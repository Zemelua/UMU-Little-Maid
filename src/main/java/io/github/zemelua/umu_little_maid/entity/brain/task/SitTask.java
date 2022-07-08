package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class SitTask extends Task<PathAwareEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.<MemoryModuleType<?>, MemoryModuleState>builder()
			.put(ModEntities.IS_SITTING, MemoryModuleState.VALUE_PRESENT)
			.build();

	public SitTask() {
		super(SitTask.REQUIRED_MEMORIES);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, PathAwareEntity entity, long time) {
		return entity.getBrain().hasMemoryModule(ModEntities.IS_SITTING);
	}

	@Override
	protected void run(ServerWorld world, PathAwareEntity entity, long time) {
		Brain<?> brain = entity.getBrain();
		brain.forget(MemoryModuleType.WALK_TARGET);
	}

	@Override
	protected void keepRunning(ServerWorld world, PathAwareEntity entity, long time) {

		// UMULittleMaid.LOGGER.info("owner ing");

		super.keepRunning(world, entity, time);
	}
}
