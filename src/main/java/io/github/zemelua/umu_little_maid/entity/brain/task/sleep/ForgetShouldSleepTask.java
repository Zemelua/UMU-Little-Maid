package io.github.zemelua.umu_little_maid.entity.brain.task.sleep;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class ForgetShouldSleepTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModMemories.SHOULD_SLEEP, MemoryModuleState.VALUE_PRESENT
	);

	private final long sleepStartTime;

	public ForgetShouldSleepTask(long sleepStartTime) {
		super(ForgetShouldSleepTask.REQUIRED_MEMORIES, 1);

		this.sleepStartTime = sleepStartTime;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E living) {
		return !RememberShouldSleepTask.shouldSleep(world, this.sleepStartTime);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		brain.forget(ModMemories.SHOULD_SLEEP);
	}
}
