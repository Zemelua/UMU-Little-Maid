package io.github.zemelua.umu_little_maid.entity.brain.task.sleep;

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

public class RememberShouldSleepTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_SHOULD_SLEEP, MemoryModuleState.VALUE_ABSENT
	);

	private final long sleepStartTime;

	public RememberShouldSleepTask(long sleepStartTime) {
		super(RememberShouldSleepTask.REQUIRED_MEMORIES, 1);

		this.sleepStartTime = sleepStartTime;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E living) {
		return RememberShouldSleepTask.shouldSleep(world, this.sleepStartTime);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		brain.remember(ModEntities.MEMORY_SHOULD_SLEEP, Unit.INSTANCE);
	}

	public static boolean shouldSleep(ServerWorld world, long sleepStartTime) {
		return world.getTimeOfDay() % 24000 >= sleepStartTime;
	}
}
