package io.github.zemelua.umu_little_maid.command;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class ModCommands {
	public static final Marker MARKER = MarkerManager.getMarker("COMMAND").addParents(UMULittleMaid.MARKER);

	private static boolean initialized = false;

	@SuppressWarnings("CommentedOutCode")
	public static void initialize() {
		if (ModCommands.initialized) throw new IllegalStateException("Commands are already initialized!");

//		CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
//			dispatcher.register(MaidCommand.INSTANCE);
//		}));

		ModCommands.initialized = true;
		UMULittleMaid.LOGGER.info(ModCommands.MARKER, "Commands are initialized!");
	}

	private ModCommands() throws IllegalAccessException {
		throw new IllegalAccessException();
	}
}
