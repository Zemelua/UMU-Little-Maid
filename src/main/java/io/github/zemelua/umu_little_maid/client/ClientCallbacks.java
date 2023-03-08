package io.github.zemelua.umu_little_maid.client;

import io.github.zemelua.umu_little_maid.client.renderer.InstructionRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

@Environment(EnvType.CLIENT)
public final class ClientCallbacks {
	static void onRenderWorldLast(WorldRenderContext context) {
		InstructionRenderer.renderSitesOverlay(context);
	}
}
