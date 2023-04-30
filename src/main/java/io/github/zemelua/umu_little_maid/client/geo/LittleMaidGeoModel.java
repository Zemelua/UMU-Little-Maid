package io.github.zemelua.umu_little_maid.client.geo;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.job.MaidJobs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

@SuppressWarnings("unused")
public class LittleMaidGeoModel extends AnimatedGeoModel<LittleMaidEntity> {
	static final String KEY_HEAD = "head";
	static final String KEY_LEFT_ARM = "left_arm";
	static final String KEY_RIGHT_ARM = "right_arm";
	static final String KEY_ARMOR_HEAD = "armorHead";
	static final String KEY_ARMOR_LEFT_LEG = "armorLeftLeg";
	static final String KEY_ARMOR_RIGHT_LEG = "armorRightLeg";
	static final String KEY_ITEM_LEFT_HAND = "itemLeftHand";
	static final String KEY_ITEM_RIGHT_HAND = "itemRightHand";
	static final String KEY_DRIPLEAF_RIGHT_HAND = "dripleaf_right_hand";

	private static final Identifier RESOURCE_MODEL = UMULittleMaid.identifier("geo/little_maid.geo.json");
	private static final Identifier RESOURCE_TEXTURE = UMULittleMaid.identifier("textures/entity/little_maid/little_maid.png");
	private static final Identifier RESOURCE_TEXTURE_FENCER = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_fencer.png");
	private static final Identifier RESOURCE_TEXTURE_ARCHER = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_archer.png");
	private static final Identifier RESOURCE_TEXTURE_CRACKER = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_cracker.png");
	private static final Identifier RESOURCE_TEXTURE_GUARD = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_guard.png");
	private static final Identifier RESOURCE_TEXTURE_HEALER = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_healer.png");
	private static final Identifier RESOURCE_TEXTURE_POSEIDON = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_poseidon.png");
	private static final Identifier RESOURCE_TEXTURE_FARMER = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_farmer.png");
	private static final Identifier RESOURCE_TEXTURE_HUNTER = UMULittleMaid.identifier("textures/entity/little_maid/little_maid_hunter.png");
	private static final Identifier RESOURCE_TEXTURE_KYUN = UMULittleMaid.identifier("textures/entity/little_maid/kyun/little_maid_kyun.png");
	private static final Identifier RESOURCE_TEXTURE_FENCER_KYUN = UMULittleMaid.identifier("textures/entity/little_maid/kyun/little_maid_fencer_kyun.png");
	private static final Identifier RESOURCE_TEXTURE_ARCHER_KYUN = UMULittleMaid.identifier("textures/entity/little_maid/kyun/little_maid_archer_kyun.png");
	private static final Identifier RESOURCE_TEXTURE_CRACKER_KYUN = UMULittleMaid.identifier("textures/entity/little_maid/kyun/little_maid_cracker_kyun.png");
	private static final Identifier RESOURCE_TEXTURE_GUARD_KYUN = UMULittleMaid.identifier("textures/entity/little_maid/kyun/little_maid_guard_kyun.png");
	private static final Identifier RESOURCE_TEXTURE_HEALER_KYUN = UMULittleMaid.identifier("textures/entity/little_maid/kyun/little_maid_healer_kyun.png");
	private static final Identifier RESOURCE_TEXTURE_POSEIDON_KYUN = UMULittleMaid.identifier("textures/entity/little_maid/kyun/little_maid_poseidon_kyun.png");
	private static final Identifier RESOURCE_TEXTURE_FARMER_KYUN = UMULittleMaid.identifier("textures/entity/little_maid/kyun/little_maid_farmer_kyun.png");
	private static final Identifier RESOURCE_TEXTURE_HUNTER_KYUN = UMULittleMaid.identifier("textures/entity/little_maid/kyun/little_maid_hunter_kyun.png");
	private static final Identifier RESOURCE_ANIMATION = UMULittleMaid.identifier("animations/little_maid.animation.json");
	private static final Identifier RESOURCE_ANIMATION_TRANSFORM = UMULittleMaid.identifier("animations/little_maid_transform.animation.json");

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
		return RESOURCE_MODEL;
	}

	@Override
	public Identifier getTextureResource(LittleMaidEntity maid) {
		if (maid.isHeadpatted()) {
			if      (!maid.isVariableCostume())                      return RESOURCE_TEXTURE_KYUN;
			else if (maid.getJob().equals(MaidJobs.FENCER))   return RESOURCE_TEXTURE_FENCER_KYUN;
			else if (maid.getJob().equals(MaidJobs.ARCHER))   return RESOURCE_TEXTURE_ARCHER_KYUN;
			else if (maid.getJob().equals(MaidJobs.CRACKER))  return RESOURCE_TEXTURE_CRACKER_KYUN;
			else if (maid.getJob().equals(MaidJobs.GUARD))    return RESOURCE_TEXTURE_GUARD_KYUN;
			else if (maid.getJob().equals(MaidJobs.HEALER))   return RESOURCE_TEXTURE_HEALER_KYUN;
			else if (maid.getJob().equals(MaidJobs.POSEIDON)) return RESOURCE_TEXTURE_POSEIDON_KYUN;
			else if (maid.getJob().equals(MaidJobs.FARMER))   return RESOURCE_TEXTURE_FARMER_KYUN;
			else if (maid.getJob().equals(MaidJobs.HUNTER))   return RESOURCE_TEXTURE_HUNTER_KYUN;
			else                                                     return RESOURCE_TEXTURE_KYUN;
		} else {
			if      (!maid.isVariableCostume())                      return RESOURCE_TEXTURE;
			else if (maid.getJob().equals(MaidJobs.FENCER))   return RESOURCE_TEXTURE_FENCER;
			else if (maid.getJob().equals(MaidJobs.ARCHER))   return RESOURCE_TEXTURE_ARCHER;
			else if (maid.getJob().equals(MaidJobs.CRACKER))  return RESOURCE_TEXTURE_CRACKER;
			else if (maid.getJob().equals(MaidJobs.GUARD))    return RESOURCE_TEXTURE_GUARD;
			else if (maid.getJob().equals(MaidJobs.HEALER))   return RESOURCE_TEXTURE_HEALER;
			else if (maid.getJob().equals(MaidJobs.POSEIDON)) return RESOURCE_TEXTURE_POSEIDON;
			else if (maid.getJob().equals(MaidJobs.FARMER))   return RESOURCE_TEXTURE_FARMER;
			else if (maid.getJob().equals(MaidJobs.HUNTER))   return RESOURCE_TEXTURE_HUNTER;
			else                                                     return RESOURCE_TEXTURE;
		}
	}

	@Override
	public Identifier getAnimationResource(LittleMaidEntity maid) {
		// BlockBenchで保存すると、中途半端な値が揃えられてしまうため、これ以上の編集、保存をする必要がないようにファイル
		// を分離します。つまり、これは完全に開発上の都合であり、仕組みとしては分ける意味はありません。
		if (maid.isTransforming()) return RESOURCE_ANIMATION_TRANSFORM;

		return RESOURCE_ANIMATION;
	}
}
