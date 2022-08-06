package io.github.zemelua.umu_little_maid.network;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class NetworkHandler {
	public static final Marker MARKER = MarkerManager.getMarker("NETWORK").addParents(UMULittleMaid.MARKER);

	public static final Identifier CHANNEL_MAID_EAT_PARTICLE = UMULittleMaid.identifier("maid_eat_particle");

	@Deprecated
	private NetworkHandler() {
		UMULittleMaid.LOGGER.warn(NetworkHandler.MARKER, "NetworkHandler をインスタンス化しないで！");
	}
}
