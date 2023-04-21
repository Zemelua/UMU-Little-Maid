package io.github.zemelua.umu_little_maid.client.particle;

import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class TwinkleParticle extends GlowParticle {
	public TwinkleParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteProvider spriteProvider) {
		super(world, x, y, z, velocityX, velocityY, velocityZ, spriteProvider);
	}

	@Override
	public void tick() {
		super.tick();

		if (this.age <= 4) {
			this.scale(1.25F);
		} else {
			this.scale(0.85F);
		}
	}

	public double getVelocityX() {
		return this.velocityX;
	}

	public double getVelocityY() {
		return this.velocityY;
	}

	public double getVelocityZ() {
		return this.velocityZ;
	}
}
