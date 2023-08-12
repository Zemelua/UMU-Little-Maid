package io.github.zemelua.umu_little_maid.client.geo.renderer;

import io.github.zemelua.umu_little_maid.client.geo.layer.MaidArmorGeoLayer;
import io.github.zemelua.umu_little_maid.client.geo.layer.MaidHeldItemGeoLayer;
import io.github.zemelua.umu_little_maid.client.geo.model.MaidGeoModel;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.renderer.DynamicGeoEntityRenderer;

public class MaidGeoRenderer extends DynamicGeoEntityRenderer<LittleMaidEntity> {
	public MaidGeoRenderer(EntityRendererFactory.Context renderManager) {
		super(renderManager, new MaidGeoModel());

		this.addRenderLayer(new MaidArmorGeoLayer(this));
		this.addRenderLayer(new MaidHeldItemGeoLayer(this));
	}

	@Override
	public void preRender(MatrixStack matrices, LittleMaidEntity maid, BakedGeoModel model,
	                      @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
	                      boolean isReRender, float tickDelta, int packedLight, int packedOverlay,
	                      float red, float green, float blue, float alpha) {
		this.model.getBone(MaidGeoModel.KEY_HEAD).ifPresent(head -> {
			MolangParser.INSTANCE.setValue("query.maid.head_pitch", head::getRotX);
			MolangParser.INSTANCE.setValue("query.maid.head_yaw", head::getRotY);
		});

		super.preRender(matrices, maid, model, bufferSource, buffer, isReRender, tickDelta, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected void applyRotations(LittleMaidEntity maid, MatrixStack matrices, float ageInTicks, float rotationYaw, float tickDelta) {
		super.applyRotations(maid, matrices, ageInTicks, rotationYaw, tickDelta);

		float leaningPitch = maid.getLeaningPitch(tickDelta);
		if (leaningPitch > 0.0F) {
			// UMULittleMaid.LOGGER.info(maid.getPitch());

			float pitch = maid.isTouchingWater() ? -90.0F - maid.getPitch() : -90.0F;
			float lerpedPitch = MathHelper.lerp(leaningPitch, 0.0f, pitch);

			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(lerpedPitch));
			if (maid.isInSwimmingPose()) {
				matrices.translate(0.0F, -1.0F, 0.3F);
			}
		}
	}
}
