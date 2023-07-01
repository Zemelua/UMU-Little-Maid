package io.github.zemelua.umu_little_maid.entity.brain.task.sleep;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;

public class WalkToSleepPosTask<E extends PathAwareEntity> extends MultiTickTask<E> {
	public WalkToSleepPosTask() {
		super(ImmutableMap.of(ModMemories.SLEEP_POS, MemoryModuleState.VALUE_PRESENT));
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		Brain<?> brain = entity.getBrain();

		brain.getOptionalMemory(ModMemories.SLEEP_POS).ifPresent(pos -> LookTargetUtil.walkTowards(entity, pos, 0.8F, 0));
	}
}
