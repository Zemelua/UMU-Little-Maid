package io.github.zemelua.umu_little_maid.client;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.util.HeadpatManager;
import io.github.zemelua.umu_little_maid.c_component.headpatting.IHeadpattingComponent;
import io.github.zemelua.umu_little_maid.c_component.instruction.IInstructionComponent;
import io.github.zemelua.umu_little_maid.client.renderer.InstructionRenderer;
import io.github.zemelua.umu_little_maid.client.renderer.gui.overlay.OverlayRenderer;
import io.github.zemelua.umu_little_maid.client.screen.LittleMaidScreen;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.InstructionUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Optional;

@Environment(EnvType.CLIENT)
public final class ClientCallbacks {
	static void onStartTick(MinecraftClient client) {
		Optional<IHeadpattingComponent> component = HeadpatManager.getHeadpattingComponent(client);
		component.ifPresent(c -> HeadpatManager.clientTick(client, c));
	}

	static void onRegisterSpritesWithBlockAtlas(@SuppressWarnings("unused") SpriteAtlasTexture atlas, ClientSpriteRegistryCallback.Registry registry) {
		registry.register(LittleMaidScreen.EMPTY_HELD_SLOT_TEXTURE);
		registry.register(LittleMaidScreen.EMPTY_HELMET_SLOT_TEXTURE);
		registry.register(LittleMaidScreen.EMPTY_BOOTS_SLOT_TEXTURE);
		registry.register(OverlayRenderer.TEXTURE_INSTRUCTION_AVAILABLE);
		registry.register(OverlayRenderer.TEXTURE_INSTRUCTION_AVAILABLE_DOWN);
		registry.register(OverlayRenderer.TEXTURE_INSTRUCTION_AVAILABLE_UP);
		registry.register(OverlayRenderer.TEXTURE_INSTRUCTION_AVAILABLE_LEFT);
		registry.register(OverlayRenderer.TEXTURE_INSTRUCTION_AVAILABLE_RIGHT);
		registry.register(OverlayRenderer.OVERLAY_DELETABLE_TEXTURE);
		registry.register(OverlayRenderer.OVERLAY_DELETABLE_TEXTURE_DOWN);
		registry.register(OverlayRenderer.OVERLAY_DELETABLE_TEXTURE_UP);
		registry.register(OverlayRenderer.OVERLAY_DELETABLE_TEXTURE_LEFT);
		registry.register(OverlayRenderer.OVERLAY_DELETABLE_TEXTURE_RIGHT);
		registry.register(OverlayRenderer.OVERLAY_UNAVAILABLE_TEXTURE);
		registry.register(OverlayRenderer.OVERLAY_UNAVAILABLE_TEXTURE_DOWN);
		registry.register(OverlayRenderer.OVERLAY_UNAVAILABLE_TEXTURE_UP);
		registry.register(OverlayRenderer.OVERLAY_UNAVAILABLE_TEXTURE_LEFT);
		registry.register(OverlayRenderer.OVERLAY_UNAVAILABLE_TEXTURE_RIGHT);
		registry.register(OverlayRenderer.OVERLAY_HOME_TEXTURE);
		registry.register(OverlayRenderer.OVERLAY_HOME_TEXTURE_DOWN);
		registry.register(OverlayRenderer.OVERLAY_HOME_TEXTURE_UP);
		registry.register(OverlayRenderer.OVERLAY_HOME_TEXTURE_LEFT);
		registry.register(OverlayRenderer.OVERLAY_HOME_TEXTURE_RIGHT);
		registry.register(OverlayRenderer.OVERLAY_ANCHOR_TEXTURE);
		registry.register(OverlayRenderer.OVERLAY_ANCHOR_TEXTURE_DOWN);
		registry.register(OverlayRenderer.OVERLAY_ANCHOR_TEXTURE_UP);
		registry.register(OverlayRenderer.OVERLAY_ANCHOR_TEXTURE_LEFT);
		registry.register(OverlayRenderer.OVERLAY_ANCHOR_TEXTURE_RIGHT);
		registry.register(OverlayRenderer.OVERLAY_DELIVERY_BOX_TEXTURE);
		registry.register(OverlayRenderer.OVERLAY_DELIVERY_BOX_TEXTURE_DOWN);
		registry.register(OverlayRenderer.OVERLAY_DELIVERY_BOX_TEXTURE_UP);
		registry.register(OverlayRenderer.OVERLAY_DELIVERY_BOX_TEXTURE_LEFT);
		registry.register(OverlayRenderer.OVERLAY_DELIVERY_BOX_TEXTURE_RIGHT);
		registry.register(InstructionRenderer.CROSSHAIR);
		registry.register(InstructionRenderer.HEADDRESS);
		registry.register(InstructionRenderer.ICON_HOME);
		registry.register(InstructionRenderer.ICON_ANCHOR);
		registry.register(InstructionRenderer.ICON_DELIVERY_BOX);
		registry.register(UMULittleMaid.identifier("particle/twinkle"));
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
		Vec3d cameraPos = context.camera().getPos();

		client.debugRenderer.pathfindingDebugRenderer.render(context.matrixStack(), context.consumers(), cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());

		InstructionRenderer.renderSitesOverlay(client, context);
	}

	static void onRenderHUD(MatrixStack matrices, @SuppressWarnings("unused") float tickDelta) {
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
			InstructionRenderer.renderGuideMessage(matrices, textRenderer, screenW, screenH, world, target, component.get());
			InstructionRenderer.renderSiteTooltip(client, matrices, textRenderer, world, camera, screenW, screenH);
		}
	}
}
