package io.github.zemelua.umu_little_maid.entity.maid.action;

import io.github.zemelua.umu_little_maid.entity.maid.ILittleMaidEntity;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.function.Supplier;

public interface IMaidAction {
	boolean tick();
	Type getType();

	enum Type {
		HARVEST(() -> ILittleMaidEntity.HARVEST),
		PLANT(() -> ILittleMaidEntity.PLANT);

		private final Supplier<RawAnimation> animation;

		Type(Supplier<RawAnimation> animation) {
			this.animation = animation;
		}

		public RawAnimation getAnimation() {
			return this.animation.get();
		}
	}
}
