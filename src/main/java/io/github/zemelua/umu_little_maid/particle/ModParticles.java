package io.github.zemelua.umu_little_maid.particle;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.registry.Registry;

public final class ModParticles {
	public static final DefaultParticleType TWINKLE = FabricParticleTypes.simple();

	public static void init() {
		Registry.register(Registry.PARTICLE_TYPE, UMULittleMaid.identifier("twinkle"), TWINKLE);
	}

	private ModParticles() {}
}
