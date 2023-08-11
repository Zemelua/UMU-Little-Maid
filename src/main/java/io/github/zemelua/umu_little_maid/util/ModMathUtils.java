package io.github.zemelua.umu_little_maid.util;

import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.*;

import java.lang.Math;

public final class ModMathUtils {
	public static Vec3d rotationToVector(double pitch, double yaw) {
		double pitchRad = Math.toRadians(pitch);
		double yawRad = Math.toRadians(-yaw);
		double h = Math.cos(yawRad);
		double i = Math.sin(yawRad);
		double j = Math.cos(pitchRad);
		double k = Math.sin(pitchRad);
		return new Vec3d(i * j, -k, h * j);
	}

	public static void scaleMatrices(MatrixStack matrices, float scale) {
		matrices.scale(scale, scale, scale);
	}

	@Nullable
	public static Vector2dc project3DTo2D(Vector3d pos3D, Camera camera, Matrix4f modelViewMatrix, Matrix4f projectionMatrix, int windowW, int windowH, double scaleFactor) {
		Vector3d cameraPos = ModMathUtils.vecToVector(camera.getPos());

		Vector4d vector = new Vector4d(new Vector3d(pos3D).sub(cameraPos), 1.0D);
		product(modelViewMatrix, vector);
		product(projectionMatrix, vector);

		if (vector.w() <= 0.0D) return null;

		double w = (1.0D / vector.w()) * 0.5D;
		double x = vector.x() * w + 0.5F;
		double y = vector.y() * w + 0.5F;

		return new Vector2d(x * windowW / scaleFactor, (windowH - y * windowH) / scaleFactor);
	}

	private static void product(Matrix4f matrix4, Vector4d vector4) {
		vector4.set(
				vector4.x() * matrix4.m00() + vector4.y() * matrix4.m10() + vector4.z() * matrix4.m20() + vector4.w() * matrix4.m30(),
				vector4.x() * matrix4.m01() + vector4.y() * matrix4.m11() + vector4.z() * matrix4.m21() + vector4.w() * matrix4.m31(),
				vector4.x() * matrix4.m02() + vector4.y() * matrix4.m12() + vector4.z() * matrix4.m22() + vector4.w() * matrix4.m32(),
				vector4.x() * matrix4.m03() + vector4.y() * matrix4.m13() + vector4.z() * matrix4.m23() + vector4.w() * matrix4.m33()
		);
	}

	public static Vector3d vecToVector(Vec3d vec3d) {
		return new Vector3d(vec3d.getX(), vec3d.getY(), vec3d.getZ());
	}
}
