package io.github.zemelua.umu_little_maid.entity.maid;

import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.function.Consumer;

public enum MaidItemAnimationSetter implements IMaidItemAnimationSetter {
	BOW(builder -> builder.thenLoop("hold_bow_right"),
			builder -> builder.thenLoop("hold_bow_left")),
	SPEAR(builder -> builder.thenLoop("hold_spear_right"),
			builder -> builder.thenLoop("hold_spear_left"));

	private final Consumer<RawAnimation> setterWhenLeftIsActive;
	private final Consumer<RawAnimation> setterWhenRightIsActive;

	MaidItemAnimationSetter(Consumer<RawAnimation> setterWhenLeftIsActive, Consumer<RawAnimation> setterWhenRightIsActive) {
		this.setterWhenLeftIsActive = setterWhenLeftIsActive;
		this.setterWhenRightIsActive = setterWhenRightIsActive;
	}


	@Override
	public void setItemAnimationWhenLeftIsActive(RawAnimation builder) {
		this.setterWhenLeftIsActive.accept(builder);
	}

	@Override
	public void setItemAnimationWhenRightIsActive(RawAnimation builder) {
		this.setterWhenRightIsActive.accept(builder);
	}
}
