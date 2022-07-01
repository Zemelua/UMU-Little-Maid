package io.github.zemelua.umu_little_maid.entity.maid.personality;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;

public class ShyPersonality extends MaidPersonality {
	protected ShyPersonality(MaidPersonality.Builder builder) {
		super(builder);
	}

	@Override
	public double getFollowStartDistance(LittleMaidEntity maid) {
		return Math.min(this.followStartDistance - maid.getIntimacy() * 0.035D, 2.0D);
	}

	@Override
	public double getFollowStopDistance(LittleMaidEntity maid) {
		return Math.min(this.followStopDistance - maid.getIntimacy() * 0.016D, 0.5D);
	}
}
