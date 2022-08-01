package io.github.zemelua.umu_little_maid.entity.brain.task.eat;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.mixin.TaskAccessor;
import io.github.zemelua.umu_little_maid.tag.ModTags;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

import java.util.Map;

public class MaidEatTask extends Task<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_SHOULD_EAT, MemoryModuleState.VALUE_PRESENT
	);
	private static final int USE_TIME = 16;

	private ItemStack eatingStack = ItemStack.EMPTY;
	private int eatingTicks = 0;

	public MaidEatTask() {
		super(MaidEatTask.REQUIRED_MEMORIES, MaidEatTask.USE_TIME);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		return !maid.getStackInHand(Hand.OFF_HAND).isEmpty();
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		this.eatingStack = MaidEatTask.searchHealItems(maid);
		this.eatingTicks = 0;

		maid.setStackInHand(Hand.OFF_HAND, this.eatingStack.copy().split(1));
		maid.playEatSound(this.eatingStack);
		maid.setPose(ModEntities.POSE_EATING);
		brain.forget(MemoryModuleType.ATTACK_TARGET);
		brain.forget(MemoryModuleType.WALK_TARGET);
		brain.forget(MemoryModuleType.LOOK_TARGET);
		brain.forget(MemoryModuleType.IS_PANICKING);
	}

	@Override
	protected void keepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		if (this.eatingTicks % 5 == 0) {
			maid.spawnEatingParticles();
		}

		this.eatingTicks++;
	}

	@Override
	protected void finishRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		this.eatingTicks = 0;
		maid.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);

		if (time > ((TaskAccessor<?>) this).getEndTime()) {
			maid.heal(5.0F);
			this.eatingStack.split(1);
		}
		maid.setPose(EntityPose.STANDING);
	}

	public static <E extends InventoryOwner> ItemStack searchHealItems(E inventoryOwner) {
		return ModUtils.searchInInventory(inventoryOwner.getInventory(), itemStackArg
				-> itemStackArg.isIn(ModTags.ITEM_MAID_HEAL_FOODS));
	}
}
