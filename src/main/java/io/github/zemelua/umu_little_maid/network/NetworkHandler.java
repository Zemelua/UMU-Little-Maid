package io.github.zemelua.umu_little_maid.network;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.tag.ModTags;
import io.github.zemelua.umu_little_maid.util.ItemParticleScaleManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public final class NetworkHandler {
	public static final Identifier CHANNEL_PARTICLE_SIZE = UMULittleMaid.identifier("particle_size");

	public static final ClientPlayNetworking.PlayChannelHandler HANDLER_PARTICLE_SIZE;

	private static boolean initialized = false;
	public static void initialize() {
		if (NetworkHandler.initialized) throw new IllegalStateException("Network handler is already initialized!");

		ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.CHANNEL_PARTICLE_SIZE, NetworkHandler.HANDLER_PARTICLE_SIZE);

		NetworkHandler.initialized = true;
		UMULittleMaid.LOGGER.info(ModTags.MARKER, "Network handler has initialized!");
	}

	@Deprecated private NetworkHandler() {throw new UnsupportedOperationException();}

	static {
		HANDLER_PARTICLE_SIZE = (client, handler, packet, sender) -> ItemParticleScaleManager.setSize(packet.readFloat());
	}
}
