package io.github.zemelua.umu_little_maid.entity.brain.task.tameable;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class SitTask extends MultiTickTask<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModMemories.IS_SITTING, MemoryModuleState.VALUE_PRESENT
	);

	public SitTask() {
		super(SitTask.REQUIRED_MEMORIES);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		brain.stopAllTasks(world, maid);

		return true;
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		return maid.getBrain().hasMemoryModule(ModMemories.IS_SITTING);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		brain.forget(MemoryModuleType.WALK_TARGET);
		brain.forget(MemoryModuleType.ATTACK_TARGET);
	}
}
