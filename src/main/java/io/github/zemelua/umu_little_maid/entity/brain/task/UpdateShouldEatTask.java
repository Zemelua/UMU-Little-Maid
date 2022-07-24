package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

import java.util.Map;
import java.util.function.Predicate;

public class UpdateShouldEatTask extends Task<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_SHOULD_EAT, MemoryModuleState.REGISTERED
	);

	private final Predicate<LittleMaidEntity> postpone;

	public UpdateShouldEatTask() {
		this(living -> false);
	}

	public UpdateShouldEatTask(Predicate<LittleMaidEntity> postpone) {
		super(UpdateShouldEatTask.REQUIRED_MEMORIES, 1);

		this.postpone = postpone;
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<?> brain = maid.getBrain();

		if (!MaidEatTask.searchHealItems(maid).isEmpty()) {
			if (this.postpone.test(maid)) {
				if (maid.getHealth() < maid.getMaxHealth() * 0.4F) {
					brain.remember(ModEntities.MEMORY_SHOULD_EAT, Unit.INSTANCE);

					return;
				}
			} else {
				if (maid.getHealth() < maid.getMaxHealth()) {
					brain.remember(ModEntities.MEMORY_SHOULD_EAT, Unit.INSTANCE);

					return;
				}
			}
		}

		if (brain.hasMemoryModule(ModEntities.MEMORY_SHOULD_EAT)) {
			brain.forget(ModEntities.MEMORY_SHOULD_EAT);
		}
	}
}
