package io.github.zemelua.umu_little_maid.entity.maid.action;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class PlantAction extends AbstractMaidAction {
	private final LittleMaidEntity maid;
	private final BlockPos pos;

	public PlantAction(LittleMaidEntity maid, BlockPos pos) {
		super(23, Type.PLANT);

		this.maid = maid;
		this.pos = pos;
	}

	@Override
	protected void tick(int ticks) {
		if (ticks == 1) {
			ItemStack crop = this.maid.getHasCrop();
			this.maid.setStackInHand(Hand.OFF_HAND, crop.split(1));
		} else if (ticks == 12) {
			World world = this.maid.getWorld();
			ItemStack crop = this.maid.getHasCrop();

			if (crop.getItem() instanceof BlockItem cropBlockItem) {
				BlockState cropState = cropBlockItem.getBlock().getDefaultState();

				world.setBlockState(this.pos, cropState);
				world.emitGameEvent(GameEvent.BLOCK_PLACE, this.pos, GameEvent.Emitter.of(this.maid, cropState));
				world.playSound(null, this.pos.getX(), this.pos.getY(), this.pos.getZ(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
				crop.decrement(1);
			}

//			Brain<LittleMaidEntity> brain = maid.getBrain();
//			MaidFarmTaskOld.resetMemories(brain, pos);
		}
	}
}
