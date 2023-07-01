package io.github.zemelua.umu_little_maid.inventory;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ModInventories {
	public static final Marker MARKER = MarkerManager.getMarker("INVENTORY").addParents(UMULittleMaid.MARKER);

	public static final ScreenHandlerType<LittleMaidScreenHandler> LITTLE_MAID;

	private static boolean initialized = false;
	public static void initialize() {
		if (ModInventories.initialized) throw new IllegalStateException("Inventories are already initialized!");

		Registry.register(Registries.SCREEN_HANDLER, UMULittleMaid.identifier("little_maid"), ModInventories.LITTLE_MAID);

		ModInventories.initialized = true;
		UMULittleMaid.LOGGER.info(ModInventories.MARKER, "Inventories are initialized!");
	}

	private ModInventories() throws IllegalAccessException {throw new IllegalAccessException();}

	static {
		LITTLE_MAID = new ExtendedScreenHandlerType<>(LittleMaidScreenHandler::new);
	}
}
