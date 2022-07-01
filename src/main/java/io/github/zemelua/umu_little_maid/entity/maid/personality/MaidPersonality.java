package io.github.zemelua.umu_little_maid.entity.maid.personality;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;

public class MaidPersonality {
	protected final boolean leap;
	protected final boolean curt;
	protected final double followStartDistance;
	protected final double followStopDistance;

	protected MaidPersonality(Builder builder) {
		this.leap = builder.leap;
		this.curt = builder.curt;
		this.followStartDistance = builder.followStartDistance;
		this.followStopDistance = builder.followStopDistance;
	}

	public boolean canLeapAtTarget(LittleMaidEntity maid) {
		return this.leap;
	}

	public boolean isCurt(LittleMaidEntity maid) {
		return this.curt;
	}

	public double getFollowStartDistance(LittleMaidEntity maid) {
		return this.followStartDistance;
	}

	public double getFollowStopDistance(LittleMaidEntity maid) {
		return this.followStopDistance;
	}

	protected static class Builder {
		private boolean leap = false;
		private boolean curt = false;
		private double followStartDistance = 10.0D;
		private double followStopDistance = 2.9D;

		protected Builder setLeap() {
			this.leap = true;

			return this;
		}

		protected Builder setCurt() {
			this.curt = true;

			return this;
		}

		protected Builder setFollowStartDistance(double followStartDistance) {
			this.followStartDistance = followStartDistance;

			return this;
		}

		protected Builder setFollowStopDistance(double followStopDistance) {
			this.followStopDistance = followStopDistance;

			return this;
		}
	}
}
