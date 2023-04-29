package io.github.zemelua.umu_little_maid.client.particle;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class ShockParticle extends SpriteBillboardParticle {
	private final SpriteProvider sprites;

	public ShockParticle(ClientWorld clientWorld, double x, double y, double z, SpriteProvider sprites) {
		super(clientWorld, x, y, z);

		this.sprites = sprites;

		this.setMaxAge(4);
		this.scale(4.0F);
		this.setSpriteForAge(this.sprites);
	}

	@Override
	public void tick() {
		super.tick();

		this.setSpriteForAge(this.sprites);
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}
}
