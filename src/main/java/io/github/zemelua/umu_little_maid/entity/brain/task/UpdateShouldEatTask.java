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
import java.util.function.Predicate;

public class UpdateShouldEatTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_SHOULD_EAT, MemoryModuleState.REGISTERED
	);

	private final Predicate<E> postpone;

	public UpdateShouldEatTask() {
		this(living -> false);
	}

	public UpdateShouldEatTask(Predicate<E> postpone) {
		super(UpdateShouldEatTask.REQUIRED_MEMORIES);

		this.postpone = postpone;
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		if (this.postpone.test(living)) {
			if (living.getHealth() < living.getMaxHealth() * 0.4F) {
				brain.remember(ModEntities.MEMORY_SHOULD_EAT, Unit.INSTANCE);

				return;
			}
		} else {
			if (living.getHealth() < living.getMaxHealth()) {
				brain.remember(ModEntities.MEMORY_SHOULD_EAT, Unit.INSTANCE);

				return;
			}
		}

		if (brain.hasMemoryModule(ModEntities.MEMORY_SHOULD_EAT)) {
			brain.forget(ModEntities.MEMORY_SHOULD_EAT);
		}
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E inventoryOwner, long time) {
		return true;
	}
}
