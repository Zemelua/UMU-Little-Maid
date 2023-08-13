package io.github.zemelua.umu_little_maid.register;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.MaidPersonality;
import io.github.zemelua.umu_little_maid.entity.maid.job.IMaidJob;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class ModRegistries {
	public static final Marker MARKER = MarkerManager.getMarker("REGISTRY").addParents(UMULittleMaid.MARKER);

	public static final Registry<MaidPersonality> MAID_PERSONALITY;
	public static final Registry<IMaidJob> MAID_JOB;

	public static void initialize() {
		UMULittleMaid.LOGGER.info(ModRegistries.MARKER, "Registries are initialized!");
	}

	static {
		MAID_PERSONALITY = FabricRegistryBuilder
				.<MaidPersonality>createSimple(RegistryKey.ofRegistry(UMULittleMaid.identifier("maid_personality")))
				.buildAndRegister();
		MAID_JOB = FabricRegistryBuilder
				.<IMaidJob>createSimple(RegistryKey.ofRegistry(UMULittleMaid.identifier("maid_job")))
				.buildAndRegister();
	}

	private ModRegistries() {}
}
