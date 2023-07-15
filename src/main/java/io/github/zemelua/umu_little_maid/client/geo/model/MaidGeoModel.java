package io.github.zemelua.umu_little_maid.client.geo.model;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.job.MaidJobs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

import java.util.Optional;

@SuppressWarnings("unused")
public class MaidGeoModel extends DefaultedEntityGeoModel<LittleMaidEntity> {
	public static final String KEY_HEAD = EntityModelPartNames.HEAD;
	public static final String KEY_HAT = EntityModelPartNames.HAT;
	public static final String KEY_BODY = EntityModelPartNames.BODY;
	public static final String KEY_JACKET = EntityModelPartNames.JACKET;
	public static final String KEY_SKIRT = "skirt";
	public static final String KEY_APRON = "apron";
	public static final String KEY_LEFT_ARM = EntityModelPartNames.LEFT_ARM;
	public static final String KEY_LEFT_SLEEVE = "left_sleeve";
	public static final String KEY_RIGHT_ARM = EntityModelPartNames.RIGHT_ARM;
	public static final String KEY_RIGHT_SLEEVE = "right_sleeve";
	public static final String KEY_LEFT_LEG = EntityModelPartNames.LEFT_LEG;
	public static final String KEY_LEFT_PANTS = "left_pants";
	public static final String KEY_RIGHT_LEG = EntityModelPartNames.RIGHT_LEG;
	public static final String KEY_RIGHT_PANTS = "right_pants";
	public static final String KEY_BONE_USING_DRIPLEAF = "using_dripleaf_bone";
	public static final String KEY_BONE_CHANGING_COSTUME = "changing_costume_bone";

	public static final String KEY_ARMOR_HEAD = "armorHead";
	public static final String KEY_ARMOR_LEFT_LEG = "armorLeftLeg";
	public static final String KEY_ARMOR_RIGHT_LEG = "armorRightLeg";

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
	private static final Identifier RESOURCE_TEXTURE_SLEEPING = UMULittleMaid.identifier("textures/entity/little_maid/sleeping/little_maid_sleeping.png");
	private static final Identifier RESOURCE_TEXTURE_FENCER_SLEEPING = UMULittleMaid.identifier("textures/entity/little_maid/sleeping/little_maid_fencer_sleeping.png");
	private static final Identifier RESOURCE_TEXTURE_ARCHER_SLEEPING = UMULittleMaid.identifier("textures/entity/little_maid/sleeping/little_maid_archer_sleeping.png");
	private static final Identifier RESOURCE_TEXTURE_CRACKER_SLEEPING = UMULittleMaid.identifier("textures/entity/little_maid/sleeping/little_maid_cracker_sleeping.png");
	private static final Identifier RESOURCE_TEXTURE_GUARD_SLEEPING = UMULittleMaid.identifier("textures/entity/little_maid/sleeping/little_maid_guard_sleeping.png");
	private static final Identifier RESOURCE_TEXTURE_HEALER_SLEEPING = UMULittleMaid.identifier("textures/entity/little_maid/sleeping/little_maid_healer_sleeping.png");
	private static final Identifier RESOURCE_TEXTURE_POSEIDON_SLEEPING = UMULittleMaid.identifier("textures/entity/little_maid/sleeping/little_maid_poseidon_sleeping.png");
	private static final Identifier RESOURCE_TEXTURE_FARMER_SLEEPING = UMULittleMaid.identifier("textures/entity/little_maid/sleeping/little_maid_farmer_sleeping.png");
	private static final Identifier RESOURCE_TEXTURE_HUNTER_SLEEPING = UMULittleMaid.identifier("textures/entity/little_maid/sleeping/little_maid_hunter_sleeping.png");
	private static final Identifier RESOURCE_ANIMATION = UMULittleMaid.identifier("animations/little_maid.animation.json");
	private static final Identifier RESOURCE_ANIMATION_TRANSFORM = UMULittleMaid.identifier("animations/little_maid_transform.animation.json");

	public MaidGeoModel() {
		super(UMULittleMaid.identifier("little_maid"), false);
	}

	@Override
	public void setCustomAnimations(LittleMaidEntity maid, long instanceId, AnimationState<LittleMaidEntity> animationState) {
		Optional<GeoBone> head = this.getBone(KEY_HEAD);

		EntityModelData modelData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

		int unpause = !MinecraftClient.getInstance().isPaused() || maid.shouldPlayAnimsWhileGamePaused() ? 1 : 0;

		head.ifPresent(h -> {
			if (h.getRotX() == 0.0F && h.getRotY() == 0.0F && h.getRotZ() == 0.0F) {
				h.setRotX(h.getRotX() + (float) Math.toRadians(modelData.headPitch()) * unpause);
				h.setRotY(h.getRotY() + (float) Math.toRadians(modelData.netHeadYaw()) * unpause);
			}
		});
	}

	@Override
	public Identifier getModelResource(LittleMaidEntity maid) {
		return RESOURCE_MODEL;
	}

	@Override
	public Identifier getTextureResource(LittleMaidEntity maid) {
		if (maid.isHeadpatted()) {
			if (!maid.isVariableCostume()) return RESOURCE_TEXTURE_KYUN;
			else if (maid.getJob().equals(MaidJobs.FENCER)) return RESOURCE_TEXTURE_FENCER_KYUN;
			else if (maid.getJob().equals(MaidJobs.ARCHER)) return RESOURCE_TEXTURE_ARCHER_KYUN;
			else if (maid.getJob().equals(MaidJobs.CRACKER)) return RESOURCE_TEXTURE_CRACKER_KYUN;
			else if (maid.getJob().equals(MaidJobs.GUARD)) return RESOURCE_TEXTURE_GUARD_KYUN;
			else if (maid.getJob().equals(MaidJobs.HEALER)) return RESOURCE_TEXTURE_HEALER_KYUN;
			else if (maid.getJob().equals(MaidJobs.POSEIDON)) return RESOURCE_TEXTURE_POSEIDON_KYUN;
			else if (maid.getJob().equals(MaidJobs.FARMER)) return RESOURCE_TEXTURE_FARMER_KYUN;
			else if (maid.getJob().equals(MaidJobs.HUNTER)) return RESOURCE_TEXTURE_HUNTER_KYUN;
			else return RESOURCE_TEXTURE_KYUN;
		} else if (maid.isSleeping() || maid.isHealing()) {
			if (!maid.isVariableCostume()) return RESOURCE_TEXTURE_SLEEPING;
			else if (maid.getJob().equals(MaidJobs.FENCER)) return RESOURCE_TEXTURE_FENCER_SLEEPING;
			else if (maid.getJob().equals(MaidJobs.ARCHER)) return RESOURCE_TEXTURE_ARCHER_SLEEPING;
			else if (maid.getJob().equals(MaidJobs.CRACKER)) return RESOURCE_TEXTURE_CRACKER_SLEEPING;
			else if (maid.getJob().equals(MaidJobs.GUARD)) return RESOURCE_TEXTURE_GUARD_SLEEPING;
			else if (maid.getJob().equals(MaidJobs.HEALER)) return RESOURCE_TEXTURE_HEALER_SLEEPING;
			else if (maid.getJob().equals(MaidJobs.POSEIDON)) return RESOURCE_TEXTURE_POSEIDON_SLEEPING;
			else if (maid.getJob().equals(MaidJobs.FARMER)) return RESOURCE_TEXTURE_FARMER_SLEEPING;
			else if (maid.getJob().equals(MaidJobs.HUNTER)) return RESOURCE_TEXTURE_HUNTER_SLEEPING;
			else return RESOURCE_TEXTURE_SLEEPING;
		} else {
			if      (!maid.isVariableCostume())               return RESOURCE_TEXTURE;
			else if (maid.getJob().equals(MaidJobs.FENCER))   return RESOURCE_TEXTURE_FENCER;
			else if (maid.getJob().equals(MaidJobs.ARCHER))   return RESOURCE_TEXTURE_ARCHER;
			else if (maid.getJob().equals(MaidJobs.CRACKER))  return RESOURCE_TEXTURE_CRACKER;
			else if (maid.getJob().equals(MaidJobs.GUARD))    return RESOURCE_TEXTURE_GUARD;
			else if (maid.getJob().equals(MaidJobs.HEALER))   return RESOURCE_TEXTURE_HEALER;
			else if (maid.getJob().equals(MaidJobs.POSEIDON)) return RESOURCE_TEXTURE_POSEIDON;
			else if (maid.getJob().equals(MaidJobs.FARMER))   return RESOURCE_TEXTURE_FARMER;
			else if (maid.getJob().equals(MaidJobs.HUNTER))   return RESOURCE_TEXTURE_HUNTER;
			else                                              return RESOURCE_TEXTURE;
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
