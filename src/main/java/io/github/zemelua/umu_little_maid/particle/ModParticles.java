package io.github.zemelua.umu_little_maid.particle;

import com.mojang.serialization.Codec;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.client.particle.ZZZParticle;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public final class ModParticles {
	public static final DefaultParticleType TWINKLE = FabricParticleTypes.simple();
	public static final DefaultParticleType SHOCK = FabricParticleTypes.simple();
	public static final DefaultParticleType SHOCKWAVE = FabricParticleTypes.simple();
	public static final ParticleType<ZZZParticle.Mediator> ZZZ = new ParticleType<>(false, ZZZParticle.Mediator.FACTORY) {
		@Override
		public Codec<ZZZParticle.Mediator> getCodec() {
			return ZZZParticle.Mediator.CODEC;
		}
	};

	public static void init() {
		Registry.register(Registries.PARTICLE_TYPE, UMULittleMaid.identifier("twinkle"), TWINKLE);
		Registry.register(Registries.PARTICLE_TYPE, UMULittleMaid.identifier("shock"), SHOCK);
		Registry.register(Registries.PARTICLE_TYPE, UMULittleMaid.identifier("shockwave"), SHOCKWAVE);
		Registry.register(Registries.PARTICLE_TYPE, UMULittleMaid.identifier("zzz"), ZZZ);
	}

	private ModParticles() {}
}
