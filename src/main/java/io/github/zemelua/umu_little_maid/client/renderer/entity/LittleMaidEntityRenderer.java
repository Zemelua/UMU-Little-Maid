package io.github.zemelua.umu_little_maid.client.renderer.entity;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.client.UMULittleMaidClient;
import io.github.zemelua.umu_little_maid.client.model.entity.LittleMaidEntityModel;
import io.github.zemelua.umu_little_maid.client.renderer.entity.feature.MaidDripleafRenderer;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class LittleMaidEntityRenderer extends MobEntityRenderer<LittleMaidEntity, LittleMaidEntityModel> {
	public LittleMaidEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new LittleMaidEntityModel(context.getPart(UMULittleMaidClient.LAYER_LITTLE_MAID)), 0.5F);

		this.addFeature(new HeldItemFeatureRenderer<>(this, context.getHeldItemRenderer()));
		this.addFeature(new MaidDripleafRenderer<>(this, context.getBlockRenderManager(), this.model.getBoneUsingDripleaf()));
	}

	@Override
	public void render(LittleMaidEntity maid, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		this.model.setUsingDripleaf(maid.isUsingDripleaf());
		
		super.render(maid, f, g, matrixStack, vertexConsumerProvider, i);
	}

	@Override
	public Identifier getTexture(LittleMaidEntity entity) {
		return UMULittleMaid.identifier("textures/entity/little_maid/little_maid.png");
	}
}
