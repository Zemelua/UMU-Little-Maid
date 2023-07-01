package io.github.zemelua.umu_little_maid.entity.brain.task.guard;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class RememberGuardTargetTask extends MultiTickTask<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModMemories.GUARDABLE_LIVING, MemoryModuleState.VALUE_PRESENT,
			ModMemories.GUARD_AGAINST, MemoryModuleState.VALUE_ABSENT
	);

	public RememberGuardTargetTask() {
		super(RememberGuardTargetTask.REQUIRED_MEMORIES, 0);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		brain.getOptionalMemory(ModMemories.GUARDABLE_LIVING)
				.ifPresent(target -> brain.remember(ModMemories.GUARD_AGAINST, target));
	}
}
