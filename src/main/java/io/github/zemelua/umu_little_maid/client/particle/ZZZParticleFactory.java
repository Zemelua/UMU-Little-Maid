package io.github.zemelua.umu_little_maid.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;

public class ZZZParticleFactory implements ParticleFactory<ZZZParticle.Mediator> {
	private final SpriteProvider sprites;

	public ZZZParticleFactory(SpriteProvider sprites) {
		this.sprites = sprites;
	}

	@Nullable
	@Override
	public Particle createParticle(ZZZParticle.Mediator parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		return new ZZZParticle(world, x, y, z, velocityX, velocityY, velocityZ, parameters.index(),  this.sprites);
	}
}
