package io.github.zemelua.umu_little_maid.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.c_component.Components;
import io.github.zemelua.umu_little_maid.c_component.IInstructionComponent;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.*;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
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
	public static final Identifier ATLAS_ID = UMULittleMaid.identifier("textures/atlas/instruction.png");
	public static final SpriteAtlasTexture ATLAS = new SpriteAtlasTexture(ATLAS_ID);
	public static final Identifier OVERLAY_AVAILABLE_TEXTURE = UMULittleMaid.identifier("gui/instruction_overlay_available");
	public static final Identifier OVERLAY_AVAILABLE_TEXTURE_DOWN = UMULittleMaid.identifier("gui/instruction_overlay_available_down");
	public static final Identifier OVERLAY_AVAILABLE_TEXTURE_UP = UMULittleMaid.identifier("gui/instruction_overlay_available_up");
	public static final Identifier OVERLAY_AVAILABLE_TEXTURE_LEFT = UMULittleMaid.identifier("gui/instruction_overlay_available_left");
	public static final Identifier OVERLAY_AVAILABLE_TEXTURE_RIGHT = UMULittleMaid.identifier("gui/instruction_overlay_available_right");
	public static final Identifier OVERLAY_UNAVAILABLE_TEXTURE = UMULittleMaid.identifier("gui/instruction_overlay_unavailable");
	public static final Identifier OVERLAY_UNAVAILABLE_TEXTURE_DOWN = UMULittleMaid.identifier("gui/instruction_overlay_unavailable_down");
	public static final Identifier OVERLAY_UNAVAILABLE_TEXTURE_UP = UMULittleMaid.identifier("gui/instruction_overlay_unavailable_up");
	public static final Identifier OVERLAY_UNAVAILABLE_TEXTURE_LEFT = UMULittleMaid.identifier("gui/instruction_overlay_unavailable_left");
	public static final Identifier OVERLAY_UNAVAILABLE_TEXTURE_RIGHT = UMULittleMaid.identifier("gui/instruction_overlay_unavailable_right");
	public static final Identifier OVERLAY_HOME_TEXTURE = UMULittleMaid.identifier("gui/instruction_overlay_home");
	public static final Identifier OVERLAY_HOME_TEXTURE_DOWN = UMULittleMaid.identifier("gui/instruction_overlay_home_down");
	public static final Identifier OVERLAY_HOME_TEXTURE_UP = UMULittleMaid.identifier("gui/instruction_overlay_home_up");
	public static final Identifier OVERLAY_HOME_TEXTURE_LEFT = UMULittleMaid.identifier("gui/instruction_overlay_home_left");
	public static final Identifier OVERLAY_HOME_TEXTURE_RIGHT = UMULittleMaid.identifier("gui/instruction_overlay_home_right");
	public static final Identifier OVERLAY_DELIVERY_BOX_TEXTURE = UMULittleMaid.identifier("gui/instruction_overlay_delivery_box");
	public static final Identifier OVERLAY_DELIVERY_BOX_TEXTURE_DOWN = UMULittleMaid.identifier("gui/instruction_overlay_delivery_box_down");
	public static final Identifier OVERLAY_DELIVERY_BOX_TEXTURE_UP = UMULittleMaid.identifier("gui/instruction_overlay_delivery_box_up");
	public static final Identifier OVERLAY_DELIVERY_BOX_TEXTURE_LEFT = UMULittleMaid.identifier("gui/instruction_overlay_delivery_box_left");
	public static final Identifier OVERLAY_DELIVERY_BOX_TEXTURE_RIGHT = UMULittleMaid.identifier("gui/instruction_overlay_delivery_box_right");
	public static final Identifier CROSSHAIR = UMULittleMaid.identifier("gui/hud/instruction_crosshair");
	public static final Identifier SITE_ICON = UMULittleMaid.identifier("instruction/site/icon");
	public static final Identifier HEADDRESS = UMULittleMaid.identifier("instruction/site/headdress");
	private static final Overlay OVERLAY_AVAILABLE = new Overlay(
			ModRenderLayers.INSTRUCTION_OVERLAY_TARGET,
			PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
			OVERLAY_AVAILABLE_TEXTURE,
			OVERLAY_AVAILABLE_TEXTURE_DOWN,
			OVERLAY_AVAILABLE_TEXTURE_UP,
			OVERLAY_AVAILABLE_TEXTURE_LEFT,
			OVERLAY_AVAILABLE_TEXTURE_RIGHT
	);
	private static final Overlay OVERLAY_UNAVAILABLE = new Overlay(
			ModRenderLayers.INSTRUCTION_OVERLAY_TARGET,
			PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
			OVERLAY_UNAVAILABLE_TEXTURE,
			OVERLAY_UNAVAILABLE_TEXTURE_DOWN,
			OVERLAY_UNAVAILABLE_TEXTURE_UP,
			OVERLAY_UNAVAILABLE_TEXTURE_LEFT,
			OVERLAY_UNAVAILABLE_TEXTURE_RIGHT
	);
	private static final Overlay OVERLAY_HOME = new Overlay(
			ModRenderLayers.INSTRUCTION_OVERLAY_SITE,
			PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
			OVERLAY_HOME_TEXTURE,
			OVERLAY_HOME_TEXTURE_DOWN,
			OVERLAY_HOME_TEXTURE_UP,
			OVERLAY_HOME_TEXTURE_LEFT,
			OVERLAY_HOME_TEXTURE_RIGHT
	);
	private static final Overlay OVERLAY_DELIVERY_BOX = new Overlay(
			ModRenderLayers.INSTRUCTION_OVERLAY_SITE,
			PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
			OVERLAY_DELIVERY_BOX_TEXTURE,
			OVERLAY_DELIVERY_BOX_TEXTURE_DOWN,
			OVERLAY_DELIVERY_BOX_TEXTURE_UP,
			OVERLAY_DELIVERY_BOX_TEXTURE_LEFT,
			OVERLAY_DELIVERY_BOX_TEXTURE_RIGHT
	);

	// public static final

	public static void renderTargetOverlay(WorldRenderContext worldRenderContext, BlockOutlineContext blockOutlineContext) {
		VertexConsumerProvider verticesProvider = Objects.requireNonNull(worldRenderContext.consumers());
		MatrixStack matrices = worldRenderContext.matrixStack();
		Camera camera = worldRenderContext.camera();
		BlockPos pos = blockOutlineContext.blockPos();
		BlockState state = blockOutlineContext.blockState();

		if (state.isIn(BlockTags.BEDS) || state.isOf(Blocks.CHEST)) {
			renderOverlay(verticesProvider, matrices, camera, OVERLAY_AVAILABLE, pos, state, false);
		} else {
			renderOverlay(verticesProvider, matrices, camera, OVERLAY_UNAVAILABLE, pos, state, false);
		}
	}

	public static void renderSitesOverlay(WorldRenderContext context) {
		PlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);
		IInstructionComponent instructionComponent = player.getComponent(Components.INSTRUCTION);
		Optional<LittleMaidEntity> maid = instructionComponent.getTarget();
		ClientWorld world = context.world();
		MatrixStack matrices = context.matrixStack();
		Camera camera = context.camera();
		VertexConsumerProvider verticesProvider = Objects.requireNonNull(context.consumers());

		maid.flatMap(LittleMaidEntity::getHome).filter(h -> shouldRender(world, player, h)).ifPresent(h -> {
			BlockPos homePos = h.getPos();

			renderOverlay(verticesProvider, matrices, camera, OVERLAY_HOME, homePos, world.getBlockState(homePos), true);
		});

		maid.ifPresent(m -> m.getDeliveryBoxes().stream().filter(b -> shouldRender(world, player, b)).forEach(b -> {
			BlockPos boxPos = b.getPos();

			renderOverlay(verticesProvider, matrices, camera, OVERLAY_DELIVERY_BOX, boxPos, world.getBlockState(boxPos), true);
		}));
	}

	private static boolean shouldRender(World world, PlayerEntity player, GlobalPos pos) {
		// TODO: 描画距離の設定に応じてレンダリングするかどうか決める
		return world.getRegistryKey().equals(pos.getDimension()) && pos.getPos().isWithinDistance(player.getPos(), 70);
	}

	private static void renderOverlay(VertexConsumerProvider verticesProvider, MatrixStack matrices, Camera camera,
	                                  Overlay overlay, BlockPos pos, BlockState state, boolean drawIcon) {
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
			Sprite texture = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(SITE_ICON);
			DrawableHelper.drawSprite(matrices, screenW / 2, screenH / 2, 0, texture.getWidth(), texture.getHeight(), texture);

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

	private static class Overlay {
		private final RenderLayer layer;
		private final Identifier atlas;
		private final Identifier base;
		private final Identifier down;
		private final Identifier up;
		private final Identifier left;
		private final Identifier right;

		private Overlay(RenderLayer layer, Identifier atlas, Identifier base, Identifier down, Identifier up, Identifier left, Identifier right) {
			this.layer = layer;
			this.atlas = atlas;
			this.base = base;
			this.down = down;
			this.up = up;
			this.left = left;
			this.right = right;
		}

		private void render(VertexConsumerProvider verticesProvider, MatrixStack matrices, Camera camera, BlockPos pos, @Nullable Direction connectTo) {
			Vec3d cameraPos = camera.getPos();
			VertexConsumer vertices = verticesProvider.getBuffer(this.layer);
			Sprite[] textures = this.computeTextures(connectTo);

			matrices.push();
			matrices.translate(pos.getX() - cameraPos.getX(), pos.getY() - cameraPos.getY(), pos.getZ() - cameraPos.getZ());
			MatrixStack.Entry entry = matrices.peek();
			Matrix4f matrix = entry.getPositionMatrix();

			if (connectTo != Direction.DOWN) {
				// DOWN
				vertices.vertex(matrix, 0.0F, 0.0F, 0.0F)
						.texture(textures[0].getMinU(), textures[0].getMaxV())
						.next();
				vertices.vertex(matrix, 1.0F, 0.0F, 0.0F)
						.texture(textures[0].getMaxU(), textures[0].getMaxV())
						.next();
				vertices.vertex(matrix, 1.0F, 0.0F, 1.0F)
						.texture(textures[0].getMaxU(), textures[0].getMinV())
						.next();
				vertices.vertex(matrix, 0.0F, 0.0F, 1.0F)
						.texture(textures[0].getMinU(), textures[0].getMinV())
						.next();
			}

			if (connectTo != Direction.UP) {
				// UP
				vertices.vertex(matrix, 0.0F, 1.0F, 0.0F)
						.texture(textures[1].getMinU(), textures[1].getMinV())
						.next();
				vertices.vertex(matrix, 0.0F, 1.0F, 1.0F)
						.texture(textures[1].getMinU(), textures[1].getMaxV())
						.next();
				vertices.vertex(matrix, 1.0F, 1.0F, 1.0F)
						.texture(textures[1].getMaxU(), textures[1].getMaxV())
						.next();
				vertices.vertex(matrix, 1.0F, 1.0F, 0.0F)
						.texture(textures[1].getMaxU(), textures[1].getMinV())
						.next();
			}

			if (connectTo != Direction.NORTH) {
				// NORTH
				vertices.vertex(matrix, 1.0F, 1.0F, 0.0F)
						.texture(textures[2].getMinU(), textures[2].getMinV())
						.next();
				vertices.vertex(matrix, 1.0F, 0.0F, 0.0F)
						.texture(textures[2].getMinU(), textures[2].getMaxV())
						.next();
				vertices.vertex(matrix, 0.0F, 0.0F, 0.0F)
						.texture(textures[2].getMaxU(), textures[2].getMaxV())
						.next();
				vertices.vertex(matrix, 0.0F, 1.0F, 0.0F)
						.texture(textures[2].getMaxU(), textures[2].getMinV())
						.next();
			}

			if (connectTo != Direction.SOUTH) {
				// SOUTH
				vertices.vertex(matrix, 0.0F, 1.0F, 1.0F)
						.texture(textures[3].getMinU(), textures[3].getMinV())
						.next();
				vertices.vertex(matrix, 0.0F, 0.0F, 1.0F)
						.texture(textures[3].getMinU(), textures[3].getMaxV())
						.next();
				vertices.vertex(matrix, 1.0F, 0.0F, 1.0F)
						.texture(textures[3].getMaxU(), textures[3].getMaxV())
						.next();
				vertices.vertex(matrix, 1.0F, 1.0F, 1.0F)
						.texture(textures[3].getMaxU(), textures[3].getMinV())
						.next();
			}

			if (connectTo != Direction.WEST) {
				// WEST
				vertices.vertex(matrix, 0.0F, 1.0F, 0.0F)
						.texture(textures[4].getMinU(), textures[4].getMinV())
						.next();
				vertices.vertex(matrix, 0.0F, 0.0F, 0.0F)
						.texture(textures[4].getMinU(), textures[4].getMaxV())
						.next();
				vertices.vertex(matrix, 0.0F, 0.0F, 1.0F)
						.texture(textures[4].getMaxU(), textures[4].getMaxV())
						.next();
				vertices.vertex(matrix, 0.0F, 1.0F, 1.0F)
						.texture(textures[4].getMaxU(), textures[4].getMinV())
						.next();
			}

			if (connectTo != Direction.EAST) {
				// EAST
				vertices.vertex(matrix, 1.0F, 1.0F, 1.0F)
						.texture(textures[5].getMinU(), textures[5].getMinV())
						.next();
				vertices.vertex(matrix, 1.0F, 0.0F, 1.0F)
						.texture(textures[5].getMinU(), textures[5].getMaxV())
						.next();
				vertices.vertex(matrix, 1.0F, 0.0F, 0.0F)
						.texture(textures[5].getMaxU(), textures[5].getMaxV())
						.next();
				vertices.vertex(matrix, 1.0F, 1.0F, 0.0F)
						.texture(textures[5].getMaxU(), textures[5].getMinV())
						.next();
			}

			matrices.pop();
		}

		private Sprite[] computeTextures(@Nullable Direction connectTo) {
			Sprite base = MinecraftClient.getInstance().getSpriteAtlas(this.atlas).apply(this.base);
			Sprite down = MinecraftClient.getInstance().getSpriteAtlas(this.atlas).apply(this.down);
			Sprite up = MinecraftClient.getInstance().getSpriteAtlas(this.atlas).apply(this.up);
			Sprite left = MinecraftClient.getInstance().getSpriteAtlas(this.atlas).apply(this.left);
			Sprite right = MinecraftClient.getInstance().getSpriteAtlas(this.atlas).apply(this.right);

			if (connectTo == null) return new Sprite[]{base, base, base, base, base, base};

			return switch (connectTo) {
				case DOWN -> new Sprite[]{base, base, up, up, up, up};
				case UP -> new Sprite[]{base, base, down, down, down, down};
				case NORTH -> new Sprite[]{up, down, base, base, right, left};
				case SOUTH -> new Sprite[]{down, up, base, base, left, right};
				case WEST -> new Sprite[]{left, right, left, right, base, base};
				case EAST -> new Sprite[]{right, left, right, left, base, base};
			};
		}
	}

	public static void renderCrossHair(MinecraftClient client, MatrixStack matrices, int screenW, int screenH, float z) {
		Sprite texture = client.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(CROSSHAIR);
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
		float padding = 20;
		float centerX = screenW / 2.0F;
		float centerY = screenH / 2.0F;
		float y = centerY - (textRenderer.fontHeight / 2.0F);

		Text onLeftClick = InstructionUtils.guideCancelMessage();
		float leftX = centerX - padding - textRenderer.getWidth(onLeftClick);
		DrawableHelper.drawTextWithShadow(matrices, textRenderer, onLeftClick, Math.round(leftX), Math.round(y), 0xFFFFFF);

		float rightX = centerX + padding;
		if (target.getType() == HitResult.Type.BLOCK) {
			BlockPos targetPos = ((BlockHitResult) target).getBlockPos();
			BlockState targetState = world.getBlockState(targetPos);
			Optional<LittleMaidEntity> maid = component.getTarget();

			if (world.getWorldBorder().contains(targetPos)) {
				Text onRightClick;

				if (maid.filter(m -> m.isHome(world, targetPos) || m.isDeliveryBox(world, targetPos)).isPresent()) {
					onRightClick = InstructionUtils.guideRemoveMessage();
				} else if (targetState.isIn(BlockTags.BEDS) || targetState.isOf(Blocks.CHEST)) {
					onRightClick = InstructionUtils.guideDecideMessage();
				} else {
					onRightClick = Text.empty();
				}

				DrawableHelper.drawTextWithShadow(matrices, textRenderer, onRightClick, Math.round(rightX), Math.round(y), 0xFFFFFF);
			}
		}
	}
}
