package io.github.zemelua.umu_little_maid.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.c_component.Components;
import io.github.zemelua.umu_little_maid.c_component.IInstructionComponent;
import io.github.zemelua.umu_little_maid.client.renderer.gui.overlay.Overlay;
import io.github.zemelua.umu_little_maid.client.renderer.gui.overlay.OverlayRenderer;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.*;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

import static net.minecraft.world.RaycastContext.FluidHandling.*;
import static net.minecraft.world.RaycastContext.ShapeType.*;

public final class InstructionRenderer {
	public static final Identifier CROSSHAIR = UMULittleMaid.identifier("gui/hud/instruction_crosshair");
	public static final Identifier HEADDRESS = UMULittleMaid.identifier("instruction/site/headdress");

	public static void renderTargetOverlay(VertexConsumerProvider verticesProvider, MatrixStack matrices, Camera camera, World world, BlockPos pos, BlockState state, LittleMaidEntity maid) {
		if (maid.isAnySite(world, pos)) {
			renderOverlay(verticesProvider, matrices, camera, OverlayRenderer.OVERLAY_DELETABLE, pos, state);
		} else if (maid.isSettableAsAnySite(world, pos)) {
			renderOverlay(verticesProvider, matrices, camera, OverlayRenderer.AVAILABLE, pos, state);
		} else {
			renderOverlay(verticesProvider, matrices, camera, OverlayRenderer.OVERLAY_UNAVAILABLE, pos, state);
		}
	}

	public static void renderSitesOverlay(MinecraftClient client, WorldRenderContext context) {
		PlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
		IInstructionComponent instructionComponent = player.getComponent(Components.INSTRUCTION);
		Optional<LittleMaidEntity> maid = instructionComponent.getTarget();
		ClientWorld world = context.world();
		MatrixStack matrices = context.matrixStack();
		Camera camera = context.camera();
		VertexConsumerProvider verticesProvider = Objects.requireNonNull(context.consumers());

		maid.flatMap(LittleMaidEntity::getHome).filter(h -> shouldRenderSiteOverlay(client, world, player, h)).ifPresent(h -> {
			BlockPos homePos = h.getPos();

			renderOverlay(verticesProvider, matrices, camera, OverlayRenderer.OVERLAY_HOME, homePos, world.getBlockState(homePos));
		});

		maid.flatMap(LittleMaidEntity::getAnchor).filter(h -> shouldRenderSiteOverlay(client, world, player, h)).ifPresent(h -> {
			BlockPos anchorPos = h.getPos();

			OverlayRenderer.OVERLAY_ANCHOR.render(verticesProvider, matrices, camera, anchorPos, null);
		});

		maid.ifPresent(m -> m.getDeliveryBoxes().stream().filter(b -> shouldRenderSiteOverlay(client, world, player, b)).forEach(b -> {
			BlockPos boxPos = b.getPos();

			renderOverlay(verticesProvider, matrices, camera, OverlayRenderer.OVERLAY_DELIVERY_BOX, boxPos, world.getBlockState(boxPos));
		}));
	}

	private static boolean shouldRenderSiteOverlay(MinecraftClient client, World world, PlayerEntity player, GlobalPos pos) {
		Optional<HitResult> target = Optional.ofNullable(client.crosshairTarget);
		if (target.filter(h -> h.getType() == HitResult.Type.BLOCK)
				.filter(h -> ModUtils.isSameObject(world, ((BlockHitResult) h).getBlockPos(), pos))
				.isPresent()) {
			return false;
		}

		// TODO: 描画距離の設定に応じてレンダリングするかどうか決める
		return world.getRegistryKey().equals(pos.getDimension()) && pos.getPos().isWithinDistance(player.getPos(), 70);
	}

	private static void renderOverlay(VertexConsumerProvider verticesProvider, MatrixStack matrices, Camera camera,
	                                  Overlay overlay, BlockPos pos, BlockState state) {
		if (state.contains(Properties.BED_PART)) {
			Direction connectTo = switch (state.get(Properties.BED_PART)) {
				case HEAD -> state.get(Properties.HORIZONTAL_FACING).getOpposite();
				case FOOT -> state.get(Properties.HORIZONTAL_FACING);
			};
			renderDoubleOverlay(overlay, verticesProvider, matrices, camera, pos, connectTo);
		} else if (state.contains(Properties.DOUBLE_BLOCK_HALF)) {
			Direction connectTo = switch (state.get(Properties.DOUBLE_BLOCK_HALF)) {
				case UPPER -> Direction.DOWN;
				case LOWER -> Direction.UP;
			};
			renderDoubleOverlay(overlay, verticesProvider, matrices, camera, pos, connectTo);
		} else if (state.contains(Properties.HORIZONTAL_FACING) && state.contains(Properties.CHEST_TYPE)) {
			@Nullable Direction connectTo = switch (state.get(Properties.CHEST_TYPE)) {
				case LEFT -> state.get(Properties.HORIZONTAL_FACING).rotateYClockwise();
				case RIGHT -> state.get(Properties.HORIZONTAL_FACING).rotateYCounterclockwise();
				default -> null;
			};
			if (connectTo == null) {
				overlay.render(verticesProvider, matrices, camera, pos, null);
			} else {
				renderDoubleOverlay(overlay, verticesProvider, matrices, camera, pos, connectTo);
			}
		} else {
			overlay.render(verticesProvider, matrices, camera, pos, null);
		}
	}

	public static void renderSiteTooltip(MatrixStack matrices, Window window, World world, Camera camera, int screenW, int screenH) {
		PlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
		IInstructionComponent instructionComponent = player.getComponent(Components.INSTRUCTION);
		Optional<LittleMaidEntity> maid = instructionComponent.getTarget();

		double length = 100;
		Vec3d cameraPos = camera.getPos();
		Vec3d cameraRot = ModMathUtils.rotationToVector(camera.getPitch(), camera.getYaw());
		Vec3d endPos = cameraPos.add(cameraRot.multiply(length));
		Entity cameraEntity = camera.getFocusedEntity();
		RaycastContext raycast = new RaycastContext(cameraPos, endPos, OUTLINE, NONE, cameraEntity);
		BlockHitResult hitHome =  ModWorldUtils.raycast(world, raycast, b -> maid
				.flatMap(LittleMaidEntity::getHome)
				.map(h -> ModUtils.isSameObject(world, b, h))
				.orElse(false));

		if (hitHome.getType() != HitResult.Type.MISS) {
			// RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
			// Sprite texture = MinecraftClient.getInstance().getSpriteAtlas(OverlayRenderer.ATLAS).apply(SITE_ICON);
			// DrawableHelper.drawSprite(matrices, screenW / 2, screenH / 2, 0, texture.getWidth(), texture.getHeight(), texture);

			int x = Math.round(screenW / 2.0F) + 12;
			int y = Math.round(screenH / 2.0F) + 7;

			ModGUIUtils.drawULMTooltip(MinecraftClient.getInstance(), matrices, MinecraftClient.getInstance().textRenderer, x, y, null, Text.literal("メイドさんのおうち").styled(s -> s.withBold(true)), Text.of("srthbrsthy\naer"), 120);
		}
	}

	private static void renderDoubleOverlay(Overlay overlay, VertexConsumerProvider verticesProvider, MatrixStack matrices,
	                                        Camera camera, BlockPos pos, Direction connectTo) {
		overlay.render(verticesProvider, matrices, camera, pos, connectTo);
		overlay.render(verticesProvider, matrices, camera, pos.offset(connectTo), connectTo.getOpposite());
	}

	public static void renderCrossHair(MinecraftClient client, MatrixStack matrices, int screenW, int screenH, float z) {
		Sprite texture = client.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(CROSSHAIR);
		// RenderSystem.setShaderTexture(0, CROSSHAIR);
		BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
		Matrix4f matrix = matrices.peek().getPositionMatrix();
		int textureW = texture.getWidth();
		int textureH = texture.getHeight();
		float x0 = (screenW - textureW) / 2.0F;
		float y0 = (screenH - textureH) / 2.0F;
		float x1 = x0 + textureW;
		float y1 = y0 + textureH;
		float u0 = texture.getMinU();
		float v0 = texture.getMinV();
		float u1 = texture.getMaxU();
		float v1 = texture.getMaxV();

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
		RenderSystem.blendFuncSeparate(
				GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR,
				GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR,
				GlStateManager.SrcFactor.ONE,
				GlStateManager.DstFactor.ZERO
		);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
		bufferBuilder.vertex(matrix, x0, y1, z).texture(u0, v1).next();
		bufferBuilder.vertex(matrix, x1, y1, z).texture(u1, v1).next();
		bufferBuilder.vertex(matrix, x1, y0, z).texture(u1, v0).next();
		bufferBuilder.vertex(matrix, x0, y0, z).texture(u0, v0).next();
		BufferRenderer.drawWithShader(bufferBuilder.end());
	}

	public static void renderGuideMessage(MatrixStack matrices, TextRenderer textRenderer, int screenW, int screenH, World world, HitResult target, IInstructionComponent component) {
		int padding = 20;
		float centerX = screenW / 2.0F;
		float centerY = screenH / 2.0F;
		int y = Math.round(centerY - (textRenderer.fontHeight / 2.0F));

		Text onLeftClick = InstructionUtils.guideCancelMessage();
		int leftW = textRenderer.getWidth(onLeftClick);
		int leftX = Math.round(centerX - padding - leftW);
		ModUtils.GUIs.drawTextWithBackground(matrices, textRenderer, onLeftClick, leftX, y, 0xFFFFFF);

		int rightX = Math.round(centerX + padding);
		if (target.getType() == HitResult.Type.BLOCK) {
			BlockPos targetPos = ((BlockHitResult) target).getBlockPos();
			Optional<LittleMaidEntity> maid = component.getTarget();

			if (maid.isPresent() && world.getWorldBorder().contains(targetPos)) {
				if (maid.get().isAnySite(world, targetPos)) {
					ModUtils.GUIs.drawTextWithBackground(matrices, textRenderer, InstructionUtils.guideRemoveMessage(), rightX, y, 0xFFFFFF);
				} else if (maid.get().isSettableAsAnySite(world, targetPos)) {
					ModUtils.GUIs.drawTextWithBackground(matrices, textRenderer, InstructionUtils.guideDecideMessage(), rightX, y, 0xFFFFFF);
				}
			}
		}
	}
}
