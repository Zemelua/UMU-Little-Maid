package io.github.zemelua.umu_little_maid.entity.brain.task.swim;

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

public class RememberShouldBreathTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModMemories.SHOULD_BREATH, MemoryModuleState.VALUE_ABSENT
	);

	private final int shouldBreathAir;

	public RememberShouldBreathTask(int shouldBreathAir) {
		super(RememberShouldBreathTask.REQUIRED_MEMORIES, 0);

		this.shouldBreathAir = shouldBreathAir;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E living) {
		return living.getAir() < this.shouldBreathAir;
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		brain.remember(ModMemories.SHOULD_BREATH, Unit.INSTANCE);
	}
}
