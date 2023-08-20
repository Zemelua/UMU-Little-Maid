package io.github.zemelua.umu_little_maid.entity.brain.task.shear;

import net.minecraft.entity.Shearable;
import net.minecraft.sound.SoundCategory;

public interface IShearableWrapper<T extends Shearable> extends Shearable {
	T get();

	@Override
	default void sheared(SoundCategory soundCategory) {
		this.get().sheared(soundCategory);
	}

	@Override
	default boolean isShearable() {
		return this.get().isShearable();
	}
}
