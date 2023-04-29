package io.github.zemelua.umu_little_maid.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;

public class ShockwaveParticleFactory implements ParticleFactory<DefaultParticleType> {
	private final SpriteProvider sprites;

	public ShockwaveParticleFactory(SpriteProvider sprites) {
		this.sprites = sprites;
	}

	@Nullable
	@Override
	public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		return new ShockwaveParticle(world, x, y, z, this.sprites);
	}
}
