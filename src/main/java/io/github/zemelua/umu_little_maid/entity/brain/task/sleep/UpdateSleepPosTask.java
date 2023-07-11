package io.github.zemelua.umu_little_maid.entity.brain.task.sleep;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.api.EveryTickTask;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class UpdateSleepPosTask extends EveryTickTask<LittleMaidEntity> {
	@Override
	public void tick(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		Optional<GlobalPos> home = maid.getHome();
		Optional<BlockPos> availableBed = brain.getOptionalRegisteredMemory(ModMemories.AVAILABLE_BED);
		Optional<BlockPos> sleepPos = brain.getOptionalRegisteredMemory(ModMemories.SLEEP_POS);

		if (sleepPos.isEmpty()) {
			if (home.isPresent() && home.get().getDimension().equals(world.getRegistryKey())) {
				@Nullable Path path = maid.getNavigation().findPathTo(home.get().getPos(), 0);

				if (path != null && path.reachesTarget()) {
					brain.remember(ModMemories.SLEEP_POS, home.get().getPos());
				}
			} else if (availableBed.isPresent()) {
				@Nullable Path path = maid.getNavigation().findPathTo(availableBed.get(), 0);

				if (path != null && path.reachesTarget()) {
					brain.remember(ModMemories.SLEEP_POS, availableBed.get());
				}
			}
		} else if (!maid.isSleeping()) {
			@Nullable Path path = maid.getNavigation().findPathTo(sleepPos.get(), 0);

			if (path == null || !path.reachesTarget() || !maid.getWorld().getBlockState(sleepPos.get()).isIn(BlockTags.BEDS)) {
				UMULittleMaid.LOGGER.info(sleepPos.get());

				brain.forget(ModMemories.SLEEP_POS);
			}
		}
	}
}
