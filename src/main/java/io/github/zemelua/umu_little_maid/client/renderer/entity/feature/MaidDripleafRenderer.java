package io.github.zemelua.umu_little_maid.client.renderer.entity.feature;

import io.github.zemelua.umu_little_maid.client.model.entity.LittleMaidEntityModel;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3f;

import java.util.Optional;

public class MaidDripleafRenderer<T extends LittleMaidEntity, M extends SinglePartEntityModel<T>> extends FeatureRenderer<T, M> {
	private final BlockRenderManager blockRenderManager;

	public MaidDripleafRenderer(FeatureRendererContext<T, M> context, BlockRenderManager blockRenderManager) {
		super(context);

		this.blockRenderManager = blockRenderManager;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T maid,
	                   float limbAngle,float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		if (maid.isGliding()) {
			Optional<ModelPart> bone = this.getContextModel().getChild(LittleMaidEntityModel.KEY_BONE_USING_DRIPLEAF);

			bone.ifPresent(boneObject -> {
				matrices.push();
				matrices.scale(-1.0F, -1.0F, 1.0F);
				boneObject.rotate(matrices);

				if (maid.isLeftHanded()) {
					matrices.translate(-0.4, -0.2D, 0.5D);
					matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
				} else {
					matrices.translate(0.4, -0.2D, -0.5D);
					matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
				}

				BlockState dripleaf = Blocks.BIG_DRIPLEAF.getDefaultState();
				this.blockRenderManager.renderBlockAsEntity(dripleaf, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);

				matrices.pop();
			});
		}
	}
}
