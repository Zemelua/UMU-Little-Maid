package io.github.zemelua.umu_little_maid.entity.brain.task.farm;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.action.EMaidAction;
import io.github.zemelua.umu_little_maid.mixin.AccessorMultiTickTask;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class MaidHarvestTask extends MultiTickTask<LittleMaidEntity> {
	public MaidHarvestTask() {
		super(ImmutableMap.of(ModMemories.FARM_POS, MemoryModuleState.VALUE_PRESENT), 23);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		if (maid.isPlanting()) return false;

		Brain<LittleMaidEntity> brain = maid.getBrain();

		return brain.getOptionalRegisteredMemory(ModMemories.FARM_POS)
				.map(p -> p.isWithinDistance(maid.getPos(), 1.0D) && (MaidFarmTaskOld.isHarvestable(p, (ServerWorld) maid.getWorld()) || MaidFarmTaskOld.isGourd(p, world)))
				.orElse(false);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		maid.setActionE(EMaidAction.HARVESTING);

		Brain<LittleMaidEntity> brain = maid.getBrain();
		Optional<BlockPos> pos = brain.getOptionalRegisteredMemory(ModMemories.FARM_POS);
		pos.ifPresent(p -> brain.remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(p)));
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		return true;
	}

	@Override
	protected void keepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();
		Optional<BlockPos> pos = brain.getOptionalRegisteredMemory(ModMemories.FARM_POS);
		if (pos.isEmpty()) return;

		if (23L - (((AccessorMultiTickTask) this).getEndTime() - time) == 12L) {
			BlockState blockState = world.getBlockState(pos.get());
			@Nullable BlockEntity tile = world.getBlockEntity(pos.get());
			ItemStack tool = maid.getMainHandStack();
			List<ItemStack> drops = Block.getDroppedStacks(blockState, world, pos.get(), tile, maid, tool);

			ModUtils.Worlds.breakBlockWithoutDrop(world, pos.get(), maid);
			if (drops.size() > 0) {
				maid.setStackInHand(Hand.OFF_HAND, drops.get(0));
				drops.remove(0);
				drops.forEach(i -> maid.getInventory().addStack(i));
			}
		} else if (23L - (((AccessorMultiTickTask) this).getEndTime() - time) == 21L) {
			ItemStack drop = maid.getOffHandStack();
			maid.getInventory().addStack(drop.copy());
			maid.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		maid.removeActionE();

		Brain<LittleMaidEntity> brain = maid.getBrain();
		brain.forget(ModMemories.FARM_POS);
		brain.forget(MemoryModuleType.LOOK_TARGET);
		brain.forget(MemoryModuleType.WALK_TARGET);
	}
}
