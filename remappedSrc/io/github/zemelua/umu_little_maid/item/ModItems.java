package io.github.zemelua.umu_little_maid.item;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.mixin.SpawnEggItemAccessor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class ModItems {
	public static final Marker MARKER = MarkerManager.getMarker("ITEM").addParents(UMULittleMaid.MARKER);

	public static final Item LITTLE_MAID_SPAWN_EGG;

	private ModItems() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	private static boolean initialized = false;

	public static void initialize() {
		if (ModItems.initialized) throw new IllegalStateException("Inventories are already initialized!");

		Registry.register(Registry.ITEM, UMULittleMaid.identifier("little_maid_spawn_egg"), ModItems.LITTLE_MAID_SPAWN_EGG);
		SpawnEggItemAccessor.getSPAWN_EGGS().remove(ModEntities.LITTLE_MAID);

		ModItems.initialized = true;
		UMULittleMaid.LOGGER.info(ModItems.MARKER, "Items are initialized!");
	}

	static {
		LITTLE_MAID_SPAWN_EGG = new SpawnEggItem(ModEntities.LITTLE_MAID, 0, 0, new Item.Settings().group(ItemGroup.MISC));
	}
}
