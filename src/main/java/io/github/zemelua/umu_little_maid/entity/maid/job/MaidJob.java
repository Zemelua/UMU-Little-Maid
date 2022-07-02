package io.github.zemelua.umu_little_maid.entity.maid.job;

public class MaidJob {
	private final Aggression aggression;
	private final boolean pounce;

	private MaidJob(MaidJob.Builder builder) {
		this.aggression = builder.aggression;
		this.pounce = builder.pounce;
	}

	public boolean isMad() {
		return this.aggression == Aggression.MAD;
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
		private Aggression aggression = Aggression.AVOID;
		private boolean pounce = false;

		public Builder setMad() {
			this.aggression = Aggression.MAD;

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
		MAD,
		ACTIVE,
		GUARD,
		AVOID
	}
}
