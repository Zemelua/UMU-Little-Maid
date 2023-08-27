package io.github.zemelua.umu_little_maid.entity.brain.task.fish;

import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.api.EveryTickTask;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class WalkToFishPosTask<E extends LivingEntity> extends EveryTickTask<E> {
	@Override
	public void tick(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();
		Optional<Pair<BlockPos, BlockPos>> fishPos = brain.getOptionalRegisteredMemory(ModMemories.FISH_POS);

		if (fishPos.isPresent()) {
			LookTargetUtil.walkTowards(living, fishPos.get().getFirst(), 1.0F, 0);
		}
	}
}
