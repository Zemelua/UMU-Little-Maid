package io.github.zemelua.umu_little_maid.client.renderer.entity.feature;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

public class MaidDripleafRenderer<T extends LittleMaidEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private final BlockState dripleaf = Blocks.BIG_DRIPLEAF.getDefaultState();
	private final BlockRenderManager blockRenderManager;
	private final ModelPart bone;

	public MaidDripleafRenderer(FeatureRendererContext<T, M> context, BlockRenderManager blockRenderManager, ModelPart bone) {
		super(context);

		this.blockRenderManager = blockRenderManager;
		this.bone = bone;
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T maid,
	                   float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		if (maid.isUsingDripleaf()) {
			matrices.push();
			matrices.scale(-1.0F, -1.0F, 1.0F);
			matrices.multiply(new Quaternion(this.bone.pitch, this.bone.yaw, this.bone.roll, false));

			if (maid.isLeftHanded()) {
				matrices.translate(-0.4, -0.2D, 0.5D);
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
			} else {
				matrices.translate(0.4, -0.2D, -0.5D);
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-90.0F));
			}

			this.blockRenderManager.renderBlockAsEntity(this.dripleaf, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);

			matrices.pop();
		}
	}
}
