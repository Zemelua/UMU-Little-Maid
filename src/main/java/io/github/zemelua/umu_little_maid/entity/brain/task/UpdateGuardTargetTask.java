package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class UpdateGuardTargetTask extends Task<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_GUARDABLE_LIVING, MemoryModuleState.VALUE_PRESENT,
			ModEntities.MEMORY_GUARD_TARGET, MemoryModuleState.VALUE_ABSENT
	);

	public UpdateGuardTargetTask() {
		super(UpdateGuardTargetTask.REQUIRED_MEMORIES);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		brain.getOptionalMemory(ModEntities.MEMORY_GUARDABLE_LIVING).ifPresent(living ->
				brain.remember(ModEntities.MEMORY_GUARD_TARGET, living));
	}
}
