package io.github.zemelua.umu_little_maid.api;

import io.github.zemelua.umu_little_maid.mixin.AccessorMultiTickTask;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;

import java.util.Map;

public abstract class BetterMultiTickTask<E extends LivingEntity> extends MultiTickTask<E> {
	private final long runTime;

	public BetterMultiTickTask(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState) {
		super(requiredMemoryState);

		this.runTime = 60L;
	}

	public BetterMultiTickTask(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState, int runTime) {
		super(requiredMemoryState, runTime);

		this.runTime = runTime;
	}

	protected long getPassedTicks(long time) {
		return this.runTime - (((AccessorMultiTickTask) this).getEndTime() - time);
	}
}
