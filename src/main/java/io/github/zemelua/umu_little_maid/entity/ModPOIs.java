package io.github.zemelua.umu_little_maid.entity;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

public final class ModPOIs {
	public static final RegistryKey<PointOfInterestType> POI_DESSERT = RegistryKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, UMULittleMaid.identifier("dessert"));

	public static void init() {
		PointOfInterestTypes.register(Registry.POINT_OF_INTEREST_TYPE, POI_DESSERT, PointOfInterestTypes.getStatesOfBlock(Blocks.CAKE), 1, 1);
	}

	private ModPOIs() {}
}
