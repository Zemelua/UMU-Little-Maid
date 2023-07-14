package io.github.zemelua.umu_little_maid.entity.brain.task.farm;

import io.github.zemelua.umu_little_maid.api.EveryTickTask;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class UpdateFarmPosTask<E extends LivingEntity> extends EveryTickTask<E> {
	@Override
	public void tick(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();
		Optional<BlockPos> farmPos = brain.getOptionalRegisteredMemory(ModMemories.FARM_POS);

		if (farmPos.isEmpty()) {
			brain.getOptionalRegisteredMemory(ModMemories.FARMABLE_POSES)
					.flatMap(poses -> ModUtils.getNearestPos(poses, living))
					.ifPresent(pos -> brain.remember(ModMemories.FARM_POS, pos, 80L));
		}
	}
}
