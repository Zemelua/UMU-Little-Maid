package io.github.zemelua.umu_little_maid.mixin;

import io.github.zemelua.umu_little_maid.c_component.headpatting.HeadpattingManager;
import io.github.zemelua.umu_little_maid.tinker.Tinkers;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class MixinPlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	@Inject(method = "getArmPose",
			at = @At("HEAD"),
			cancellable = true)
	private static void getHeadpattingArmPose(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> callback) {
		boolean isHeadpatting = HeadpattingManager.isHeadpatting(player);

		if (isHeadpatting) {
			callback.setReturnValue(Tinkers.ArmPose.HEADPATTING);
		}
	}

	private MixinPlayerEntityRenderer(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}
}
