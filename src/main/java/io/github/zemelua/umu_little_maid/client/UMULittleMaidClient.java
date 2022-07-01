package io.github.zemelua.umu_little_maid.client;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@Environment(EnvType.CLIENT)
public class UMULittleMaidClient implements ClientModInitializer {
	public static final Marker MARKER = MarkerManager.getMarker("UMU_LITTLE_MAID_CLIENT");

	@Override
	public void onInitializeClient() {
		UMULittleMaid.LOGGER.info(UMULittleMaidClient.MARKER, "Start initializing mod client!");

		UMULittleMaid.LOGGER.info(UMULittleMaidClient.MARKER, "Succeeded initializing mod client!");
	}
}
