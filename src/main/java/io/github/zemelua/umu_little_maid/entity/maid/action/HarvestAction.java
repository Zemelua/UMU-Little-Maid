package io.github.zemelua.umu_little_maid.entity.maid.action;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HarvestAction extends AbstractMaidAction {
	private final LittleMaidEntity maid;
	private final BlockPos pos;

	public HarvestAction(LittleMaidEntity maid, BlockPos pos) {
		super(23, Type.HARVEST);

		this.maid = maid;
		this.pos = pos;
	}

	@Override
	protected void tick(int ticks) {
		if (ticks == 12) {
			World world = maid.getWorld();
			BlockState blockState = world.getBlockState(this.pos);
			@Nullable BlockEntity tile = world.getBlockEntity(this.pos);
			ItemStack tool = maid.getMainHandStack();
			List<ItemStack> drops = Block.getDroppedStacks(blockState, (ServerWorld) world, this.pos, tile, this.maid, tool);

			ModUtils.Worlds.breakBlockWithoutDrop(world, this.pos, this.maid);
			this.maid.setStackInHand(Hand.OFF_HAND, drops.stream().findFirst().orElse(ItemStack.EMPTY));
		} else if (ticks == 20) {
			ItemStack drop = maid.getOffHandStack();
			this.maid.getInventory().addStack(drop.copy());
			maid.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
		}
	}
}
