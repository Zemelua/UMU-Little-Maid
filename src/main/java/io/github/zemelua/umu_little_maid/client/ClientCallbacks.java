package io.github.zemelua.umu_little_maid.client;

import io.github.zemelua.umu_little_maid.c_component.IInstructionComponent;
import io.github.zemelua.umu_little_maid.client.renderer.InstructionRenderer;
import io.github.zemelua.umu_little_maid.util.InstructionUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public final class ClientCallbacks {
	static void beforeRenderDebug(WorldRenderContext context) {
		MinecraftClient client = MinecraftClient.getInstance();

		InstructionRenderer.renderSitesOverlay(client, context);
	}

	static void onRenderWorldLast(WorldRenderContext context) {

	}

	static void onRenderHUD(MatrixStack matrices, @SuppressWarnings("unused") float tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();
		TextRenderer textRenderer = client.textRenderer;
		Window window = client.getWindow();
		int screenW = window.getScaledWidth();
		int screenH = window.getScaledHeight();
		World world = client.world;
		HitResult target = Objects.requireNonNull(client.crosshairTarget);
		Optional<IInstructionComponent> component = InstructionUtils.getComponent(client);

		if (component.isPresent() && component.get().isInstructing()) {
			InstructionRenderer.renderGuideMessage(matrices, textRenderer, screenW, screenH, world, target, component.get());
			InstructionRenderer.renderSiteTooltip(matrices, window, world, client.gameRenderer.getCamera(), screenW, screenH);
		}
	}
}
