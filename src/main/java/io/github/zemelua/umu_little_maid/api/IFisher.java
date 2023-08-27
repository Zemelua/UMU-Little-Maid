package io.github.zemelua.umu_little_maid.api;

import io.github.zemelua.umu_little_maid.entity.ModFishingBobberEntity;

import java.util.Optional;

public interface IFisher {
	void onThrowFishHook(ModFishingBobberEntity fishHook);
	void onHitFish();
	void pullFishRod();
	Optional<ModFishingBobberEntity> getFishHook();
}
