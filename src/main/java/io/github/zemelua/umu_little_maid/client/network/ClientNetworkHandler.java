package io.github.zemelua.umu_little_maid.client.network;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.client.UMULittleMaidClient;
import io.github.zemelua.umu_little_maid.network.NetworkHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class ClientNetworkHandler {
	public static final Marker MARKER = MarkerManager.getMarker("NETWORK").addParents(UMULittleMaidClient.MARKER);

	private static boolean initialized = false;
	public static void initializeClient() {
		if (ClientNetworkHandler.initialized) UMULittleMaid.LOGGER.error("Network handler is already initialized!");

		ClientPlayNetworking.registerGlobalReceiver(NetworkHandler.CHANNEL_MAID_EAT_PARTICLE, (client, handler, packet, sender) -> {
			final ItemStack item = packet.readItemStack();
			final double xPos = packet.readDouble();
			final double yPos = packet.readDouble();
			final double zPos = packet.readDouble();
			final double xDelta = packet.readDouble();
			final double yDelta = packet.readDouble();
			final double zDelta = packet.readDouble();
			final float scale = 0.73F;

			client.execute(() -> {
				final World world = client.world;

				if (world == null) {
					UMULittleMaid.LOGGER.warn(ClientNetworkHandler.MARKER, "このパケットはワールドに入ってるときに送ってね！");
					return;
				}

				MaidEatParticleHandler.handle(world, item, xPos, yPos, zPos, xDelta, yDelta, zDelta, scale);
			});
		});

		ClientNetworkHandler.initialized = true;
		UMULittleMaid.LOGGER.info(ClientNetworkHandler.MARKER, "Network handler has initialized!");
	}

	@Deprecated
	private ClientNetworkHandler() {
		UMULittleMaid.LOGGER.warn(ClientNetworkHandler.MARKER, "ClientNetworkHandler をインスタンス化しないで！");
	}
}
