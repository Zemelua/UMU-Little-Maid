package io.github.zemelua.umu_little_maid.client.geo;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class LittleMaidGeoModel extends AnimatedGeoModel<LittleMaidEntity> {
	private static final Identifier MODEL_RESOURCE = UMULittleMaid.identifier("geo/little_maid.geo.json");
	private static final Identifier TEXTURE_RESOURCE = UMULittleMaid.identifier("textures/entity/little_maid/little_maid.png");
	private static final Identifier ANIMATION_RESOURCE = UMULittleMaid.identifier("animations/little_maid.animation.json");

	@Override
	public Identifier getModelResource(LittleMaidEntity object) {
		return MODEL_RESOURCE;
	}

	@Override
	public Identifier getTextureResource(LittleMaidEntity object) {
		return TEXTURE_RESOURCE;
	}

	@Override
	public Identifier getAnimationResource(LittleMaidEntity animatable) {
		return ANIMATION_RESOURCE;
	}
}
