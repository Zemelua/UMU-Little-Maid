package io.github.zemelua.umu_little_maid.entity.maid;

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
public sealed interface ILittleMaidEntity extends IAnimatable, ITameable permits LittleMaidEntity {
	@Override
	default void registerControllers(AnimationData data) {
		data.addAnimationController(new AnimationController<>(this, "behavior", 10, e -> {
			AnimationController<ILittleMaidEntity> controller = e.getController();
			AnimationBuilder builder = new AnimationBuilder();

			if (this.isEating()) {
				builder.addAnimation("eat", ILoopType.EDefaultLoopTypes.LOOP);
				controller.transitionLengthTicks = 1.5D;
			} else if (this.isSitting()) {
				builder.addAnimation("sit", ILoopType.EDefaultLoopTypes.LOOP);
				controller.transitionLengthTicks = 2.0D;
			} else if (e.isMoving()) {
				builder.addAnimation("walk", ILoopType.EDefaultLoopTypes.LOOP);
				controller.transitionLengthTicks = 2.0D;
			} else {
				builder.addAnimation("stand", ILoopType.EDefaultLoopTypes.LOOP);
				controller.transitionLengthTicks = 2.0D;
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
