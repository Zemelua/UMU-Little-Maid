package io.github.zemelua.umu_little_maid.entity.brain.task.attack.bow;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

import java.util.Map;

public class RememberHasArrowsTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModMemories.HAS_ARROWS, MemoryModuleState.VALUE_ABSENT
	);

	public RememberHasArrowsTask() {
		super(RememberHasArrowsTask.REQUIRED_MEMORIES, 0);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		if (!living.getArrowType(living.getMainHandStack()).isEmpty()) {
			brain.remember(ModMemories.HAS_ARROWS, Unit.INSTANCE);
		}
	}
}
