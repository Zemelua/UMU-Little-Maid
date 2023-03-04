package io.github.zemelua.umu_little_maid.client.renderer;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext.BlockOutlineContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class InstructionRenderer {
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

	private static final Overlay OVERLAY_AVAILABLE = new Overlay(
			ModRenderLayers.INSTRUCTION_TARGET,
			PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
			OVERLAY_AVAILABLE_TEXTURE,
			OVERLAY_AVAILABLE_TEXTURE_DOWN,
			OVERLAY_AVAILABLE_TEXTURE_UP,
			OVERLAY_AVAILABLE_TEXTURE_LEFT,
			OVERLAY_AVAILABLE_TEXTURE_RIGHT
	);
	private static final Overlay OVERLAY_UNAVAILABLE = new Overlay(
			ModRenderLayers.INSTRUCTION_TARGET,
			PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
			OVERLAY_UNAVAILABLE_TEXTURE,
			OVERLAY_UNAVAILABLE_TEXTURE_DOWN,
			OVERLAY_UNAVAILABLE_TEXTURE_UP,
			OVERLAY_UNAVAILABLE_TEXTURE_LEFT,
			OVERLAY_UNAVAILABLE_TEXTURE_RIGHT
	);

	public static void renderTargetOverlay(WorldRenderContext worldRenderContext, BlockOutlineContext blockOutlineContext) {
		VertexConsumerProvider verticesProvider = Objects.requireNonNull(worldRenderContext.consumers());
		MatrixStack matrices = worldRenderContext.matrixStack();
		Camera camera = worldRenderContext.camera();
		BlockPos pos = blockOutlineContext.blockPos();
		BlockState state = blockOutlineContext.blockState();

		if (state.isIn(BlockTags.BEDS)) {
			renderOverlay(verticesProvider, matrices, camera, OVERLAY_AVAILABLE, pos, state);
		} else {
			renderOverlay(verticesProvider, matrices, camera, OVERLAY_UNAVAILABLE, pos, state);
		}
	}

	public static void renderSitesOverlay(WorldRenderContext context) {

	}

	private static void renderOverlay(VertexConsumerProvider verticesProvider, MatrixStack matrices, Camera camera,
	                                  Overlay overlay, BlockPos pos, BlockState state) {
		if (state.contains(Properties.BED_PART)) {
			Direction connectTo = switch (state.get(Properties.BED_PART)) {
				case HEAD -> state.get(Properties.HORIZONTAL_FACING).getOpposite();
				case FOOT -> state.get(Properties.HORIZONTAL_FACING);
			};
			overlay.render(verticesProvider, matrices, camera, pos, connectTo);
			overlay.render(verticesProvider, matrices, camera, pos.offset(connectTo), connectTo.getOpposite());
		} else if (state.isIn(BlockTags.DOORS) && state.contains(Properties.DOUBLE_BLOCK_HALF)) {
			Direction connectTo = switch (state.get(Properties.DOUBLE_BLOCK_HALF)) {
				case UPPER -> Direction.DOWN;
				case LOWER -> Direction.UP;
			};
			overlay.render(verticesProvider, matrices, camera, pos, connectTo);
			overlay.render(verticesProvider, matrices, camera, pos.offset(connectTo), connectTo.getOpposite());
		} else {
			overlay.render(verticesProvider, matrices, camera, pos, null);
		}
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

			matrices.translate(0, 0, 0);
			matrices.pop();
		}

		private Sprite[] computeTextures(@Nullable Direction connectTo) {
			Sprite base = MinecraftClient.getInstance().getSpriteAtlas(this.atlas).apply(this.base);
			Sprite down = MinecraftClient.getInstance().getSpriteAtlas(this.atlas).apply(this.down);
			Sprite up = MinecraftClient.getInstance().getSpriteAtlas(this.atlas).apply(this.up);
			Sprite left = MinecraftClient.getInstance().getSpriteAtlas(this.atlas).apply(this.left);
			Sprite right = MinecraftClient.getInstance().getSpriteAtlas(this.atlas).apply(this.right);

			if (connectTo == null) return new Sprite[] {base, base, base, base, base, base};

			return switch (connectTo) {
				case DOWN -> new Sprite[] {base, base, up, up, up, up};
				case UP -> new Sprite[] {base, base, down, down, down, down};
				case NORTH -> new Sprite[] {up, down, base, base, right, left};
				case SOUTH -> new Sprite[] {down, up, base, base, left, right};
				case WEST -> new Sprite[] {left, right, left, right, base, base};
				case EAST -> new Sprite[] {right, left, right, left, base, base};
			};
		}
	}
}
