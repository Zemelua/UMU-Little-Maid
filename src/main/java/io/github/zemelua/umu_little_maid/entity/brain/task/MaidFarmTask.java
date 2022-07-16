package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.block.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;

import java.util.Map;
import java.util.Optional;

public class MaidFarmTask extends Task<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_FARM_POS, MemoryModuleState.VALUE_PRESENT,
			ModEntities.MEMORY_FARM_COOLDOWN, MemoryModuleState.VALUE_ABSENT
	);

	public MaidFarmTask() {
		super(MaidFarmTask.REQUIRED_MEMORIES);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		return brain.getOptionalMemory(ModEntities.MEMORY_FARM_POS)
				.map(blockPos -> blockPos.isWithinDistance(maid.getPos(), 1.0D))
				.orElse(false);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		return this.shouldRun(world, maid);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();
		Optional<BlockPos> pos = brain.getOptionalMemory(ModEntities.MEMORY_FARM_POS);

		if (pos.isPresent()) {
			ItemStack crop = maid.getHasCrop();

			if (MaidFarmTask.isPlantable(pos.get(), world) && !crop.isEmpty()) {
				if (crop.isOf(Items.WHEAT_SEEDS)) {
					MaidFarmTask.plant(world, maid, pos.get(), Blocks.WHEAT.getDefaultState(), crop);
				} else if (crop.isOf(Items.POTATO)) {
					MaidFarmTask.plant(world, maid, pos.get(), Blocks.POTATOES.getDefaultState(), crop);
				} else if (crop.isOf(Items.CARROT)) {
					MaidFarmTask.plant(world, maid, pos.get(), Blocks.CARROTS.getDefaultState(), crop);
				} else if (crop.isOf(Items.BEETROOT_SEEDS)) {
					MaidFarmTask.plant(world, maid, pos.get(), Blocks.BEETROOTS.getDefaultState(), crop);
				}
			}

			if (MaidFarmTask.isHarvestable(pos.get(), world)) {
				world.breakBlock(pos.get(), true, maid);
				maid.swingHand(Hand.MAIN_HAND);
				brain.remember(ModEntities.MEMORY_FARM_COOLDOWN, 20);
				brain.forget(ModEntities.MEMORY_FARM_POS);
				brain.forget(MemoryModuleType.WALK_TARGET);
				brain.forget(MemoryModuleType.LOOK_TARGET);
			}
		}
	}

	private static void plant(ServerWorld world, LittleMaidEntity maid, BlockPos pos, BlockState farmland, ItemStack crop) {
		world.setBlockState(pos, farmland);
		world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(maid, farmland));
		world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
		crop.decrement(1);
		maid.swingHand(Hand.OFF_HAND);

		Brain<?> brain = maid.getBrain();
		brain.remember(ModEntities.MEMORY_FARM_COOLDOWN, 20);
		brain.forget(ModEntities.MEMORY_FARM_POS);
		brain.forget(MemoryModuleType.WALK_TARGET);
		brain.forget(MemoryModuleType.LOOK_TARGET);
	}

	public static boolean isPlantable(BlockPos pos, ServerWorld world) {
		BlockState blockState = world.getBlockState(pos);
		BlockState downState = world.getBlockState(pos.down());

		return downState.getBlock() instanceof FarmlandBlock && blockState.isAir();
	}

	public static boolean isHarvestable(BlockPos pos, ServerWorld world) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();

		return block instanceof CropBlock crop && crop.isMature(blockState);
	}
}
