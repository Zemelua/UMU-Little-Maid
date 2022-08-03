package io.github.zemelua.umu_little_maid.util;

public final class ItemParticleScaleManager {
	private static float size = 1.0F;

	public static float getSize() {
		return ItemParticleScaleManager.size;
	}

	public static void setSize(float value) {
		ItemParticleScaleManager.size = value;
	}
}
