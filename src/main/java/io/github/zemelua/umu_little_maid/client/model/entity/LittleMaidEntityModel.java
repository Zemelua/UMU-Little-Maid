package io.github.zemelua.umu_little_maid.client.model.entity;

import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.render.entity.model.CrossbowPosing;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

public class LittleMaidEntityModel extends SinglePartEntityModel<LittleMaidEntity> implements ModelWithArms {
	public static final String KEY_HEAD = EntityModelPartNames.HEAD;
	public static final String KEY_HAT = EntityModelPartNames.HAT;
	public static final String KEY_BODY = EntityModelPartNames.BODY;
	public static final String KEY_JACKET = EntityModelPartNames.JACKET;
	public static final String KEY_SKIRT = "skirt";
	public static final String KEY_APRON = "apron";
	public static final String KEY_LEFT_ARM = EntityModelPartNames.LEFT_ARM;
	public static final String KEY_LEFT_SLEEVE = "left_sleeve";
	public static final String KEY_RIGHT_ARM = EntityModelPartNames.RIGHT_ARM;
	public static final String KEY_RIGHT_SLEEVE = "right_sleeve";
	public static final String KEY_LEFT_LEG = EntityModelPartNames.LEFT_LEG;
	public static final String KEY_LEFT_PANTS = "left_pants";
	public static final String KEY_RIGHT_LEG = EntityModelPartNames.RIGHT_LEG;
	public static final String KEY_RIGHT_PANTS = "right_pants";
	public static final String KEY_BONE_USING_DRIPLEAF = "using_dripleaf_bone";
	public static final String KEY_BONE_CHANGING_COSTUME = "changing_costume_bone";

	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart body;
	private final ModelPart skirt;
	private final ModelPart leftArm;
	private final ModelPart rightArm;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;
	private final ModelPart boneUsingDripleaf;
	private final ModelPart boneChangingCostume;

	private ArmPose leftArmPose;
	private ArmPose rightArmPose;

	private boolean isUsingDripleaf;
	public float leaningPitch;

	public LittleMaidEntityModel(ModelPart root) {
		super(RenderLayer::getEntityTranslucent);

		this.root = root;
		this.head = this.root.getChild(KEY_HEAD);
		this.body = this.root.getChild(KEY_BODY);
		this.skirt = this.root.getChild(KEY_SKIRT);
		this.leftArm = this.root.getChild(KEY_LEFT_ARM);
		this.rightArm = this.root.getChild(KEY_RIGHT_ARM);
		this.leftLeg = this.root.getChild(KEY_LEFT_LEG);
		this.rightLeg = this.root.getChild(KEY_RIGHT_LEG);
		this.boneUsingDripleaf = this.root.getChild(KEY_BONE_USING_DRIPLEAF);
		this.boneChangingCostume = this.root.getChild(KEY_BONE_CHANGING_COSTUME);

		this.root.setDefaultTransform(ModelTransform.NONE);
		this.head.setDefaultTransform(ModelTransform.pivot(0.0F, 8.0F, 0.0F));
		this.body.setDefaultTransform(ModelTransform.pivot(0.0F, 8.0F, 0.0F));
		this.skirt.setDefaultTransform(ModelTransform.pivot(0.0F, 14.0F, 0.0F));
		this.leftArm.setDefaultTransform(ModelTransform.pivot(4.0F, 9.5F, 0.5F));
		this.rightArm.setDefaultTransform(ModelTransform.pivot(-4.0F, 9.5F, 0.5F));
		this.leftLeg.setDefaultTransform(ModelTransform.pivot(1.5F, 17.0F, 0.0F));
		this.rightLeg.setDefaultTransform(ModelTransform.pivot(-1.5F, 17.0F, 0.0F));
		this.boneUsingDripleaf.setDefaultTransform(ModelTransform.NONE);
		this.boneChangingCostume.setDefaultTransform(ModelTransform.NONE);

		this.leftArmPose = ArmPose.EMPTY;
		this.rightArmPose = ArmPose.EMPTY;

		this.isUsingDripleaf = false;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();

		root.addChild(KEY_HEAD, ModelPartBuilder.create()
						.uv(0, 0).cuboid(KEY_HEAD, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
						.uv(0, 32).cuboid(KEY_HAT, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.5F)),
				ModelTransform.pivot(0.0F, 8.0F, 0.0F));
		root.addChild(KEY_BODY, ModelPartBuilder.create()
						.uv(0, 16).cuboid(KEY_BODY, -3.0F, 0.0F, -2.0F, 6.0F, 9.0F, 4.0F, new Dilation(0.0F))
						.uv(0, 48).cuboid(KEY_JACKET, -3.0F, 0.0F, -2.0F, 6.0F, 9.0F, 4.0F, new Dilation(0.25F)),
				ModelTransform.pivot(0.0F, 8.0F, 0.0F));
		root.addChild(KEY_SKIRT, ModelPartBuilder.create()
						.uv(32, 0).cuboid(KEY_SKIRT, -4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
						.uv(32, 33).cuboid(KEY_APRON, -4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.5F)),
				ModelTransform.pivot(0.0F, 14.0F, 0.0F));
		root.addChild(KEY_LEFT_ARM, ModelPartBuilder.create()
						.uv(28, 16).cuboid(KEY_LEFT_ARM, -1.0F, -1.5F, -1.5F, 2.0F, 9.0F, 2.0F, new Dilation(0.0F))
						.uv(28, 50).cuboid(KEY_LEFT_SLEEVE, -1.0F, -1.5F, -1.5F, 2.0F, 9.0F, 2.0F, new Dilation(0.25F)),
				ModelTransform.pivot(4.0F, 9.5F, 0.5F));
		root.addChild(KEY_RIGHT_ARM, ModelPartBuilder.create()
						.uv(20, 16).cuboid(KEY_RIGHT_ARM, -1.0F, -1.5F, -1.5F, 2.0F, 9.0F, 2.0F, new Dilation(0.0F))
						.uv(20, 50).cuboid(KEY_RIGHT_SLEEVE, -1.0F, -1.5F, -1.5F, 2.0F, 9.0F, 2.0F, new Dilation(0.25F)),
				ModelTransform.pivot(-4.0F, 9.5F, 0.5F));
		root.addChild(KEY_LEFT_LEG, ModelPartBuilder.create()
						.uv(50, 16).cuboid(KEY_LEFT_LEG, -1.5F, 0.0F, -2.0F, 3.0F, 7.0F, 4.0F, new Dilation(0.0F))
						.uv(50, 50).cuboid(KEY_LEFT_PANTS, -1.5F, 0.0F, -2.0F, 3.0F, 7.0F, 4.0F, new Dilation(0.25F)),
				ModelTransform.pivot(1.5F, 17.0F, 0.0F));
		root.addChild(KEY_RIGHT_LEG, ModelPartBuilder.create()
						.uv(36, 16).cuboid(KEY_RIGHT_LEG, -1.5F, 0.0F, -2.0F, 3.0F, 7.0F, 4.0F, new Dilation(0.0F))
						.uv(36, 50).cuboid(KEY_RIGHT_PANTS, -1.5F, 0.0F, -2.0F, 3.0F, 7.0F, 4.0F, new Dilation(0.25F)),
				ModelTransform.pivot(-1.5F, 17.0F, 0.0F));

		root.addChild(KEY_BONE_USING_DRIPLEAF, ModelPartBuilder.create(), ModelTransform.NONE);
		root.addChild(KEY_BONE_CHANGING_COSTUME, ModelPartBuilder.create(), ModelTransform.NONE);

		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void animateModel(LittleMaidEntity maid, float limbAngle, float limbDistance, float tickDelta) {
		this.leaningPitch = maid.getLeaningPitch(tickDelta);

		this.initializeCubes();

		ArmPose mainPose = getArmPose(maid, Hand.MAIN_HAND);
		ArmPose offPose = mainPose.isTwoHanded()
				? maid.getOffHandStack().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM
				: getArmPose(maid, Hand.OFF_HAND);

		this.leftArmPose = maid.getMainArm().equals(Arm.LEFT) ? mainPose : offPose;
		this.rightArmPose = maid.getMainArm().equals(Arm.RIGHT) ? mainPose : offPose;
	}

	@Override
	public void setAngles(LittleMaidEntity maid, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
		this.setHeadAngle(maid, headPitch, headYaw);
		if (maid.isSitting()) {
			this.setSittingAngle();
		} else {
			this.setStandingAngle(limbAngle, limbDistance);
		}

		if (maid.getPose() != ModEntities.POSE_EATING) {
			this.setArmAngleByItem(maid);
		}
		if (this.rightArmPose != ArmPose.SPYGLASS) {
			CrossbowPosing.swingArm(this.rightArm, animationProgress, 1.0F);
		}
		if (this.leftArmPose != ArmPose.SPYGLASS) {
			CrossbowPosing.swingArm(this.leftArm, animationProgress, -1.0F);
		}

		Arm arm = maid.preferredHand == Hand.MAIN_HAND ? maid.getMainArm() : maid.getMainArm().getOpposite();
		this.adaptAttackingAngel(arm);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		matrices.multiply(new Quaternion(this.boneChangingCostume.pitch, this.boneChangingCostume.yaw, this.boneChangingCostume.roll, false));

		if (this.isUsingDripleaf) {
			matrices.push();
			matrices.translate(0.0F / 16.0F, 0.5F / 16.0F, 0.0F / 16.0F);

			this.head.setPivot(0.0F, 7.5F, 0.0F);
			this.body.setPivot(0.0F, 7.5F, 0.0F);
			this.skirt.setPivot(0.0F, 13.5F, 0.0F);
			this.leftArm.setPivot(4.0F, 9.0F, 0.5F);
			this.rightArm.setPivot(-4.0F, 9.0F, 0.5F);
			this.leftLeg.setPivot(1.5F, 16.5F, 0.0F);
			this.rightLeg.setPivot(-1.5F, 16.5F, 0.0F);

			matrices.multiply(new Quaternion(this.boneUsingDripleaf.pitch, this.boneUsingDripleaf.yaw, this.boneUsingDripleaf.roll, false));

			this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);

			matrices.pop();
		} else {
			super.render(matrices, vertices, light, overlay, red, green, blue, alpha);
		}
	}

	private void initializeCubes() {
		this.root.resetTransform();
		this.head.resetTransform();
		this.body.resetTransform();
		this.skirt.resetTransform();
		this.leftArm.resetTransform();
		this.rightArm.resetTransform();
		this.leftLeg.resetTransform();
		this.rightLeg.resetTransform();
		this.boneUsingDripleaf.resetTransform();
		this.boneChangingCostume.resetTransform();
	}

	private void setHeadAngle(LittleMaidEntity maid, float headPitch, float headYaw) {
		boolean isRolling = maid.getRoll() > 4;
		boolean isSwimming = maid.isInSwimmingPose();
		float lerped = isSwimming
				? ModUtils.lerpAngle(this.leaningPitch, this.head.pitch, -0.7853982F)
				: ModUtils.lerpAngle(this.leaningPitch, this.head.pitch, (float) Math.toRadians(headPitch));

		this.head.yaw = (float) Math.toRadians(headYaw);
	}

	private void setStandingAngle(float limbAngle, float limbDistance) {
		this.leftArm.pitch += MathHelper.cos(limbAngle * 0.6662F) * limbDistance;
		this.rightArm.pitch += MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * limbDistance;
		this.leftLeg.pitch = MathHelper.cos(limbAngle * 0.6662F + (float) Math.PI) * 1.4F * limbDistance;
		this.rightLeg.pitch = MathHelper.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
	}

	private void setSittingAngle() {
	}

	private void setArmAngleByItem(LittleMaidEntity maid) {
		boolean mainHandIsRight = maid.getMainArm() == Arm.RIGHT;
		if (maid.isUsingItem()) {
			boolean activeHandIsMain = maid.getActiveHand() == Hand.MAIN_HAND;
			if (mainHandIsRight == activeHandIsMain) {
				this.setRightArmAngleByItem(maid);
			} else {
				this.setLeftArmAngleByItem(maid);
			}
		} else {
			boolean isTwoHanded = mainHandIsRight ? this.leftArmPose.isTwoHanded() : this.rightArmPose.isTwoHanded();
			if (mainHandIsRight != isTwoHanded) {
				this.setLeftArmAngleByItem(maid);
				this.setRightArmAngleByItem(maid);
			} else {
				this.setRightArmAngleByItem(maid);
				this.setLeftArmAngleByItem(maid);
			}
		}
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

	private void preChangeCostumeAnimation(AnimationState animation, float limbAngle, float limbDistance) {
		this.setStandingAngle(limbAngle, limbDistance);
		this.boneChangingCostume.yaw = (float) Math.toRadians(animation.getTimeRunning() / 1000.0F * 360.0F / 0.5F);
	}

	private void setRightArmAngleByItem(LittleMaidEntity maid) {
		switch (this.rightArmPose) {
			case EMPTY -> this.rightArm.yaw = 0.0F;
			case BLOCK -> {
				this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.9424779F;
				this.rightArm.yaw = -0.5235988F;
			}
			case ITEM -> {
				this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.31415927F;
				this.rightArm.yaw = 0.0F;
			}
			case THROW_SPEAR -> {
				this.rightArm.pitch = this.rightArm.pitch * 0.5F - (float) Math.PI;
				if (maid.isSwimming()) {
					this.rightArm.pitch += Math.toRadians(-90.0D);
				}
				this.rightArm.yaw = 0.0F;
				this.rightArm.roll = (float) Math.toRadians(-20.0F);
			}
			case BOW_AND_ARROW -> {
				this.rightArm.yaw = -0.1F + this.head.yaw;
				this.leftArm.yaw = 0.1F + this.head.yaw + 0.4F;
				this.rightArm.pitch = -1.5707964F + this.head.pitch;
				this.leftArm.pitch = -1.5707964F + this.head.pitch;
			}
			case CROSSBOW_CHARGE -> CrossbowPosing.charge(this.rightArm, this.leftArm, maid, true);
			case CROSSBOW_HOLD -> CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, true);
			case SPYGLASS -> {
				this.rightArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622F - (maid.isInSneakingPose() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
				this.rightArm.yaw = this.head.yaw - 0.2617994F;
			}
			case TOOT_HORN -> {
				this.rightArm.pitch = MathHelper.clamp(this.head.pitch, -1.2F, 1.2F) - 1.4835298F;
				this.rightArm.yaw = this.head.yaw - 0.5235988F;
			}
		}
	}

	private void setLeftArmAngleByItem(LittleMaidEntity maid) {
		switch (this.leftArmPose) {
			case EMPTY -> this.leftArm.yaw = 0.0F;
			case BLOCK -> {
				this.leftArm.pitch = this.leftArm.pitch * 0.5F - 0.9424779F;
				this.leftArm.yaw = 0.5235988F;
			}
			case ITEM -> {
				this.leftArm.pitch = this.leftArm.pitch * 0.5F - 0.31415927F;
				this.leftArm.yaw = 0.0F;
			}
			case THROW_SPEAR -> {
				this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) Math.PI;
				if (maid.isSwimming()) {
					this.rightArm.pitch += Math.toRadians(-90.0D);
				}
				this.leftArm.yaw = 0.0F;
				this.leftArm.roll = (float) Math.toRadians(20.0F);
			}
			case BOW_AND_ARROW -> {
				this.rightArm.yaw = -0.1F + this.head.yaw - 0.4F;
				this.leftArm.yaw = 0.1F + this.head.yaw;
				this.rightArm.pitch = -1.5707964F + this.head.pitch;
				this.leftArm.pitch = -1.5707964F + this.head.pitch;
			}
			case CROSSBOW_CHARGE -> CrossbowPosing.charge(this.rightArm, this.leftArm, maid, false);
			case CROSSBOW_HOLD -> CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, false);
			case SPYGLASS -> {
				this.leftArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622F - (maid.isInSneakingPose() ? 0.2617994F : 0.0F), -2.4F, 3.3F);
				this.leftArm.yaw = this.head.yaw + 0.2617994F;
			}
			case TOOT_HORN -> {
				this.leftArm.pitch = MathHelper.clamp(this.head.pitch, -1.2F, 1.2F) - 1.4835298F;
				this.leftArm.yaw = this.head.yaw + 0.5235988F;
			}
		}
	}

	private static ArmPose getArmPose(LittleMaidEntity maid, Hand hand) {
		ItemStack mainStack = maid.getStackInHand(hand);
		if (mainStack.isEmpty()) return ArmPose.EMPTY;

		if (maid.getActiveHand() == hand && maid.getItemUseTimeLeft() > 0) {
			UseAction useAction = mainStack.getUseAction();
			switch (useAction) {
				case BLOCK -> {return ArmPose.BLOCK;}
				case BOW -> {return ArmPose.BOW_AND_ARROW;}
				case SPEAR -> {return ArmPose.THROW_SPEAR;}
				case SPYGLASS -> {return ArmPose.SPYGLASS;}
				case TOOT_HORN -> {return ArmPose.TOOT_HORN;}
				case CROSSBOW -> {return ArmPose.CROSSBOW_CHARGE;}
			}
		} else if (!maid.handSwinging && mainStack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(mainStack)) {
			return ArmPose.CROSSBOW_HOLD;
		}

		return ArmPose.ITEM;
	}

	@Override
	public ModelPart getPart() {
		return this.root;
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

	public void copyStateTo(LittleMaidEntityModel model) {
		super.copyStateTo(model);

		model.head.copyTransform(this.head);
		model.body.copyTransform(this.body);
		model.skirt.copyTransform(this.body);
		model.rightArm.copyTransform(this.rightArm);
		model.leftArm.copyTransform(this.leftArm);
		model.rightLeg.copyTransform(this.rightLeg);
		model.leftLeg.copyTransform(this.leftLeg);
		model.boneUsingDripleaf.copyTransform(this.boneUsingDripleaf);
		model.boneChangingCostume.copyTransform(this.boneChangingCostume);

		model.leftArmPose = this.leftArmPose;
		model.rightArmPose = this.rightArmPose;
	}

	public void setUsingDripleaf(boolean value) {
		this.isUsingDripleaf = value;
	}

	public static void setVisible(LittleMaidEntityModel model, EquipmentSlot slot) {
		model.head.visible = false;
		model.body.visible = false;
		model.rightArm.visible = false;
		model.leftArm.visible = false;
		model.rightLeg.visible = false;
		model.leftLeg.visible = false;

		switch (slot) {
			case HEAD -> model.head.visible = true;
			case CHEST -> {
				model.body.visible = true;
				model.rightArm.visible = true;
				model.leftArm.visible = true;
			}
			case LEGS -> {
				model.body.visible = true;
				model.rightLeg.visible = true;
				model.leftLeg.visible = true;
			}
			case FEET -> {
				model.rightLeg.visible = true;
				model.leftLeg.visible = true;
			}
		}
	}
}
