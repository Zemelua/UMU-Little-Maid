package io.github.zemelua.umu_little_maid;

import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.inventory.ModInventories;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
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

		ModRegistries.initialize();
		ModEntities.initialize();
		ModInventories.initialize();

		UMULittleMaid.LOGGER.info(UMULittleMaid.MARKER, "Succeeded initializing mod!");
	}

	public static Identifier identifier(String path) {
		return new Identifier(UMULittleMaid.MOD_ID, path);
	}
}
