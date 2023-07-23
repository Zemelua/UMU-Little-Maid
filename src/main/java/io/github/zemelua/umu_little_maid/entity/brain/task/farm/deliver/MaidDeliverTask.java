package io.github.zemelua.umu_little_maid.entity.brain.task.farm.deliver;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.ILittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.action.MaidAction;
import io.github.zemelua.umu_little_maid.mixin.AccessorMultiTickTask;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

import java.util.List;
import java.util.Optional;

public class MaidDeliverTask<M extends LivingEntity & ILittleMaidEntity> extends MultiTickTask<M> {
	public MaidDeliverTask() {
		super(ImmutableMap.of(ModMemories.DELIVERY_BOX, MemoryModuleState.VALUE_PRESENT), 999);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, M maid) {
		Brain<?> brain = maid.getBrain();
		Optional<BlockPos> boxPos = brain.getOptionalRegisteredMemory(ModMemories.DELIVERY_BOX);
		if (boxPos.isEmpty()) return false;

		if (maid.hasHarvests() && maid.canAction()) {
			return boxPos.get().isWithinDistance(maid.getPos(), 1.3D);
		}

		return false;
	}

	@Override
	protected void run(ServerWorld world, M maid, long time) {
		maid.setAction(MaidAction.DELIVERING);

		Brain<?> brain = maid.getBrain();
		brain.forget(MemoryModuleType.WALK_TARGET);
		Optional<BlockPos> boxPos = brain.getOptionalRegisteredMemory(ModMemories.DELIVERY_BOX);
		boxPos.ifPresent(p -> world.addSyncedBlockEvent(p, world.getBlockState(p).getBlock(), 1, 1));
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, M maid, long time) {
		if (999L - (((AccessorMultiTickTask) this).getEndTime() - time) < 20L) return true;

		Brain<?> brain = maid.getBrain();
		Optional<BlockPos> boxPos = brain.getOptionalRegisteredMemory(ModMemories.DELIVERY_BOX);
		if (boxPos.isEmpty() || !boxPos.get().isWithinDistance(maid.getPos(), 1.3D)) return false;

		Optional<Inventory> boxInventory = Optional.ofNullable(HopperBlockEntity.getInventoryAt(world, boxPos.get()));
		if (boxInventory.isEmpty()) return false;

		Inventory maidInventory = maid.getInventory();
		for (int i = 0; i < maidInventory.size(); i++) {
			ItemStack stack = maidInventory.getStack(i);
			if (!stack.isIn(ModTags.ITEM_MAID_HARVESTS)) continue;

			if (ModUtils.Inventories.canInsert(boxInventory.get(), stack)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void keepRunning(ServerWorld world, M maid, long time) {
		if ((999L - (((AccessorMultiTickTask) this).getEndTime() - time)) % 5L == 0L) {
			Brain<?> brain = maid.getBrain();
			Optional<BlockPos> boxPos = brain.getOptionalRegisteredMemory(ModMemories.DELIVERY_BOX);
			if (boxPos.isEmpty()) return;

			Optional<Inventory> boxInventory = Optional.ofNullable(HopperBlockEntity.getInventoryAt(world, boxPos.get()));
			if (boxInventory.isEmpty()) return;

			Inventory maidInventory = maid.getInventory();
			for (int i = 0; i < maidInventory.size(); i++) {
				ItemStack original = maidInventory.getStack(i).copy();
				if (!original.isIn(ModTags.ITEM_MAID_HARVESTS)) continue;

				ItemStack left = HopperBlockEntity.transfer(maidInventory, boxInventory.get(), original, null);
				if (left.isEmpty() || left.getCount() < original.getCount()) {
					maidInventory.setStack(i, left);
					return;
				}
			}
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, M maid, long time) {
		maid.removeAction();

		Brain<?> brain = maid.getBrain();
		Optional<BlockPos> boxPos = brain.getOptionalRegisteredMemory(ModMemories.DELIVERY_BOX);
		boxPos.ifPresent(p -> {
			int lookingCount = ChestBlockEntity.getPlayersLookingInChestCount(world, p);
			world.addSyncedBlockEvent(p, world.getBlockState(p).getBlock(), 1, lookingCount);
		});
		brain.forget(ModMemories.DELIVERY_BOX);

		if (boxPos.isPresent() && maid.hasHarvests()) {
			Optional<List<Pair<GlobalPos, Long>>> undeliverableBoxes = brain.getOptionalRegisteredMemory(ModMemories.UNDELIVERABLE_BOXES);
			ImmutableList.Builder<Pair<GlobalPos, Long>> builder = ImmutableList.builder();
			undeliverableBoxes.ifPresent(builder::addAll);
			builder.add(Pair.of(GlobalPos.create(world.getRegistryKey(), boxPos.get()), time + 200L));
			brain.remember(ModMemories.UNDELIVERABLE_BOXES, builder.build());
		}
	}
}
