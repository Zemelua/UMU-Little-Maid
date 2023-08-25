package io.github.zemelua.umu_little_maid.client.renderer;

import io.github.zemelua.umu_little_maid.entity.ModFishingBobberEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class ModFishingBobberRenderer extends EntityRenderer<ModFishingBobberEntity> {
	public ModFishingBobberRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	@Override
	public void render(ModFishingBobberEntity fishHook, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		double s;
		float r;
		double q;
		double p;
		double o;
		@Nullable Entity owner = fishHook.getOwner();
		if (!(owner instanceof LivingEntity ownerLiving)) return;

		matrixStack.push();
		matrixStack.push();
		matrixStack.scale(0.5F, 0.5F, 0.5F);
		matrixStack.multiply(this.dispatcher.getRotation());
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f positionMatrix = entry.getPositionMatrix();
		Matrix3f normalMatrix = entry.getNormalMatrix();
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(this.getTexture(fishHook)));
		vertex(vertexConsumer, positionMatrix, normalMatrix, i, 0.0f, 0, 0, 1);
		vertex(vertexConsumer, positionMatrix, normalMatrix, i, 1.0f, 0, 1, 1);
		vertex(vertexConsumer, positionMatrix, normalMatrix, i, 1.0f, 1, 1, 0);
		vertex(vertexConsumer, positionMatrix, normalMatrix, i, 0.0f, 1, 0, 0);
		matrixStack.pop();
		int j = ownerLiving.getMainArm() == Arm.RIGHT ? 1 : -1;
		ItemStack itemStack = ownerLiving.getMainHandStack();
		if (!itemStack.isOf(Items.FISHING_ROD)) {
			j = -j;
		}
		float l = MathHelper.lerp(g, ownerLiving.prevBodyYaw, ownerLiving.bodyYaw) * ((float)Math.PI / 180);
		double d = MathHelper.sin(l);
		double e = MathHelper.cos(l);
		double m = (double)j * 0.35;
		double n = 0.8;
		if (this.dispatcher.gameOptions != null && !this.dispatcher.gameOptions.getPerspective().isFirstPerson() || owner != MinecraftClient.getInstance().player) {
			o = MathHelper.lerp((double)g, owner.prevX, owner.getX()) - e * m - d * 0.8;
			p = owner.prevY + (double)owner.getStandingEyeHeight() + (owner.getY() - owner.prevY) * (double)g - 0.45;
			q = MathHelper.lerp((double)g, owner.prevZ, owner.getZ()) - d * m + e * 0.8;
			r = owner.isInSneakingPose() ? -0.1875f : 0.0f;
		} else {
			s = 960.0 / (double)this.dispatcher.gameOptions.getFov().getValue().intValue();
			Vec3d vec3d = this.dispatcher.camera.getProjection().getPosition((float)j * 0.525f, -0.1f);
			vec3d = vec3d.multiply(s);
			o = MathHelper.lerp((double)g, owner.prevX, owner.getX()) + vec3d.x;
			p = MathHelper.lerp((double)g, owner.prevY, owner.getY()) + vec3d.y;
			q = MathHelper.lerp((double)g, owner.prevZ, owner.getZ()) + vec3d.z;
			r = owner.getStandingEyeHeight();
		}
		s = MathHelper.lerp((double)g, fishHook.prevX, fishHook.getX());
		double t = MathHelper.lerp((double)g, fishHook.prevY, fishHook.getY()) + 0.25;
		double u = MathHelper.lerp((double)g, fishHook.prevZ, fishHook.getZ());
		float v = (float)(o - s);
		float w = (float)(p - t) + r;
		float x = (float)(q - u);
		VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLineStrip());
		MatrixStack.Entry entry2 = matrixStack.peek();
		int y = 16;
		for (int z = 0; z <= 16; ++z) {
			renderFishingLine(v, w, x, vertexConsumer2, entry2, percentage(z, 16), percentage(z + 1, 16));
		}
		matrixStack.pop();
		super.render(fishHook, f, g, matrixStack, vertexConsumerProvider, i);
	}

	private static float percentage(int value, int max) {
		return (float)value / (float)max;
	}

	private static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, float x, int y, int u, int v) {
		buffer.vertex(matrix, x - 0.5f, (float)y - 0.5f, 0.0f).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
	}

	private static void renderFishingLine(float x, float y, float z, VertexConsumer buffer, MatrixStack.Entry matrices, float segmentStart, float segmentEnd) {
		float f = x * segmentStart;
		float g = y * (segmentStart * segmentStart + segmentStart) * 0.5f + 0.25f;
		float h = z * segmentStart;
		float i = x * segmentEnd - f;
		float j = y * (segmentEnd * segmentEnd + segmentEnd) * 0.5f + 0.25f - g;
		float k = z * segmentEnd - h;
		float l = MathHelper.sqrt(i * i + j * j + k * k);
		buffer.vertex(matrices.getPositionMatrix(), f, g, h).color(0, 0, 0, 255).normal(matrices.getNormalMatrix(), i /= l, j /= l, k /= l).next();
	}

	@Override
	public Identifier getTexture(ModFishingBobberEntity entity) {
		return new Identifier("textures/entity/fishing_hook.png");
	}
}
