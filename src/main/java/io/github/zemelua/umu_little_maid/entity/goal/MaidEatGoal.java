package io.github.zemelua.umu_little_maid.entity.goal;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import java.util.Arrays;
import java.util.EnumSet;

public class MaidEatGoal extends Goal {
	private static final int USE_TIME = 16;
	private static final Item[] EAT_ITEMS = new Item[]{Items.SUGAR};

	private final LittleMaidEntity maid;

	private ItemStack eatingStack = ItemStack.EMPTY;

	public MaidEatGoal(LittleMaidEntity maid) {
		this.maid = maid;

		this.setControls(EnumSet.of(Control.MOVE));
	}

	@Override
	public boolean canStart() {
		ItemStack itemStack = this.searchHealItems();

		if (!itemStack.isEmpty() && !this.maid.isUsingItem() && this.maid.getNavigation().isIdle()) {
			if (this.isInDangerOrTargeting()) {
				return this.maid.getHealth() < this.maid.getMaxHealth() * 0.4D;
			} else {
				return this.maid.getHealth() < this.maid.getMaxHealth();
			}
		}

		return false;
	}

	@Override
	public boolean shouldContinue() {
		return this.maid.getHealth() < this.maid.getMaxHealth()
				&& this.maid.getTarget() == null
				&& !this.maid.getStackInHand(Hand.OFF_HAND).isEmpty()
				&& this.maid.getEatingTicks() < MaidEatGoal.USE_TIME
				&& this.maid.getNavigation().isIdle();
	}

	@Override
	public void start() {
		this.eatingStack = this.searchHealItems();
		this.maid.setStackInHand(Hand.OFF_HAND, eatingStack.copy().split(1));
		this.maid.setTarget(null);
		this.maid.getNavigation().stop();
		this.maid.setEatingTicks(0);
	}

	@Override
	public void stop() {
		this.maid.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
		if (this.maid.getEatingTicks() >= MaidEatGoal.USE_TIME) {
			this.maid.heal(5.0F);
			this.eatingStack.split(1);
		}
		this.maid.setEatingTicks(0);
	}

	@Override
	public void tick() {
		this.maid.setEatingTicks(this.maid.getEatingTicks() + 1);
		this.maid.playEatingAnimation();
	}

	private boolean isInDangerOrTargeting() {
		return this.maid.getAttacker() != null || this.maid.shouldEscapePowderSnow() || this.maid.isOnFire() || this.maid.getTarget() != null;
	}

	private ItemStack searchHealItems() {
		return ModUtils.searchInInventory(this.maid.getInventory(), itemStackArg
				-> Arrays.stream(MaidEatGoal.EAT_ITEMS).anyMatch(itemStackArg::isOf));
	}
}
