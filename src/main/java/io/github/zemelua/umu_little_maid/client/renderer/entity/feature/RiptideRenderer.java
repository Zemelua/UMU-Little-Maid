package io.github.zemelua.umu_little_maid.client.renderer.entity.feature;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;

public class RiptideRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private static final Identifier TEXTURE = TridentRiptideFeatureRenderer.TEXTURE;

	private final ModelPart aura;

	public RiptideRenderer(FeatureRendererContext<T, M> context, EntityModelLoader entityModelLoader) {
		super(context);

		this.aura = entityModelLoader.getModelPart(EntityModelLayers.SPIN_ATTACK);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T living, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		if (!living.isUsingRiptide()) return;

		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(RiptideRenderer.TEXTURE));
		for (int i = 0; i < 3; i++) {
			matrices.push();

			float rotation = animationProgress * -(45 + i * 5);
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(rotation));
			float scale = 0.75F * i;
			matrices.scale(scale, scale, scale);
			matrices.translate(0.0D, -0.2D + 0.6D * i, 0.0D);
			this.aura.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);

			matrices.pop();
		}
	}
}
