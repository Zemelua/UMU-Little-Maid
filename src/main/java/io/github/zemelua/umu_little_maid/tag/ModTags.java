package io.github.zemelua.umu_little_maid.tag;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class ModTags {
	public static final Marker MARKER = MarkerManager.getMarker("TAG").addParents(UMULittleMaid.MARKER);

	public static final TagKey<Block> BLOCK_MAID_HARVESTS;
	public static final TagKey<Block> BLOCK_SCARECROW_HEAD;

	public static final TagKey<Item> ITEM_MAID_FENCER_TOOLS;
	public static final TagKey<Item> ITEM_MAID_CRACKER_TOOLS;
	public static final TagKey<Item> ITEM_MAID_ARCHER_TOOLS;
	public static final TagKey<Item> ITEM_MAID_GUARD_TOOLS;
	public static final TagKey<Item> ITEM_MAID_FARMER_TOOLS;
	public static final TagKey<Item> ITEM_MAID_HEALER_TOOLS;
	public static final TagKey<Item> ITEM_MAID_POSEIDON_TOOLS;
	public static final TagKey<Item> ITEM_MAID_CONTRACT_FOODS;
	public static final TagKey<Item> ITEM_MAID_HEAL_FOODS;
	public static final TagKey<Item> ITEM_MAID_REINFORCE_FOODS;
	public static final TagKey<Item> ITEM_MAID_DRIPLEAFS;
	public static final TagKey<Item> ITEM_MAID_CHANGE_COSTUMES;
	public static final TagKey<Item> ITEM_MAID_CROPS;
	public static final TagKey<Item> ITEM_MAID_PRODUCTS;

	public static final TagKey<EntityType<?>> ENTITY_MAID_GENERAL_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_BRAVERY_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_DILIGENT_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_AUDACIOUS_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_GENTLE_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_SHY_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_LAZY_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_TSUNDERE_HOSTILES;

	public static final TagKey<PointOfInterestType> POI_FARMER;

	private static boolean initialized = false;
	public static void initialize() {
		if (ModTags.initialized) throw new IllegalStateException("Tags are already initialized!");

		ModTags.initialized = true;
		UMULittleMaid.LOGGER.info(ModTags.MARKER, "Tags are initialized!");
	}

	private ModTags() throws IllegalAccessException {throw new IllegalAccessException();}

	static {
		BLOCK_MAID_HARVESTS = TagKey.of(Registry.BLOCK_KEY, UMULittleMaid.identifier("maid_harvests"));
		BLOCK_SCARECROW_HEAD = TagKey.of(Registry.BLOCK_KEY, UMULittleMaid.identifier("scarecrow_head"));

		ITEM_MAID_FENCER_TOOLS = TagKey.of(Registry.ITEM_KEY, UMULittleMaid.identifier("maid_fencer_tools"));
		ITEM_MAID_CRACKER_TOOLS = TagKey.of(Registry.ITEM_KEY, UMULittleMaid.identifier("maid_cracker_tools"));
		ITEM_MAID_ARCHER_TOOLS = TagKey.of(Registry.ITEM_KEY, UMULittleMaid.identifier("maid_archer_tools"));
		ITEM_MAID_GUARD_TOOLS = TagKey.of(Registry.ITEM_KEY, UMULittleMaid.identifier("maid_guard_tools"));
		ITEM_MAID_FARMER_TOOLS = TagKey.of(Registry.ITEM_KEY, UMULittleMaid.identifier("maid_farmer_tools"));
		ITEM_MAID_HEALER_TOOLS = TagKey.of(Registry.ITEM_KEY, UMULittleMaid.identifier("maid_healer_tools"));
		ITEM_MAID_POSEIDON_TOOLS = TagKey.of(Registry.ITEM_KEY, UMULittleMaid.identifier("maid_poseidon_tools"));
		ITEM_MAID_CONTRACT_FOODS = TagKey.of(Registry.ITEM_KEY, UMULittleMaid.identifier("maid_contract_foods"));
		ITEM_MAID_HEAL_FOODS = TagKey.of(Registry.ITEM_KEY, UMULittleMaid.identifier("maid_heal_foods"));
		ITEM_MAID_REINFORCE_FOODS = TagKey.of(Registry.ITEM_KEY, UMULittleMaid.identifier("maid_reinforce_foods"));
		ITEM_MAID_DRIPLEAFS = TagKey.of(Registry.ITEM_KEY, UMULittleMaid.identifier("maid_dripleafs"));
		ITEM_MAID_CHANGE_COSTUMES = TagKey.of(Registry.ITEM_KEY, UMULittleMaid.identifier("maid_change_costumes"));
		ITEM_MAID_CROPS = TagKey.of(Registry.ITEM_KEY, UMULittleMaid.identifier("maid_crops"));
		ITEM_MAID_PRODUCTS = TagKey.of(Registry.ITEM_KEY, UMULittleMaid.identifier("maid_products"));

		ENTITY_MAID_GENERAL_HOSTILES = TagKey.of(Registry.ENTITY_TYPE_KEY, UMULittleMaid.identifier("maid_general_hostiles"));
		ENTITY_MAID_BRAVERY_HOSTILES = TagKey.of(Registry.ENTITY_TYPE_KEY, UMULittleMaid.identifier("maid_bravery_hostiles"));
		ENTITY_MAID_DILIGENT_HOSTILES = TagKey.of(Registry.ENTITY_TYPE_KEY, UMULittleMaid.identifier("maid_diligent_hostiles"));
		ENTITY_MAID_AUDACIOUS_HOSTILES = TagKey.of(Registry.ENTITY_TYPE_KEY, UMULittleMaid.identifier("maid_audacious_hostiles"));
		ENTITY_MAID_GENTLE_HOSTILES = TagKey.of(Registry.ENTITY_TYPE_KEY, UMULittleMaid.identifier("maid_gentle_hostiles"));
		ENTITY_MAID_SHY_HOSTILES = TagKey.of(Registry.ENTITY_TYPE_KEY, UMULittleMaid.identifier("maid_shy_hostiles"));
		ENTITY_MAID_LAZY_HOSTILES = TagKey.of(Registry.ENTITY_TYPE_KEY, UMULittleMaid.identifier("maid_lazy_hostiles"));
		ENTITY_MAID_TSUNDERE_HOSTILES = TagKey.of(Registry.ENTITY_TYPE_KEY, UMULittleMaid.identifier("maid_tsundere_hostiles"));

		POI_FARMER = TagKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, UMULittleMaid.identifier("farmer"));
	}
}
