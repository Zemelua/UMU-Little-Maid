package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class MaidForgetFarmPosTask extends Task<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_FARM_POS, MemoryModuleState.VALUE_PRESENT
	);

	public MaidForgetFarmPosTask() {
		super(MaidForgetFarmPosTask.REQUIRED_MEMORIES);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<?> brain = maid.getBrain();

		brain.getOptionalMemory(ModEntities.MEMORY_FARM_POS)
				.ifPresent(poses -> {
					if ((!MaidFarmTask.isPlantable(poses, world) || maid.getHasCrop().isEmpty()) && !MaidFarmTask.isHarvestable(poses, world)) {
						brain.forget(ModEntities.MEMORY_FARM_POS);
					}
				});
	}
}
