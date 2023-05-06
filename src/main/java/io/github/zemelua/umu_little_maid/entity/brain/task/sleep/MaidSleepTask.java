package io.github.zemelua.umu_little_maid.entity.brain.task.sleep;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.OpenDoorsTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

/**
 * {@link net.minecraft.entity.ai.brain.task.SleepTask} の、{@link ModMemories#SLEEP_POS} 版です。
 */
public class MaidSleepTask extends Task<LittleMaidEntity> {
	private long startTime;

	public MaidSleepTask() {
		super(ImmutableMap.of(ModMemories.SLEEP_POS, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.LAST_WOKEN, MemoryModuleState.REGISTERED));
	}

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		long l;
		if (maid.hasVehicle()) {
			return false;
		}
		Brain<?> brain = maid.getBrain();
		BlockPos sleepPos = brain.getOptionalMemory(ModMemories.SLEEP_POS).get();
		Optional<Long> optional = brain.getOptionalMemory(MemoryModuleType.LAST_WOKEN);
		if (optional.isPresent() && (l = world.getTime() - optional.get()) > 0L && l < 100L) {
			return false;
		}
		BlockState blockState = world.getBlockState(sleepPos);
		return sleepPos.isWithinDistance(maid.getPos(), 2.0) && blockState.isIn(BlockTags.BEDS) && !blockState.get(BedBlock.OCCUPIED);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LittleMaidEntity entity, long time) {
		Optional<BlockPos> optional = entity.getBrain().getOptionalMemory(ModMemories.SLEEP_POS);
		if (optional.isEmpty()) {
			return false;
		}
		BlockPos blockPos = optional.get();
		return entity.getY() > (double)blockPos.getY() + 0.4 && blockPos.isWithinDistance(entity.getPos(), 1.14);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity entity, long time) {
		if (time > this.startTime) {
			OpenDoorsTask.pathToDoor(world, entity, null, null);
			entity.sleep(entity.getBrain().getOptionalMemory(ModMemories.SLEEP_POS).get());
		}
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	protected void finishRunning(ServerWorld world, LittleMaidEntity entity, long time) {
		if (entity.isSleeping()) {
			entity.wakeUp();
			this.startTime = time + 40L;
		}
	}
}
