package io.github.zemelua.umu_little_maid.util;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

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
}
