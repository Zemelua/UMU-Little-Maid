package io.github.zemelua.umu_little_maid.entity.brain.task.sleep;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;

public class WalkToSleepPosTask<E extends PathAwareEntity> extends Task<E> {
	public WalkToSleepPosTask() {
		super(ImmutableMap.of(ModMemories.SLEEP_POS, MemoryModuleState.VALUE_PRESENT));
	}

	@Override
	protected void run(ServerWorld world, E entity, long time) {
		Brain<?> brain = entity.getBrain();

		brain.getOptionalMemory(ModMemories.SLEEP_POS).ifPresent(pos
				-> brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 0.8F, 0)));
	}
}
