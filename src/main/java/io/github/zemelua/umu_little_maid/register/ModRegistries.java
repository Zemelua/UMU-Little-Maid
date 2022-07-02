package io.github.zemelua.umu_little_maid.register;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.job.MaidJob;
import io.github.zemelua.umu_little_maid.entity.maid.personality.MaidPersonality;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class ModRegistries {
	public static final Marker MARKER = MarkerManager.getMarker("REGISTRY").addParents(UMULittleMaid.MARKER);

	public static final Registry<MaidPersonality> MAID_PERSONALITY;
	public static final Registry<MaidJob> MAID_JOB;

	private ModRegistries() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	private static boolean initialized = false;

	public static void initialize() {
		if (ModRegistries.initialized) throw new IllegalStateException("Registries are already initialized!");

		ModRegistries.initialized = true;
		UMULittleMaid.LOGGER.info(ModRegistries.MARKER, "Registries are initialized!");
	}

	static {
		MAID_PERSONALITY = FabricRegistryBuilder
				.createSimple(MaidPersonality.class, UMULittleMaid.identifier("maid_personality"))
				.buildAndRegister();
		MAID_JOB = FabricRegistryBuilder
				.createSimple(MaidJob.class, UMULittleMaid.identifier("maid_job"))
				.buildAndRegister();
	}
}
