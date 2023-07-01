package io.github.zemelua.umu_little_maid.entity.maid;

import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import software.bernie.geckolib.core.animation.RawAnimation;

public interface IMaidItemAnimationSetter {
	default void setItemAnimation(LittleMaidEntity maid, RawAnimation builder) {
		if (maid.getMainArm().equals(Arm.LEFT)) {
			if (maid.getActiveHand().equals(Hand.MAIN_HAND)) {
				this.setItemAnimationWhenLeftIsActive(builder);
			} else {
				this.setItemAnimationWhenRightIsActive(builder);
			}
		} else {
			if (maid.getActiveHand().equals(Hand.MAIN_HAND)) {
				this.setItemAnimationWhenRightIsActive(builder);
			} else {
				this.setItemAnimationWhenLeftIsActive(builder);
			}
		}
	}

	void setItemAnimationWhenLeftIsActive(RawAnimation builder);
	void setItemAnimationWhenRightIsActive(RawAnimation builder);
}
