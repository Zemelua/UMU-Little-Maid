package io.github.zemelua.umu_little_maid.entity.brain.task.eat;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class MaidEatTask extends Task<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModMemories.SHOULD_EAT, MemoryModuleState.VALUE_PRESENT
	);

	public MaidEatTask() {
		super(MaidEatTask.REQUIRED_MEMORIES, 0);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		return maid.getPose() != ModEntities.POSE_EATING;
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return true;
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		maid.eatFood(MaidEatTask.searchHealItems(maid).split(1), food -> maid.heal(5.0F));
	}

	public static <E extends InventoryOwner> ItemStack searchHealItems(E inventoryOwner) {
		return ModUtils.searchInInventory(inventoryOwner.getInventory(), itemStackArg
				-> itemStackArg.isIn(ModTags.ITEM_MAID_HEAL_FOODS));
	}
}
