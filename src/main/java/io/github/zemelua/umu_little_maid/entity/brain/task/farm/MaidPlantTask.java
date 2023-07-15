package io.github.zemelua.umu_little_maid.entity.brain.task.farm;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.action.EMaidAction;
import io.github.zemelua.umu_little_maid.mixin.AccessorMultiTickTask;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

import java.util.Optional;

public class MaidPlantTask extends MultiTickTask<LittleMaidEntity> {
	public MaidPlantTask() {
		super(ImmutableMap.of(ModMemories.FARM_POS, MemoryModuleState.VALUE_PRESENT), 23);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		if (maid.isHarvesting()) return false;

		Brain<LittleMaidEntity> brain = maid.getBrain();

		return brain.getOptionalRegisteredMemory(ModMemories.FARM_POS)
				.map(p -> p.isWithinDistance(maid.getPos(), 1.0D) && MaidFarmTaskOld.isPlantable(p, (ServerWorld) maid.getWorld()))
				.orElse(false);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		maid.setActionE(EMaidAction.PLANTING);

		Brain<LittleMaidEntity> brain = maid.getBrain();
		Optional<BlockPos> pos = brain.getOptionalRegisteredMemory(ModMemories.FARM_POS);
		pos.ifPresent(p -> brain.remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(p)));
		brain.forget(MemoryModuleType.WALK_TARGET);
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

		if (23L - (((AccessorMultiTickTask) this).getEndTime() - time) == 1L) {
			ItemStack crop = maid.getHasCrop();
			maid.setStackInHand(Hand.OFF_HAND, crop.split(1));
		} else if (23L - (((AccessorMultiTickTask) this).getEndTime() - time) == 12L) {
			ItemStack crop = maid.getHasCrop();

			if (crop.getItem() instanceof BlockItem cropBlockItem) {
				BlockState cropState = cropBlockItem.getBlock().getDefaultState();

				world.setBlockState(pos.get(), cropState);
				world.emitGameEvent(GameEvent.BLOCK_PLACE, pos.get(), GameEvent.Emitter.of(maid, cropState));
				world.playSound(null, pos.get().getX(), pos.get().getY(), pos.get().getZ(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
				crop.decrement(1);
			}
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
