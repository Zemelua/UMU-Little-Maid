package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class UpdateAttractTargetsTask extends Task<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_ATTRACTABLE_LIVINGS, MemoryModuleState.VALUE_PRESENT,
			ModEntities.MEMORY_ATTRACT_TARGETS, MemoryModuleState.VALUE_ABSENT
	);

	public UpdateAttractTargetsTask() {
		super(UpdateAttractTargetsTask.REQUIRED_MEMORIES);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		brain.getOptionalMemory(ModEntities.MEMORY_ATTRACTABLE_LIVINGS).ifPresent(livings ->
				brain.remember(ModEntities.MEMORY_ATTRACT_TARGETS, livings));
	}
}
