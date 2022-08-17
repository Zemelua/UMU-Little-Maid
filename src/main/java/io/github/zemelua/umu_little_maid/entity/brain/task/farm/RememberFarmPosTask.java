package io.github.zemelua.umu_little_maid.entity.brain.task.farm;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class RememberFarmPosTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_FARMABLE_POSES, MemoryModuleState.VALUE_PRESENT,
			ModEntities.MEMORY_FARM_POS, MemoryModuleState.VALUE_ABSENT
	);

	public RememberFarmPosTask() {
		super(RememberFarmPosTask.REQUIRED_MEMORIES, 0);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		brain.getOptionalMemory(ModEntities.MEMORY_FARMABLE_POSES)
				.flatMap(poses -> ModUtils.getNearestPos(poses, living))
				.ifPresent(pos -> brain.remember(ModEntities.MEMORY_FARM_POS, pos, 200L));
	}
}
