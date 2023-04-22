package io.github.zemelua.umu_little_maid.client.geo;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class LittleMaidGeoModel extends AnimatedGeoModel<LittleMaidEntity> {
	static final String KEY_HEAD = "head";
	static final String KEY_ARMOR_HEAD = "armorHead";
	static final String KEY_ARMOR_LEFT_LEG = "armorLeftLeg";
	static final String KEY_ARMOR_RIGHT_LEG = "armorRightLeg";
	static final String KEY_ITEM_LEFT_HAND = "itemLeftHand";
	static final String KEY_ITEM_RIGHT_HAND = "itemRightHand";
	static final String KEY_DRIPLEAF_RIGHT_HAND = "dripleaf_right_hand";

	private static final Identifier MODEL_RESOURCE = UMULittleMaid.identifier("geo/little_maid.geo.json");
	private static final Identifier MODEL_DRIPLEAF_RESOURCE = UMULittleMaid.identifier("geo/little_maid_dripleaf.geo.json");
	private static final Identifier TEXTURE_RESOURCE = UMULittleMaid.identifier("textures/entity/little_maid/little_maid.png");
	private static final Identifier ANIMATION_RESOURCE = UMULittleMaid.identifier("animations/little_maid.animation.json");
	private static final Identifier ANIMATION_TRANSFORM_RESOURCE = UMULittleMaid.identifier("animations/little_maid_transform.animation.json");

	@Override
	@SuppressWarnings("unchecked")
	public void setCustomAnimations(LittleMaidEntity maid, int instanceId, AnimationEvent animationEvent) {
		super.setCustomAnimations(maid, instanceId, animationEvent);

		IBone head = this.getBone(KEY_HEAD);

		EntityModelData modelData = (EntityModelData) animationEvent.getExtraDataOfType(EntityModelData.class).get(0);

		AnimationData manager = maid.getFactory().getOrCreateAnimationData(instanceId);
		int unpause = !MinecraftClient.getInstance().isPaused() || manager.shouldPlayWhilePaused ? 1 : 0;

		if (head != null && head.getRotationX() == 0.0F && head.getRotationY() == 0.0F && head.getRotationZ() == 0.0F) {
			head.setRotationX(head.getRotationX() + (float) Math.toRadians(modelData.headPitch) * unpause);
			head.setRotationY(head.getRotationY() + (float) Math.toRadians(modelData.netHeadYaw) * unpause);
		}
	}

	@Override
	public Identifier getModelResource(LittleMaidEntity maid) {
		// if (maid.isUsingDripleaf()) return MODEL_DRIPLEAF_RESOURCE;

		return MODEL_RESOURCE;
	}

	@Override
	public Identifier getTextureResource(LittleMaidEntity maid) {
		// TODO: getTextureの処理をモデル側に移す
		return maid.getTexture();
	}

	@Override
	public Identifier getAnimationResource(LittleMaidEntity animatable) {
		if (animatable.isTransforming()) return ANIMATION_TRANSFORM_RESOURCE;

		return ANIMATION_RESOURCE;
	}
}
