package io.github.zemelua.umu_little_maid.network;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.network.client.MaidEatParticleHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class NetworkHandler {
	public static final Marker MARKER = MarkerManager.getMarker("NETWORK").addParents(UMULittleMaid.MARKER);

	public static final Identifier CHANNEL_MAID_EAT_PARTICLE = UMULittleMaid.identifier("particle_size");

	public static final ClientPlayNetworking.PlayChannelHandler HANDLER_MAID_EAT_PARTICLE;

	private static boolean initialized = false;
	public static void initialize() {
		if (NetworkHandler.initialized) throw new IllegalStateException("Network handler is already initialized!");

		ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.CHANNEL_MAID_EAT_PARTICLE, NetworkHandler.HANDLER_MAID_EAT_PARTICLE);

		NetworkHandler.initialized = true;
		UMULittleMaid.LOGGER.info(NetworkHandler.MARKER, "Network handler has initialized!");
	}

	@Deprecated private NetworkHandler() {throw new UnsupportedOperationException();}

	static {
		HANDLER_MAID_EAT_PARTICLE = MaidEatParticleHandler.INSTANCE;
	}
}
