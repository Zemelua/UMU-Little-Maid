package io.github.zemelua.umu_little_maid.entity.maid;

import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;

import java.util.function.Consumer;

public enum MaidItemAnimationSetter implements IMaidItemAnimationSetter {
	BOW(builder -> builder.addAnimation("hold_bow_right", ILoopType.EDefaultLoopTypes.LOOP),
			builder -> builder.addAnimation("hold_bow_left", ILoopType.EDefaultLoopTypes.LOOP)),
	SPEAR(builder -> builder.addAnimation("hold_spear_right", ILoopType.EDefaultLoopTypes.LOOP),
			builder -> builder.addAnimation("hold_spear_left", ILoopType.EDefaultLoopTypes.LOOP));

	private final Consumer<AnimationBuilder> setterWhenLeftIsActive;
	private final Consumer<AnimationBuilder> setterWhenRightIsActive;

	MaidItemAnimationSetter(Consumer<AnimationBuilder> setterWhenLeftIsActive, Consumer<AnimationBuilder> setterWhenRightIsActive) {
		this.setterWhenLeftIsActive = setterWhenLeftIsActive;
		this.setterWhenRightIsActive = setterWhenRightIsActive;
	}


	@Override
	public void setItemAnimationWhenLeftIsActive(AnimationBuilder builder) {
		this.setterWhenLeftIsActive.accept(builder);
	}

	@Override
	public void setItemAnimationWhenRightIsActive(AnimationBuilder builder) {
		this.setterWhenRightIsActive.accept(builder);
	}
}
