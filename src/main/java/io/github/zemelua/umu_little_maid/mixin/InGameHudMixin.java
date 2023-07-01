package io.github.zemelua.umu_little_maid.mixin;

import io.github.zemelua.umu_little_maid.client.renderer.InstructionRenderer;
import io.github.zemelua.umu_little_maid.util.InstructionUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
	@Shadow @Final private MinecraftClient client;
	@Shadow private int scaledWidth;
	@Shadow private int scaledHeight;

	/**
	 * クライアントプレイヤーがインストラクションを実行中のとき、デフォルトの十字クロスヘアとアタックインジケータの描画
	 * をキャンセルし、インストラクションクロスヘアを描画します。それ以外のときはなにもしません。
	 */
	@SuppressWarnings("SpellCheckingInspection")
	@Inject(method = "renderCrosshair",
			at = @At(value = "INVOKE",
					target = "Lcom/mojang/blaze3d/systems/RenderSystem;blendFuncSeparate(Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;Lcom/mojang/blaze3d/platform/GlStateManager$SrcFactor;Lcom/mojang/blaze3d/platform/GlStateManager$DstFactor;)V"),
			cancellable = true)
	private void replaceCrosshairWhileInstructing(DrawContext context, CallbackInfo callback) {
		if (InstructionUtils.getComponent(Objects.requireNonNull(this.client.player)).isInstructing()) {
			InstructionRenderer.renderCrossHair(context.getMatrices(), this.scaledWidth, this.scaledHeight);

			callback.cancel();
		}
	}
}
