package io.github.zemelua.umu_little_maid.mixin;

import io.github.zemelua.umu_little_maid.tinker.Tinkers;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class MixinBipedEntityModel<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {
	@Shadow public BipedEntityModel.ArmPose leftArmPose;
	@Shadow public BipedEntityModel.ArmPose rightArmPose;
	@Shadow @Final public ModelPart leftArm;
	@Shadow @Final public ModelPart rightArm;

	@Inject(method = "positionLeftArm",
			at = @At("HEAD"),
			cancellable = true)
	private void setHeadpattingLeftArmAngel(T entity, CallbackInfo callback) {
		if (this.leftArmPose == Tinkers.ArmPose.HEADPATTING) {
			this.leftArm.yaw += Math.toRadians(90.0D);

			callback.cancel();
		}
	}

	@Inject(method = "positionRightArm",
			at = @At("HEAD"),
			cancellable = true)
	private void setHeadpattingRightArmAngel(T entity, CallbackInfo callback) {
		if (this.rightArmPose == Tinkers.ArmPose.HEADPATTING) {
			this.rightArm.yaw += Math.toRadians(60.0D);

			callback.cancel();
		}
	}
}
