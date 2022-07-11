package io.github.zemelua.umu_little_maid.entity.maid.job;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public record MaidJob(Predicate<ItemStack> itemStackPredicate) {
	public boolean canApply(LivingEntity entity) {
		return entity.isHolding(this.itemStackPredicate);
	}
}
