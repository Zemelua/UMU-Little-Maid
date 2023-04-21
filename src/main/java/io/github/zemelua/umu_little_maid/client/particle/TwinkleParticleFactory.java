package io.github.zemelua.umu_little_maid.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.util.Color;

public class TwinkleParticleFactory implements ParticleFactory<DefaultParticleType> {
	private static final Color YELLOW = Color.ofOpaque(0xfff759);
	private static final Color ORANGE = Color.ofOpaque(0xffdc42);

	private final SpriteProvider spriteProvider;

	public TwinkleParticleFactory(SpriteProvider spriteProvider) {
		this.spriteProvider = spriteProvider;
	}

	@Nullable
	@Override
	public Particle createParticle(DefaultParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		TwinkleParticle particle = new TwinkleParticle(world, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
		particle.setVelocity(particle.getVelocityX() * 0.3, particle.getVelocityY() * 0.1, particle.getVelocityZ() * 0.3);

		if (world.getRandom().nextBoolean()) {
			particle.setColor(YELLOW.getRed() / 255.0F, YELLOW.getGreen() / 255.0F, YELLOW.getBlue() / 255.0F);
		} else {
			particle.setColor(ORANGE.getRed() / 255.0F, ORANGE.getGreen() / 255.0F, ORANGE.getBlue() / 255.0F);
		}
		particle.setMaxAge((int)(8.0 / (world.getRandom().nextDouble() * 0.8 + 0.2)));

		return particle;
	}
}
