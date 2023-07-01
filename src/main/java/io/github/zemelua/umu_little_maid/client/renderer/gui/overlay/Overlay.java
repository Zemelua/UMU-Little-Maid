package io.github.zemelua.umu_little_maid.client.renderer.gui.overlay;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@SuppressWarnings("ClassCanBeRecord")
public class Overlay {
	private final RenderLayer layer;
	private final Identifier atlas;
	private final Identifier base;
	private final Identifier down;
	private final Identifier up;
	private final Identifier left;
	private final Identifier right;

	Overlay(RenderLayer layer, Identifier atlas, Identifier base, Identifier down, Identifier up, Identifier left, Identifier right) {
		this.layer = layer;
		this.atlas = atlas;
		this.base = base;
		this.down = down;
		this.up = up;
		this.left = left;
		this.right = right;
	}

	public void render(VertexConsumerProvider verticesProvider, MatrixStack matrices, Camera camera, BlockPos pos, @Nullable Direction connectTo) {
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
		MinecraftClient client = MinecraftClient.getInstance();

		Sprite base = client.getSpriteAtlas(this.atlas).apply(this.base);
		Sprite down = client.getSpriteAtlas(this.atlas).apply(this.down);
		Sprite up = client.getSpriteAtlas(this.atlas).apply(this.up);
		Sprite left = client.getSpriteAtlas(this.atlas).apply(this.left);
		Sprite right = client.getSpriteAtlas(this.atlas).apply(this.right);

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
