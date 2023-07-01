package io.github.zemelua.umu_little_maid.entity;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

public final class ModPOIs {
	public static final RegistryKey<PointOfInterestType> POI_DESSERT = RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, UMULittleMaid.identifier("dessert"));

	public static void init() {
		PointOfInterestTypes.register(Registries.POINT_OF_INTEREST_TYPE, POI_DESSERT, PointOfInterestTypes.getStatesOfBlock(Blocks.CAKE), 1, 1);
	}

	private ModPOIs() {}
}
