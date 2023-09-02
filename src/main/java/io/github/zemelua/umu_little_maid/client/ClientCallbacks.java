package io.github.zemelua.umu_little_maid.client;

import io.github.zemelua.umu_little_maid.c_component.headpatting.IHeadpattingComponent;
import io.github.zemelua.umu_little_maid.c_component.instruction.IInstructionComponent;
import io.github.zemelua.umu_little_maid.client.renderer.InstructionRenderer;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.HeadpatManager;
import io.github.zemelua.umu_little_maid.util.InstructionUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public final class ClientCallbacks {
	static void onStartTick(MinecraftClient client) {
		Optional<IHeadpattingComponent> component = HeadpatManager.getHeadpattingComponent(client);
		component.ifPresent(c -> HeadpatManager.clientTick(client, c));

		if (InstructionRenderer.drewSiteTooltipLast) {
			InstructionRenderer.renderSiteTooltipTicks++;
		} else {
			InstructionRenderer.renderSiteTooltipTicks = 0;
		}
	}

	static boolean onRenderBlockOutline(WorldRenderContext worldRenderContext, WorldRenderContext.BlockOutlineContext blockOutlineContext) {
		MinecraftClient client = MinecraftClient.getInstance();
		Optional<LittleMaidEntity> maid = InstructionUtils.getMaid(client);

		if (maid.isPresent()) {
			VertexConsumerProvider verticesProvider = Objects.requireNonNull(worldRenderContext.consumers());
			MatrixStack matrices = worldRenderContext.matrixStack();
			Camera camera = worldRenderContext.camera();
			World world = worldRenderContext.world();
			BlockPos pos = blockOutlineContext.blockPos();
			BlockState state = blockOutlineContext.blockState();

			InstructionRenderer.renderTargetOverlay(verticesProvider, matrices, camera, world, pos, state, maid.get());

			return false;
		}

		return true;
	}

	static void beforeRenderDebug(WorldRenderContext context) {
		MinecraftClient client = MinecraftClient.getInstance();

		InstructionRenderer.renderSitesOverlay(client, context);
	}

	static void onRenderHUD(DrawContext context, float tickDelta) {
		MinecraftClient client = MinecraftClient.getInstance();
		TextRenderer textRenderer = client.textRenderer;
		Window window = client.getWindow();
		int screenW = window.getScaledWidth();
		int screenH = window.getScaledHeight();
		World world = client.world;
		Camera camera = client.gameRenderer.getCamera();
		HitResult target = Objects.requireNonNull(client.crosshairTarget);
		Optional<IInstructionComponent> component = InstructionUtils.getComponent(client);

		if (component.isPresent() && component.get().isInstructing()) {
			//TODO レンダリング系の関数を全部context使うようにする
			InstructionRenderer.renderGuideMessage(context, textRenderer, screenW, screenH, world, target, component.get());
			InstructionRenderer.renderSiteTooltip(context, textRenderer, world, camera, screenW, screenH, tickDelta);
			InstructionRenderer.renderSitesIcon(client, context, world);
		}
	}
}
