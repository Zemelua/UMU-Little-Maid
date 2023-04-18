package io.github.zemelua.umu_little_maid.entity.maid;

import io.github.zemelua.umu_little_maid.api.IHeadpattable;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.util.ITameable;
import net.minecraft.entity.EntityPose;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.manager.AnimationData;

// LittleMaidEntity クラスがめっっちゃ長くなってきたので、こっちに書けるものはこっちに書いときます。
public sealed interface ILittleMaidEntity extends IAnimatable, ITameable, IHeadpattable permits LittleMaidEntity {
	@Override
	default void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "behavior", 1.5F, e -> {
			AnimationController<ILittleMaidEntity> controller = e.getController();
			AnimationBuilder builder = new AnimationBuilder();

			if (this.isHeadpatted()) {
				builder.addAnimation("headpatted", ILoopType.EDefaultLoopTypes.LOOP);
			} else if (this.isEating()) {
				builder.addAnimation("eat", ILoopType.EDefaultLoopTypes.LOOP);
			} else if (this.isSitting()) {
				builder.addAnimation("sit", ILoopType.EDefaultLoopTypes.LOOP);
			} else if (e.isMoving()) {
				builder.addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP);
			} else {
				builder.addAnimation("stand", ILoopType.EDefaultLoopTypes.LOOP);
			}

			controller.setAnimation(builder);

			return PlayState.CONTINUE;
		}));
	}

	default boolean isEating() {
		return this.getPose().equals(ModEntities.POSE_EATING);
	}

	/**
	 * @see LittleMaidEntity#getPoses()
	 */
	EntityPose getPose();
}
