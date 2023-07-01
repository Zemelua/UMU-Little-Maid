package io.github.zemelua.umu_little_maid.entity.maid;

import io.github.zemelua.umu_little_maid.api.IHeadpattable;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.action.MaidAction;
import io.github.zemelua.umu_little_maid.util.ITameable;
import net.minecraft.entity.EntityPose;
import software.bernie.geckolib.core.animatable.GeoAnimatable;

import java.util.Optional;

// LittleMaidEntity クラスがめっっちゃ長くなってきたので、こっちに書けるものはこっちに書いときます。
public sealed interface ILittleMaidEntity extends GeoAnimatable, ITameable, IHeadpattable permits LittleMaidEntity {
	Optional<MaidAction> getAction();

	default boolean canAction(MaidAction action) {
		Optional<MaidAction> currentAction = this.getAction();

		return currentAction.isEmpty() || currentAction.get().equals(action);
	}

	default boolean isEating() {
		return this.getPose().equals(ModEntities.POSE_EATING);
	}

	default boolean isTransforming() {
		return this.getPose().equals(ModEntities.POSE_CHANGING_COSTUME);
	}

	/**
	 * @see LittleMaidEntity#getPoses()
	 */
	EntityPose getPose();
}
