package io.github.zemelua.umu_little_maid.client.model.entity;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

public class LittleMaidEntityModel extends EntityModel<LittleMaidEntity> {
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart skirt;
	private final ModelPart rightArm;
	private final ModelPart leftArm;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;

	private BipedEntityModel.ArmPose rightArmPose;
	private BipedEntityModel.ArmPose leftArmPose;

	public float leaningPitch;

	public LittleMaidEntityModel(ModelPart base) {
		this.head = base.getChild("head");
		this.body = base.getChild("body");
		this.skirt = base.getChild("skirt");
		this.rightArm = base.getChild("right_arm");
		this.leftArm = base.getChild("left_arm");
		this.rightLeg = base.getChild("right_leg");
		this.leftLeg = base.getChild("left_leg");

		this.rightArmPose = BipedEntityModel.ArmPose.EMPTY;
		this.leftArmPose = BipedEntityModel.ArmPose.EMPTY;
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
			this.rightArm.pitch = (-0.2f + 1.5f * MathHelper.wrap(limbAngle, 13.0f)) * limbDistance;
			this.rightArm.yaw = 0.0F;
			this.rightArm.roll = (float) Math.toRadians(15.0F);
			this.leftArm.pitch = (-0.2f - 1.5f * MathHelper.wrap(limbAngle, 13.0f)) * limbDistance;
			this.leftArm.yaw = 0.0F;
			this.leftArm.roll = (float) Math.toRadians(-15.0F);
		}

		this.rightLeg.pitch = -1.5f * MathHelper.wrap(limbAngle, 13.0f) * limbDistance;
		this.leftLeg.pitch = 1.5f * MathHelper.wrap(limbAngle, 13.0f) * limbDistance;

		if (this.rightArmPose != BipedEntityModel.ArmPose.SPYGLASS) {
			CrossbowPosing.swingArm(this.rightArm, animationProgress, 1.0f);
		}
		if (this.leftArmPose != BipedEntityModel.ArmPose.SPYGLASS) {
			CrossbowPosing.swingArm(this.leftArm, animationProgress, -1.0f);
		}


		// 攻撃時の手の動き
		Arm arm = entity.getMainArm();
		Hand hand = entity.preferredHand;
		if (hand != Hand.MAIN_HAND) arm = arm.getOpposite();
		ModelPart modelPart = arm == Arm.LEFT ? this.leftArm : this.rightArm;

		float progress = 1.0f - this.handSwingProgress;
		progress *= progress;
		progress *= progress;
		progress = 1.0f - progress;
		float g = MathHelper.sin(progress * (float) Math.PI);
		float h = MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -(this.head.pitch - 0.7f) * 0.75f;
		modelPart.pitch -= g * 1.2f + h;
		modelPart.yaw += this.body.yaw * 2.0f;
		modelPart.roll += MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -0.4f;
	}

	@Override
	public void animateModel(LittleMaidEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		this.leaningPitch = entity.getLeaningPitch(tickDelta);
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
