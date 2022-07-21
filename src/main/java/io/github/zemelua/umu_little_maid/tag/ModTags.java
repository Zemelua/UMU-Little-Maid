package io.github.zemelua.umu_little_maid.tag;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.minecraft.block.Block;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class ModTags {
	public static final Marker MARKER = MarkerManager.getMarker("TAG").addParents(UMULittleMaid.MARKER);

	public static final TagKey<Block> BLOCK_SCARECROW_HEAD;
	public static final TagKey<PointOfInterestType> POI_FARMER;

	private static boolean initialized = false;

	public static void initialize() {
		if (ModTags.initialized) throw new IllegalStateException("Tags are already initialized!");

		ModTags.initialized = true;
		UMULittleMaid.LOGGER.info(ModTags.MARKER, "Tags are initialized!");
	}

	private ModTags() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	static {
		BLOCK_SCARECROW_HEAD = TagKey.of(Registry.BLOCK_KEY, UMULittleMaid.identifier("scarecrow_head"));
		POI_FARMER = TagKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, UMULittleMaid.identifier("farmer"));
	}
}
