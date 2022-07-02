package io.github.zemelua.umu_little_maid.entity.maid.personality;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;

public class MaidPersonality {
	protected final boolean pounce;
	protected final boolean curt;
	protected final double minFollowDistance;
	protected final double maxFollowDistance;

	public MaidPersonality(MaidPersonality.Builder builder) {
		this.pounce = builder.pounce;
		this.curt = builder.curt;
		this.minFollowDistance = builder.minFollowDistance;
		this.maxFollowDistance = builder.maxFollowDistance;
	}

	public boolean canPounceAtTarget(LittleMaidEntity maid) {
		return this.pounce;
	}

	public boolean isCurt(LittleMaidEntity maid) {
		return this.curt;
	}

	public double getMinFollowDistance(LittleMaidEntity maid) {
		return this.minFollowDistance;
	}

	public double getMaxFollowDistance(LittleMaidEntity maid) {
		return this.maxFollowDistance;
	}

	public static class Builder {
		private boolean pounce = false;
		private boolean curt = false;
		private double minFollowDistance = 10.0D;
		private double maxFollowDistance = 2.3D;

		public Builder setPounce() {
			this.pounce = true;

			return this;
		}

		public Builder setCurt() {
			this.curt = true;

			return this;
		}

		public Builder setMinFollowDistance(double minFollowDistance) {
			this.minFollowDistance = minFollowDistance;

			return this;
		}

		public Builder setMaxFollowDistance(double maxFollowDistance) {
			this.maxFollowDistance = maxFollowDistance;

			return this;
		}

		public MaidPersonality build() {
			return new MaidPersonality(this);
		}
	}
}
