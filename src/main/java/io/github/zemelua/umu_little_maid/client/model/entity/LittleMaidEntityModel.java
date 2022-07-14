package io.github.zemelua.umu_little_maid.client.model.entity;

import io.github.zemelua.umu_little_maid.client.UMULittleMaidClient;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AnimationState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class LittleMaidEntityModel extends SinglePartEntityModel<LittleMaidEntity> implements ModelWithArms {
	private final ModelPart base;
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
		this.base = base;
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
	public void setAngles(LittleMaidEntity maid, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.head.pitch = (float) Math.toRadians(headPitch);
		this.head.yaw = (float) Math.toRadians(headYaw);

		if (maid.isSitting()) {
			this.setSittingAngle();
		} else {
			this.setStandingAngle(limbAngle, limbDistance);
		}

		// 手のポーズ(弓とか)
		boolean mainArm = maid.getMainArm() == Arm.RIGHT;
		if (maid.isUsingItem()) {
			boolean activeHand = maid.getActiveHand() == Hand.MAIN_HAND;
			if (mainArm == activeHand) {
				this.positionRightArm(maid);
			} else {
				this.positionLeftArm(maid);
			}
		} else {
			boolean isTwoHanded = mainArm ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
			if (mainArm != isTwoHanded) {
				this.positionLeftArm(maid);
				this.positionRightArm(maid);
			} else {
				this.positionRightArm(maid);
				this.positionLeftArm(maid);
			}
		}

		// 手の揺れ
		if (this.rightArmPose != BipedEntityModel.ArmPose.SPYGLASS) {
			CrossbowPosing.swingArm(this.rightArm, animationProgress, 1.0f);
		}
		if (this.leftArmPose != BipedEntityModel.ArmPose.SPYGLASS) {
			CrossbowPosing.swingArm(this.leftArm, animationProgress, -1.0f);
		}

		// 攻撃時の手の動き
		this.adaptAttackingAngel(maid.getMainArm());

		this.updateAnimation(maid.getEatAnimation(), UMULittleMaidClient.MAID_EAT_ANIMATION, animationProgress);
	}

	private void setStandingAngle(float limbAngle, float limbDistance) {
		this.head.roll = (float) Math.toRadians(0.0F);
		this.leftArm.pitch = MathHelper.cos(limbAngle * 0.6662F) * limbDistance;
		this.leftArm.yaw = (float) Math.toRadians(0.0F);
		this.leftArm.roll = (float) Math.toRadians(-15.0F);
		this.rightArm.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * limbDistance;
		this.rightArm.yaw = (float) Math.toRadians(0.0F);
		this.rightArm.roll = (float) Math.toRadians(15.0F);
		this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
	}

	private void setSittingAngle() {
		this.head.roll = (float) Math.toRadians(13.7F);
		this.leftArm.pitch = (float) Math.toRadians(-42.0F);
		this.leftArm.yaw = (float) Math.toRadians(0.0F);
		this.leftArm.roll = (float) Math.toRadians(25.0F);
		this.rightArm.pitch = (float) Math.toRadians(-42.0);
		this.rightArm.yaw = (float) Math.toRadians(0.0F);
		this.rightArm.roll = (float) Math.toRadians(-25.0F);
	}

	private void adaptAttackingAngel(Arm attackingArm) {
		ModelPart armPart = this.getArm(attackingArm);

		float progress = 1.0F - (float) Math.pow(1.0D - this.handSwingProgress, 3.0D);
		float g = MathHelper.sin(progress * (float) Math.PI);
		float h = MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -(this.head.pitch - 0.7F) * 0.75F;
		armPart.pitch -= g * 1.2F + h;
		armPart.yaw += this.body.yaw * 2.0F;
		armPart.roll += MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -0.4F;
	}

	@Override
	protected void updateAnimation(AnimationState animationState, Animation animation, float animationProgress, float speedMultiplier) {
		animationState.update(animationProgress, speedMultiplier);
		animationState.run(state -> AnimationHelper.animate(this, animation, state.getTimeRunning(), 1.0f, Vec3f.ZERO));
	}

	@Override
	public void animateModel(LittleMaidEntity entity, float limbAngle, float limbDistance, float tickDelta) {
		this.leaningPitch = entity.getLeaningPitch(tickDelta);

		this.rightArmPose = BipedEntityModel.ArmPose.EMPTY;
		this.leftArmPose = BipedEntityModel.ArmPose.EMPTY;

		// 手に持ってるアイテムでArmPoseを切り替え
		ItemStack itemStack = entity.getMainHandStack();
		if (itemStack.isOf(Items.BOW) && entity.isAttacking()) {
			if (entity.getMainArm() == Arm.LEFT) {
				this.leftArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
			} else {
				this.rightArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
			}
		} else if (itemStack.isOf(Items.SHIELD) && entity.isBlocking()) {
			if (entity.getMainArm() == Arm.LEFT) {
				this.leftArmPose = BipedEntityModel.ArmPose.BLOCK;
			} else {
				this.rightArmPose = BipedEntityModel.ArmPose.BLOCK;
			}
		}
	}

	private void positionRightArm(LittleMaidEntity entity) {
		switch (this.rightArmPose) {
			case EMPTY -> this.rightArm.yaw = 0.0f;
			case BLOCK -> {
				this.rightArm.pitch = this.rightArm.pitch * 0.5f - 0.9424779f;
				this.rightArm.yaw = -0.5235988f;
			}
			case ITEM -> {
				this.rightArm.pitch = this.rightArm.pitch * 0.5f - 0.31415927f;
				this.rightArm.yaw = 0.0f;
			}
			case THROW_SPEAR -> {
				this.rightArm.pitch = this.rightArm.pitch * 0.5f - (float) Math.PI;
				this.rightArm.yaw = 0.0f;
			}
			case BOW_AND_ARROW -> {
				this.rightArm.yaw = -0.1f + this.head.yaw;
				this.leftArm.yaw = 0.1f + this.head.yaw + 0.4f;
				this.rightArm.pitch = -1.5707964f + this.head.pitch;
				this.leftArm.pitch = -1.5707964f + this.head.pitch;
			}
			case CROSSBOW_CHARGE -> CrossbowPosing.charge(this.rightArm, this.leftArm, entity, true);
			case CROSSBOW_HOLD -> CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, true);
			case SPYGLASS -> {
				this.rightArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622f - (entity.isInSneakingPose() ? 0.2617994f : 0.0f), -2.4f, 3.3f);
				this.rightArm.yaw = this.head.yaw - 0.2617994f;
			}
			case TOOT_HORN -> {
				this.rightArm.pitch = MathHelper.clamp(this.head.pitch, -1.2f, 1.2f) - 1.4835298f;
				this.rightArm.yaw = this.head.yaw - 0.5235988f;
			}
		}
	}

	private void positionLeftArm(LittleMaidEntity entity) {
		switch (this.leftArmPose) {
			case EMPTY -> this.leftArm.yaw = 0.0f;
			case BLOCK -> {
				this.leftArm.pitch = this.leftArm.pitch * 0.5f - 0.9424779f;
				this.leftArm.yaw = 0.5235988f;
			}
			case ITEM -> {
				this.leftArm.pitch = this.leftArm.pitch * 0.5f - 0.31415927f;
				this.leftArm.yaw = 0.0f;
			}
			case THROW_SPEAR -> {
				this.leftArm.pitch = this.leftArm.pitch * 0.5f - (float) Math.PI;
				this.leftArm.yaw = 0.0f;
			}
			case BOW_AND_ARROW -> {
				this.rightArm.yaw = -0.1f + this.head.yaw - 0.4f;
				this.leftArm.yaw = 0.1f + this.head.yaw;
				this.rightArm.pitch = -1.5707964f + this.head.pitch;
				this.leftArm.pitch = -1.5707964f + this.head.pitch;
			}
			case CROSSBOW_CHARGE -> CrossbowPosing.charge(this.rightArm, this.leftArm, entity, false);
			case CROSSBOW_HOLD -> CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, false);
			case SPYGLASS -> {
				this.leftArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622f - (entity.isInSneakingPose() ? 0.2617994f : 0.0f), -2.4f, 3.3f);
				this.leftArm.yaw = this.head.yaw + 0.2617994f;
			}
			case TOOT_HORN -> {
				this.leftArm.pitch = MathHelper.clamp(this.head.pitch, -1.2f, 1.2f) - 1.4835298f;
				this.leftArm.yaw = this.head.yaw + 0.5235988f;
			}
		}
	}

	@Override
	public ModelPart getPart() {
		return this.base;
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrixStack) {
		this.getArm(arm).rotate(matrixStack);

		matrixStack.translate(arm == Arm.RIGHT ? 0.025F : -0.025F, 0.0F, 0.0F);
		matrixStack.scale(0.68F, 0.68F, 0.68F);
	}

	private ModelPart getArm(Arm arm) {
		if (arm == Arm.LEFT) {
			return this.leftArm;
		}

		return this.rightArm;
	}

	private ModelPart getArm(LittleMaidEntity maid) {
		Arm arm = maid.getMainArm();
		Hand hand = maid.preferredHand;
		if (hand != Hand.MAIN_HAND) arm = arm.getOpposite();
		return this.getArm(arm);
	}
}
