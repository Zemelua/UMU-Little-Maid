package io.github.zemelua.umu_little_maid.network;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class NetworkHandler {
	public static final Marker MARKER = MarkerManager.getMarker("NETWORK").addParents(UMULittleMaid.MARKER);

	public static final Identifier CHANNEL_MAID_EAT_PARTICLE = UMULittleMaid.identifier("maid_eat_particle");
	public static final Identifier CHANNEL_CLIENT_INSTRUCTION_FINISH = UMULittleMaid.identifier("instruction_finish");
	public static final Identifier CHANNEL_CLIENT_HEADPATTING_START = UMULittleMaid.identifier("headpatting_start");
	public static final Identifier CHANNEL_CLIENT_HEADPATTING_FINISH = UMULittleMaid.identifier("headpatting_finish");

	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(CHANNEL_CLIENT_INSTRUCTION_FINISH, (server, player, handler, packet, sender)
				-> server.execute(() -> PacketHandlers.handleInstructionFinish(player)));
		ServerPlayNetworking.registerGlobalReceiver(CHANNEL_CLIENT_HEADPATTING_START, (server, player, handler, packet, sender) -> {
			int maidID = packet.readInt();

			server.execute(() -> PacketHandlers.handleStartHeadpatting(player, player.getWorld(), maidID));
		});
		ServerPlayNetworking.registerGlobalReceiver(CHANNEL_CLIENT_HEADPATTING_FINISH, (server, player, handler, packet, sender)
				-> server.execute(() -> PacketHandlers.handleFinishHeadpatting(player)));
	}

	@Deprecated
	private NetworkHandler() {
		UMULittleMaid.LOGGER.warn(NetworkHandler.MARKER, "NetworkHandler をインスタンス化しないで！");
	}
}
