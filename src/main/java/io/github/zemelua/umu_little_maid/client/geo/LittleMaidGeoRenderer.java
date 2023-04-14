package io.github.zemelua.umu_little_maid.client.geo;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class LittleMaidGeoRenderer extends GeoEntityRenderer<LittleMaidEntity> {
	public LittleMaidGeoRenderer(EntityRendererFactory.Context renderManager) {
		super(renderManager, new LittleMaidGeoModel());
	}
}
