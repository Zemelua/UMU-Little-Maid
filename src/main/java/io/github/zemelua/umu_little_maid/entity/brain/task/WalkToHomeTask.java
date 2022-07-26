package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;

import java.util.Map;
import java.util.Optional;

public class WalkToHomeTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT,
			MemoryModuleType.HOME, MemoryModuleState.VALUE_PRESENT
	);

	private final float speed;

	public WalkToHomeTask(float speed) {
		super(WalkToHomeTask.REQUIRED_MEMORIES);

		this.speed = speed;
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();
		Optional<GlobalPos> pos = brain.getOptionalMemory(MemoryModuleType.HOME);

		pos.ifPresent(posObject -> {
			brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(posObject.getPos(), this.speed, 0));
			// brain.remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(posObject.getPos()));
		});
	}
}
