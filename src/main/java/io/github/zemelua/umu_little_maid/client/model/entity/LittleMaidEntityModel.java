package io.github.zemelua.umu_little_maid.client.model.entity;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class LittleMaidEntityModel extends EntityModel<LittleMaidEntity> {
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart skirt;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	public LittleMaidEntityModel(ModelPart base) {
		this.head = base.getChild("head");
		this.body = base.getChild("body");
		this.skirt = base.getChild("skirt");
		this.rightArm = base.getChild("right_arm");
		this.leftArm = base.getChild("left_arm");
		this.rightLeg = base.getChild("right_leg");
		this.leftLeg = base.getChild("left_leg");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		modelPartData.addChild("head", ModelPartBuilder.create()
						.uv(0, 0).cuboid("head", -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
						.uv(0, 32).cuboid("hat", -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.5F)),
				ModelTransform.pivot(0.0F, 8.0F, 0.0F)
		);
		modelPartData.addChild("body", ModelPartBuilder.create()
						.uv(0, 16).cuboid("body", -3.0F, 0.0F, -2.0F, 6.0F, 9.0F, 4.0F, new Dilation(0.0F))
						.uv(0, 48).cuboid("jacket", -3.0F, 0.0F, -2.0F, 6.0F, 9.0F, 4.0F, new Dilation(0.25F)),
				ModelTransform.pivot(0.0F, 8.0F, 0.0F)
		);
		modelPartData.addChild("skirt", ModelPartBuilder.create()
						.uv(32, 0).cuboid("skirt", -4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
						.uv(32, 33).cuboid("apron", -4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.5F)),
				ModelTransform.pivot(0.0F, 14.0F, 0.0F)
		);
		modelPartData.addChild("right_arm", ModelPartBuilder.create()
						.uv(20, 16).cuboid("right_arm", -1.0F, -1.5F, -1.5F, 2.0F, 9.0F, 2.0F, new Dilation(0.0F))
						.uv(20, 50).cuboid("right_sleeve", -1.0F, -1.5F, -1.5F, 2.0F, 9.0F, 2.0F, new Dilation(0.25F)),
				ModelTransform.pivot(-4.0F, 9.5F, 0.5F)
		);
		modelPartData.addChild("left_arm", ModelPartBuilder.create()
						.uv(28, 16).cuboid("left_arm", -1.0F, -1.5F, -1.5F, 2.0F, 9.0F, 2.0F, new Dilation(0.0F))
						.uv(28, 50).cuboid("left_sleeve", -1.0F, -1.5F, -1.5F, 2.0F, 9.0F, 2.0F, new Dilation(0.25F)),
				ModelTransform.pivot(4.0F, 9.5F, 0.5F)
		);
		modelPartData.addChild("right_leg", ModelPartBuilder.create()
						.uv(36, 16).cuboid("right_leg", -1.5F, 0.0F, -2.0F, 3.0F, 7.0F, 4.0F, new Dilation(0.0F))
						.uv(36, 50).cuboid("right_pants", -1.5F, 0.0F, -2.0F, 3.0F, 7.0F, 4.0F, new Dilation(0.25F)),
				ModelTransform.pivot(-1.5F, 17.0F, 0.0F)
		);
		modelPartData.addChild("left_leg", ModelPartBuilder.create()
						.uv(50, 16).cuboid("left_leg", -1.5F, 0.0F, -2.0F, 3.0F, 7.0F, 4.0F, new Dilation(0.0F))
						.uv(50, 50).cuboid("left_pants", -1.5F, 0.0F, -2.0F, 3.0F, 7.0F, 4.0F, new Dilation(0.25F)),
				ModelTransform.pivot(1.5F, 17.0F, 0.0F)
		);

		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void setAngles(LittleMaidEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.head.pitch = (float) Math.toRadians(headPitch);
		this.head.yaw = (float) Math.toRadians(headYaw);
	}

	@Override
	public void animateModel(LittleMaidEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		if (entity.isSitting()) {
			this.head.roll = (float) Math.toRadians(13.7F);
			this.rightArm.pitch = (float) Math.toRadians(-42.0);
			this.rightArm.yaw = (float) Math.toRadians(0.0F);
			this.rightArm.roll = (float) Math.toRadians(-25.0F);
			this.leftArm.pitch = (float) Math.toRadians(-42.0F);
			this.leftArm.yaw = (float) Math.toRadians(0.0F);
			this.leftArm.roll = (float) Math.toRadians(25.0F);
		} else {
			this.head.roll = 0.0F;
			this.rightArm.roll = (float) Math.toRadians(15.0F);
			this.leftArm.roll = (float) Math.toRadians(-15.0F);
		}
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		this.head.render(matrixStack, vertexConsumer, light, overlay, red, green, blue, alpha);
		this.body.render(matrixStack, vertexConsumer, light, overlay, red, green, blue, alpha);
		this.skirt.render(matrixStack, vertexConsumer, light, overlay, red, green, blue, alpha);
		this.rightArm.render(matrixStack, vertexConsumer, light, overlay, red, green, blue, alpha);
		this.leftArm.render(matrixStack, vertexConsumer, light, overlay, red, green, blue, alpha);
		this.rightLeg.render(matrixStack, vertexConsumer, light, overlay, red, green, blue, alpha);
		this.leftLeg.render(matrixStack, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}
