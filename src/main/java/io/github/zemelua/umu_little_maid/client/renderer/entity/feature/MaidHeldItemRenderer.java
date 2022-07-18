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
import net.minecraft.util.math.Quaternion;

import java.util.Optional;

public class MaidHeldItemRenderer<T extends LittleMaidEntity, M extends SinglePartEntityModel<T> & ModelWithArms> extends HeldItemFeatureRenderer<T, M> {
	public MaidHeldItemRenderer(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
		super(context, heldItemRenderer);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
		Optional<ModelPart> bone = this.getContextModel().getChild(LittleMaidEntityModel.KEY_BONE_USING_DRIPLEAF);

		if (bone.isPresent()) {
			matrices.push();
			matrices.multiply(new Quaternion(bone.get().pitch, bone.get().yaw, bone.get().roll, false));
			super.render(matrices, vertexConsumerProvider, i, livingEntity, f, g, h, j, k, l);
			matrices.pop();
		} else {
			super.render(matrices, vertexConsumerProvider, i, livingEntity, f, g, h, j, k, l);
		}
	}
}
