package io.github.zemelua.umu_little_maid.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;

public class ShockParticleFactory implements ParticleFactory<DefaultParticleType> {
	private final SpriteProvider sprites;

	public ShockParticleFactory(SpriteProvider sprites) {
		this.sprites = sprites;
	}

	@Nullable
	@Override
	public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		return new ShockParticle(world, x, y, z, this.sprites);
	}
}
