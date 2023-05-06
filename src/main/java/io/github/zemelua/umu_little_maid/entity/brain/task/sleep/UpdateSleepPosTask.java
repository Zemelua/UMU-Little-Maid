package io.github.zemelua.umu_little_maid.entity.brain.task.sleep;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class UpdateSleepPosTask extends Task<LittleMaidEntity> {
	public UpdateSleepPosTask() {
		super(ImmutableMap.of(ModMemories.SHOULD_SLEEP, MemoryModuleState.VALUE_PRESENT, ModMemories.SLEEP_POS, MemoryModuleState.VALUE_ABSENT), 1);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity entity) {
		UMULittleMaid.LOGGER.info("sssss");

		return super.shouldRun(world, entity);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();
		Optional<GlobalPos> home = maid.getHome();
		Optional<BlockPos> availableBed = brain.getOptionalMemory(ModMemories.AVAILABLE_BED);

		if (home.isPresent() && home.get().getDimension().equals(world.getRegistryKey())) {
			@Nullable Path path = maid.getNavigation().findPathTo(home.get().getPos(), 0);

			if (path != null && path.reachesTarget()) {
				maid.getBrain().remember(ModMemories.SLEEP_POS, home.get().getPos(), 100L);
			}
		}

		if (availableBed.isPresent()) {
			@Nullable Path path = maid.getNavigation().findPathTo(availableBed.get(), 0);

			if (path != null && path.reachesTarget()) {
				maid.getBrain().remember(ModMemories.SLEEP_POS, availableBed.get(), 100L);
			}
		}
	}
}
