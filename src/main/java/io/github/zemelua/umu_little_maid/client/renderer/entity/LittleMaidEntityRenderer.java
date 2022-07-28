package io.github.zemelua.umu_little_maid.client.renderer.entity;

import io.github.zemelua.umu_little_maid.client.UMULittleMaidClient;
import io.github.zemelua.umu_little_maid.client.model.entity.LittleMaidEntityModel;
import io.github.zemelua.umu_little_maid.client.renderer.entity.feature.MaidDripleafRenderer;
import io.github.zemelua.umu_little_maid.client.renderer.entity.feature.MaidHeldItemRenderer;
import io.github.zemelua.umu_little_maid.client.renderer.entity.feature.RiptideRenderer;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class LittleMaidEntityRenderer extends MobEntityRenderer<LittleMaidEntity, LittleMaidEntityModel> {
	public LittleMaidEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new LittleMaidEntityModel(context.getPart(UMULittleMaidClient.LAYER_LITTLE_MAID)), 0.5F);

		this.addFeature(new MaidHeldItemRenderer<>(this, context.getHeldItemRenderer()));
		this.addFeature(new MaidDripleafRenderer<>(this, context.getBlockRenderManager()));
		this.addFeature(new RiptideRenderer<>(this, context.getModelLoader()));
	}

	@Override
	public void render(LittleMaidEntity maid, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		this.model.setUsingDripleaf(maid.isUsingDripleaf());
		
		super.render(maid, f, g, matrixStack, vertexConsumerProvider, i);
	}

	@Override
	protected void setupTransforms(LittleMaidEntity maid, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
		float leaningPitch = maid.getLeaningPitch(tickDelta);

		super.setupTransforms(maid, matrices, animationProgress, bodyYaw, tickDelta);

		if (leaningPitch > 0.0F) {
			float maxPitch = maid.isTouchingWater() ? -90.0F - maid.getPitch() : -90.0F;
			float pitch = MathHelper.lerp(leaningPitch, 0.0F, maxPitch);
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(pitch));
			if (maid.isInSwimmingPose()) {
				matrices.translate(0.0F, -1.0F, 0.3F);
			}
		}
	}

	@Override
	public Identifier getTexture(LittleMaidEntity entity) {
		return entity.getTexture();
	}
}
