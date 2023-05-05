package io.github.zemelua.umu_little_maid.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.zemelua.umu_little_maid.particle.ModParticles;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class ZZZParticle extends SpriteBillboardParticle {
	private int delay;

	protected ZZZParticle(ClientWorld world, double x, double y, double z, double xVel, double yVel, double zVel, int index, SpriteProvider sprites) {
		super(world, x, y, z);

		this.setVelocity(xVel, yVel, zVel);
		this.setMaxAge(20 - index * 4);
		this.scale = 0.3F * (1 - index * 0.15F);
		this.setSpriteForAge(sprites);
		this.velocityMultiplier = 0.8F - ((float) Math.pow(index, 2.0D) * 0.17F);

		this.delay = index * 5;
	}

	@Override
	public void tick() {
		if (this.delay > 0) {
			this.delay--;
			return;
		}

		super.tick();
		if (this.age >= this.getMaxAge() - 2) {
			this.scale(1.0F - (this.age - (this.getMaxAge() - 2)) * 0.5F);
		}
	}

	@Override
	public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
		if (this.delay > 0) {
			return;
		}

		super.buildGeometry(vertexConsumer, camera, tickDelta);
	}

	@Override
	public ParticleTextureSheet getType() {
		return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
	}

	public record Mediator(int index) implements ParticleEffect {
		public static final ParticleEffect.Factory<ZZZParticle.Mediator> FACTORY = new Factory<>() {
			@Override
			public Mediator read(ParticleType<Mediator> type, StringReader reader) throws CommandSyntaxException {
				return new Mediator(reader.readInt());
			}

			@Override
			public Mediator read(ParticleType<Mediator> type, PacketByteBuf packet) {
				return new Mediator(packet.readInt());
			}
		};

		public static final Codec<ZZZParticle.Mediator> CODEC = RecordCodecBuilder.create(instance
				-> instance.group((Codec.INT.fieldOf("index")).forGetter(Mediator::index))
				.apply(instance, ZZZParticle.Mediator::new));

		@Override
		public ParticleType<?> getType() {
			return ModParticles.ZZZ;
		}

		@Override
		public void write(PacketByteBuf packet) {
			packet.writeInt(this.index);
		}

		@Override
		public String asString() {
			return "";
		}
	}
}
