package io.github.zemelua.umu_little_maid.entity.brain.task.farm;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.brain.sensor.MaidFarmablePosesSensor;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.action.HarvestAction;
import net.minecraft.block.*;
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
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;

import java.util.Map;
import java.util.Optional;

public class MaidFarmTaskOld extends MultiTickTask<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModMemories.FARM_POS, MemoryModuleState.VALUE_PRESENT,
			ModMemories.FARM_COOLDOWN, MemoryModuleState.VALUE_ABSENT
	);

	public MaidFarmTaskOld() {
		super(MaidFarmTaskOld.REQUIRED_MEMORIES);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		return brain.getOptionalRegisteredMemory(ModMemories.FARM_POS)
				.map(blockPos -> blockPos.isWithinDistance(maid.getPos(), 1.0D))
				.orElse(false);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();
		Optional<BlockPos> pos = brain.getOptionalRegisteredMemory(ModMemories.FARM_POS);

		pos.ifPresent(posValue -> {
			ItemStack crop = maid.getHasCrop();

			if (MaidFarmTaskOld.isPlantable(posValue, world) && !crop.isEmpty()) {
				if (crop.getItem() instanceof BlockItem cropItem) {
					MaidFarmTaskOld.plant(world, maid, posValue, cropItem.getBlock().getDefaultState(), crop);
				}
			}

			if (MaidFarmTaskOld.isHarvestable(posValue, world) || MaidFarmTaskOld.isGourd(posValue, world) && maid.canBreakGourd()) {
//				world.breakBlock(posValue, true, maid);
//				maid.swingHand(Hand.MAIN_HAND);

				maid.startAction(new HarvestAction(maid, posValue));

				MaidFarmTaskOld.resetMemories(brain, posValue);
			}

			if (MaidFarmablePosesSensor.canAnyFarm(world, maid, posValue)) {
				brain.getOptionalMemory(ModMemories.FARMABLE_POSES).ifPresent(poses -> poses.add(posValue));
			}
		});
	}

	public static void plant(ServerWorld world, LittleMaidEntity maid, BlockPos pos, BlockState farmland, ItemStack crop) {
		world.setBlockState(pos, farmland);
		world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Emitter.of(maid, farmland));
		world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
		crop.decrement(1);
		maid.swingHand(Hand.OFF_HAND);

		Brain<LittleMaidEntity> brain = maid.getBrain();
		MaidFarmTaskOld.resetMemories(brain, pos);
	}

	public static void resetMemories(Brain<LittleMaidEntity> brain, BlockPos pos) {
		// brain.remember(ModMemories.FARM_COOLDOWN, Unit.INSTANCE, 23L);
		brain.getOptionalMemory(ModMemories.FARMABLE_POSES).ifPresent(poses -> poses.remove(pos));
		brain.forget(ModMemories.FARM_POS);
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

		if (blockState.isIn(ModTags.BLOCK_MAID_HARVESTS)) {
			if (block instanceof CropBlock crop) {
				return crop.isMature(blockState);
			}

			return true;
		}

		return false;
	}

	public static boolean isGourd(BlockPos pos, ServerWorld world) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();

		if (blockState.isIn(ModTags.BLOCK_MAID_GOURDS)) {
			if (block instanceof GourdBlock gourd) {
				for (int i = 0; i < 4; i++) {
					Direction direction = Direction.fromHorizontal(i);
					BlockState sideState = world.getBlockState(pos.offset(direction));
					if (sideState.isOf(gourd.getAttachedStem())) {
						try {
							if (sideState.get(AttachedStemBlock.FACING).equals(direction.getOpposite())) {
								return true;
							}
						} catch (IllegalArgumentException exception) {
							UMULittleMaid.LOGGER.info(ModEntities.MARKER, "不明な原因でBlockからFACINGを得られなかったよ！");
						}
					}
				}

				return false;
			}

			return true;
		}

		return false;
	}
}
