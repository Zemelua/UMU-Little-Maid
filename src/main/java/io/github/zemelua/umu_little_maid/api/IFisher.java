package io.github.zemelua.umu_little_maid.api;

import io.github.zemelua.umu_little_maid.entity.ModFishingBobberEntity;

import java.util.Optional;

public interface IFisher {
	void onHitFish();
	Optional<ModFishingBobberEntity> getFishHook();
}
