package io.github.zemelua.umu_little_maid.network;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.tag.ModTags;
import io.github.zemelua.umu_little_maid.util.ItemParticleScaleManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;

public final class NetworkHandler {
	public static final Identifier CHANNEL_MAID_EAT_PARTICLE = UMULittleMaid.identifier("particle_size");

	public static final ClientPlayNetworking.PlayChannelHandler HANDLER_MAID_EAT_PARTICLE;

	private static boolean initialized = false;
	public static void initialize() {
		if (NetworkHandler.initialized) throw new IllegalStateException("Network handler is already initialized!");

		ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.CHANNEL_MAID_EAT_PARTICLE, NetworkHandler.HANDLER_MAID_EAT_PARTICLE);

		NetworkHandler.initialized = true;
		UMULittleMaid.LOGGER.info(ModTags.MARKER, "Network handler has initialized!");
	}

	@Deprecated private NetworkHandler() {throw new UnsupportedOperationException();}

	static {
		HANDLER_MAID_EAT_PARTICLE = (client, handler, packet, sender) -> {
			final ItemStack item = packet.readItemStack();
			final double x = packet.readDouble();
			final double y = packet.readDouble();
			final double z = packet.readDouble();
			final double xDelta = packet.readDouble();
			final double yDelta = packet.readDouble();
			final double zDelta = packet.readDouble();

			if (client.world != null) {
				ItemParticleScaleManager.setSize(0.75F);

				final ParticleEffect particle = new ItemStackParticleEffect(ParticleTypes.ITEM, item);
				client.world.addParticle(particle, x, y, z, xDelta, yDelta, zDelta);

				ItemParticleScaleManager.setSize(1.0F);
			}
		};
	}
}
