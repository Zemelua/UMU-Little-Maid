package io.github.zemelua.umu_little_maid.mixin;

import io.github.zemelua.umu_little_maid.c_component.headpatting.HeadpattingManager;
import io.github.zemelua.umu_little_maid.tinker.Tinkers;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
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

	@Inject(method = "animateArms",
			at = @At("HEAD"))
	private void animateHeadpattingArms(T entity, float animationProgress, CallbackInfo callback) {
		if (entity instanceof PlayerEntity player && this.rightArmPose == Tinkers.ArmPose.HEADPATTING && ModUtils.isThirdPersonView()) {
			float tickDelta = MinecraftClient.getInstance().getTickDelta();
			int ticks = HeadpattingManager.getComponent(player).getHeadpattingTicks();
			double sin = Math.sin(Math.toRadians((ticks + tickDelta) * 18.0D));

			this.rightArm.roll = (float) Math.toRadians(0.0D + sin * -20.0D);
			this.rightArm.yaw = (float) Math.toRadians(0.0D + sin * 30.0D);
		}
	}

	@Inject(method = "positionLeftArm",
			at = @At("HEAD"),
			cancellable = true)
	private void setHeadpattingLeftArmAngel(T entity, CallbackInfo callback) {
		if (this.leftArmPose == Tinkers.ArmPose.HEADPATTING) {
			this.leftArm.pitch += Math.toRadians(-100.0D);

			callback.cancel();
		}
	}

	@Inject(method = "positionRightArm",
			at = @At("HEAD"),
			cancellable = true)
	private void setHeadpattingRightArmAngel(T entity, CallbackInfo callback) {
		if (this.rightArmPose == Tinkers.ArmPose.HEADPATTING) {
			this.rightArm.pitch += Math.toRadians(-100.0D);

			callback.cancel();
		}
	}
}
