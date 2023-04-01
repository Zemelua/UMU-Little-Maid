package io.github.zemelua.umu_little_maid.register;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.MaidJob;
import io.github.zemelua.umu_little_maid.entity.maid.MaidPersonality;
import io.github.zemelua.umu_little_maid.entity.maid.feeling.IMaidFeeling;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class ModRegistries {
	public static final Marker MARKER = MarkerManager.getMarker("REGISTRY").addParents(UMULittleMaid.MARKER);

	public static final Registry<MaidPersonality> MAID_PERSONALITY;
	public static final Registry<MaidJob> MAID_JOB;
	public static final Registry<IMaidFeeling> MAID_FEELING;

	public static void initialize() {
		UMULittleMaid.LOGGER.info(ModRegistries.MARKER, "Registries are initialized!");
	}

	static {
		MAID_PERSONALITY = FabricRegistryBuilder
				.createSimple(MaidPersonality.class, UMULittleMaid.identifier("maid_personality"))
				.buildAndRegister();
		MAID_JOB = FabricRegistryBuilder
				.createSimple(MaidJob.class, UMULittleMaid.identifier("maid_job"))
				.buildAndRegister();
		MAID_FEELING = FabricRegistryBuilder
				.createSimple(IMaidFeeling.class, UMULittleMaid.identifier("maid_feeling"))
				.buildAndRegister();
	}

	private ModRegistries() {}
}
