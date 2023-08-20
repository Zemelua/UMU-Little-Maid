package io.github.zemelua.umu_little_maid.entity.brain.task.shear;

import net.minecraft.entity.Shearable;
import net.minecraft.entity.mob.MobEntity;

public class ShearableMobWrapper<T extends MobEntity & Shearable> implements IShearableWrapper<T> {
	private final T shearable;

	public ShearableMobWrapper(T shearable) {
		this.shearable = shearable;
	}

	@Override
	public T get() {
		return this.shearable;
	}
}
