package io.github.zemelua.umu_little_maid.entity.brain.task.farm;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Map;
import java.util.Optional;

public class WalkToFarmPosTask<E extends PathAwareEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT,
			ModEntities.MEMORY_FARM_POS, MemoryModuleState.VALUE_PRESENT,
			ModEntities.MEMORY_FARM_COOLDOWN, MemoryModuleState.VALUE_ABSENT
	);

	private final float speed;

	public WalkToFarmPosTask(float speed) {
		super(WalkToFarmPosTask.REQUIRED_MEMORIES, 0);

		this.speed = speed;
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();
		Optional<BlockPos> pos = brain.getOptionalMemory(ModEntities.MEMORY_FARM_POS);

		pos.ifPresent(posValue -> brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(posValue, this.speed, 0)));
	}
}
