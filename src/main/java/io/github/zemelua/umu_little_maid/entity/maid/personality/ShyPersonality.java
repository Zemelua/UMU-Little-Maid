package io.github.zemelua.umu_little_maid.entity.maid.personality;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;

public class ShyPersonality extends MaidPersonality {
	public ShyPersonality(MaidPersonality.Builder builder) {
		super(builder);
	}

	@Override
	public double getMinFollowDistance(LittleMaidEntity maid) {
		return Math.min(this.minFollowDistance - maid.getIntimacy() * 0.035D, this.minFollowDistance);
	}

	@Override
	public double getMaxFollowDistance(LittleMaidEntity maid) {
		return Math.min(this.maxFollowDistance - maid.getIntimacy() * 0.016D, this.maxFollowDistance);
	}
}
