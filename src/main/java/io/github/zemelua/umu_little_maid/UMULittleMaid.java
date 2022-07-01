package io.github.zemelua.umu_little_maid;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class UMULittleMaid implements ModInitializer {
	public static final String MOD_ID = "umu_little_maid";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Marker MARKER = MarkerManager.getMarker("UMU_LITTLE_MAID");

	@Override
	public void onInitialize() {
		UMULittleMaid.LOGGER.info(UMULittleMaid.MARKER, "Start initializing mod!");

		UMULittleMaid.LOGGER.info(UMULittleMaid.MARKER, "Succeeded initializing mod!");
	}
}
