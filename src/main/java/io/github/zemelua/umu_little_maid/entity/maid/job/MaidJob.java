package io.github.zemelua.umu_little_maid.entity.maid.job;

import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class MaidJob {
	private final Predicate<ItemStack> itemStackPredicate;
	private final Aggression aggression;
	private final boolean pounce;

	private MaidJob(MaidJob.Builder builder) {
		this.itemStackPredicate = builder.itemStackPredicate;
		this.aggression = builder.aggression;
		this.pounce = builder.pounce;
	}

	public boolean canApply(ItemStack itemStack) {
		return this.itemStackPredicate.test(itemStack);
	}

	public boolean isActive() {
		return this.aggression == Aggression.ACTIVE;
	}

	public boolean isGuard() {
		return this.aggression == Aggression.GUARD;
	}

	public boolean isAvoid() {
		return this.aggression == Aggression.AVOID;
	}

	public boolean canPounceAtTarget() {
		return this.pounce;
	}

	public static class Builder {
		private Predicate<ItemStack> itemStackPredicate = (itemStack -> false);
		private Aggression aggression = Aggression.AVOID;
		private boolean pounce = false;

		public Builder setItemStackPredicate(Predicate<ItemStack> itemStackPredicate) {
			this.itemStackPredicate = itemStackPredicate;

			return this;
		}

		public Builder setActive() {
			this.aggression = Aggression.ACTIVE;

			return this;
		}

		public Builder setGuard() {
			this.aggression = Aggression.GUARD;

			return this;
		}

		public Builder setPounce() {
			this.pounce = true;

			return this;
		}

		public MaidJob build() {
			return new MaidJob(this);
		}
	}

	private enum Aggression {
		ACTIVE,
		RANGED,
		GUARD,
		AVOID
	}
}
