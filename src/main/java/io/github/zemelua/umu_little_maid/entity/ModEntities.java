package io.github.zemelua.umu_little_maid.entity;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.personality.MaidPersonality;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import net.minecraft.entity.data.TrackedDataHandler;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class ModEntities {
	public static final Marker MARKER = MarkerManager.getMarker("ENTITY").addParents(UMULittleMaid.MARKER);

	public static final TrackedDataHandler<MaidPersonality> PERSONALITY_HANDLER;

	private ModEntities() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	private static boolean initialized = false;

	public static void initialize() {
		if (ModEntities.initialized) throw new IllegalStateException("Entities are already initialized!");

		ModEntities.initialized = true;
		UMULittleMaid.LOGGER.info(ModEntities.MARKER, "Entities are initialized!");
	}

	static {
		PERSONALITY_HANDLER = TrackedDataHandler.of(ModRegistries.MAID_PERSONALITY);
	}
}
