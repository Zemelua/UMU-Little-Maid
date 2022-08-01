package io.github.zemelua.umu_little_maid.client.renderer.entity.feature;

import io.github.zemelua.umu_little_maid.client.model.entity.LittleMaidEntityModel;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Optional;

public class MaidHeldItemRenderer<T extends LittleMaidEntity, M extends SinglePartEntityModel<T> & ModelWithArms> extends HeldItemFeatureRenderer<T, M> {
	public MaidHeldItemRenderer(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
		super(context, heldItemRenderer);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, T maid,
	                   float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		Optional<ModelPart> bone = this.getContextModel().getChild(LittleMaidEntityModel.KEY_BONE_USING_DRIPLEAF);

		bone.ifPresentOrElse(boneObject -> {
			matrices.push();
			boneObject.rotate(matrices);

			super.render(matrices, vertexConsumerProvider, i, maid, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);

			matrices.pop();
		}, () -> super.render(matrices, vertexConsumerProvider, i, maid, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch));
	}
}
