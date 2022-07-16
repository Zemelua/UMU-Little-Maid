package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Optional;

public class WalkToFarmPosTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			MemoryModuleType.WALK_TARGET, MemoryModuleState.REGISTERED,
			MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED,
			ModEntities.MEMORY_FARM_POS, MemoryModuleState.VALUE_PRESENT,
			ModEntities.MEMORY_FARM_COOLDOWN, MemoryModuleState.VALUE_ABSENT
	);

	private final float speed;

	public WalkToFarmPosTask(float speed) {
		super(WalkToFarmPosTask.REQUIRED_MEMORIES);

		this.speed = speed;
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();
		Optional<BlockPos> pos = brain.getOptionalMemory(ModEntities.MEMORY_FARM_POS);

		pos.ifPresent(posArg -> {
			brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(posArg, this.speed, 0));
			brain.remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(posArg));
		});
	}
}
