package io.github.zemelua.umu_little_maid.data.tag;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.MaidPersonality;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@SuppressWarnings("SpellCheckingInspection")
public final class ModTags {
	public static final Marker MARKER = MarkerManager.getMarker("TAG").addParents(UMULittleMaid.MARKER);

	public static final TagKey<Block> BLOCK_MAID_SETTABLE_AS_HOME;
	public static final TagKey<Block> BLOCK_MAID_SETTABLE_AS_DELIVERY_BOX;
	public static final TagKey<Block> BLOCK_MAID_HARVESTS;
	public static final TagKey<Block> BLOCK_MAID_GOURDS;
	public static final TagKey<Block> BLOCK_SCARECROW_HEAD;

	public static final TagKey<Item> ITEM_MAID_FENCER_TOOLS;
	public static final TagKey<Item> ITEM_MAID_CRACKER_TOOLS;
	public static final TagKey<Item> ITEM_MAID_ARCHER_TOOLS;
	public static final TagKey<Item> ITEM_MAID_GUARD_TOOLS;
	public static final TagKey<Item> ITEM_MAID_FARMER_TOOLS;
	public static final TagKey<Item> ITEM_MAID_HEALER_TOOLS;
	public static final TagKey<Item> ITEM_MAID_POSEIDON_TOOLS;
	public static final TagKey<Item> ITEM_MAID_HUNTER_TOOLS;
	public static final TagKey<Item> ITEM_MAID_SHEPHERD_TOOLS;
	public static final TagKey<Item> ITEM_MAID_CONTRACT_FOODS;
	public static final TagKey<Item> ITEM_MAID_INSTRUCTORS;
	public static final TagKey<Item> ITEM_MAID_HEAL_FOODS;
	public static final TagKey<Item> ITEM_MAID_REINFORCE_FOODS;
	public static final TagKey<Item> ITEM_MAID_DRIPLEAFS;
	public static final TagKey<Item> ITEM_MAID_CHANGE_COSTUMES;
	public static final TagKey<Item> ITEM_MAID_CROPS;
	public static final TagKey<Item> ITEM_MAID_HARVESTS;
	public static final TagKey<Item> ITEM_MAID_PRODUCTS;
	public static final TagKey<Item> ITEM_MAID_SHEPHERD_DELIVERS;
	public static final TagKey<Item> ITEM_MAID_CHORUS_FRUITS;

	public static final TagKey<EntityType<?>> ENTITY_MAID_GENERAL_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_BRAVERY_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_DILIGENT_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_AUDACIOUS_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_GENTLE_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_SHY_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_LAZY_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_TSUNDERE_HOSTILES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_BRAVERY_CHASES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_DILIGENT_CHASES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_AUDACIOUS_CHASES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_GENTLE_CHASES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_SHY_CHASES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_LAZY_CHASES;
	public static final TagKey<EntityType<?>> ENTITY_MAID_TSUNDERE_CHASES;

	public static final TagKey<PointOfInterestType> POI_MAID_FENCER_SITE;
	public static final TagKey<PointOfInterestType> POI_MAID_CRACKER_SITE;
	public static final TagKey<PointOfInterestType> POI_MAID_ARCHER_SITE;
	public static final TagKey<PointOfInterestType> POI_MAID_GUARD_SITE;
	public static final TagKey<PointOfInterestType> POI_MAID_FARMER_SITE;
	public static final TagKey<PointOfInterestType> POI_MAID_HEALER_SITE;
	public static final TagKey<PointOfInterestType> POI_MAID_POSEIDON_SITE;
	public static final TagKey<PointOfInterestType> POI_MAID_HUNTER_SITE;
	public static final TagKey<PointOfInterestType> POI_MAID_SHEPHERD_SITE;

	public static final TagKey<MaidPersonality> PERSONALITY_PERSISTENCES;
	public static final TagKey<MaidPersonality> PERSONALITY_FLUTTER_WHEN_KILLS;
	public static final TagKey<MaidPersonality> PERSONALITY_FLUTTER_WHEN_CRAFTS;
	public static final TagKey<MaidPersonality> PERSONALITY_FLUTTER_WHEN_KINDS;
	public static final TagKey<MaidPersonality> PERSONALITY_FLUTTER_WHEN_WAKE_UPS;
	public static final TagKey<MaidPersonality> PERSONALITY_DEVOTE_WHEN_ATTACK_OWNERS_ENEMIES;
	public static final TagKey<MaidPersonality> PERSONALITY_DEVOTE_WHEN_BOW_ATTACKS;
	public static final TagKey<MaidPersonality> PERSONALITY_DEVOTE_WHEN_HEAL_OWNERS;

	public static void initialize() {
		UMULittleMaid.LOGGER.info(ModTags.MARKER, "Tags are initialized!");
	}

	private ModTags() {}

	static {
		BLOCK_MAID_SETTABLE_AS_HOME = TagKey.of(RegistryKeys.BLOCK, UMULittleMaid.identifier("maid_settable_as_home"));
		BLOCK_MAID_SETTABLE_AS_DELIVERY_BOX = TagKey.of(RegistryKeys.BLOCK, UMULittleMaid.identifier("maid_settable_as_delivery_box"));
		BLOCK_MAID_HARVESTS = TagKey.of(RegistryKeys.BLOCK, UMULittleMaid.identifier("maid_harvests"));
		BLOCK_MAID_GOURDS = TagKey.of(RegistryKeys.BLOCK, UMULittleMaid.identifier("maid_gourds"));
		BLOCK_SCARECROW_HEAD = TagKey.of(RegistryKeys.BLOCK, UMULittleMaid.identifier("scarecrow_head"));

		ITEM_MAID_FENCER_TOOLS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_fencer_tools"));
		ITEM_MAID_CRACKER_TOOLS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_cracker_tools"));
		ITEM_MAID_ARCHER_TOOLS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_archer_tools"));
		ITEM_MAID_GUARD_TOOLS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_guard_tools"));
		ITEM_MAID_FARMER_TOOLS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_farmer_tools"));
		ITEM_MAID_HEALER_TOOLS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_healer_tools"));
		ITEM_MAID_POSEIDON_TOOLS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_poseidon_tools"));
		ITEM_MAID_HUNTER_TOOLS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_hunter_tools"));
		ITEM_MAID_SHEPHERD_TOOLS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_shepherd_tools"));
		ITEM_MAID_CONTRACT_FOODS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_contract_foods"));
		ITEM_MAID_INSTRUCTORS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_instructors"));
		ITEM_MAID_HEAL_FOODS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_heal_foods"));
		ITEM_MAID_REINFORCE_FOODS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_reinforce_foods"));
		ITEM_MAID_DRIPLEAFS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_dripleafs"));
		ITEM_MAID_CHANGE_COSTUMES = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_change_costumes"));
		ITEM_MAID_CROPS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_crops"));
		ITEM_MAID_HARVESTS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_harvests"));
		ITEM_MAID_PRODUCTS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_products"));
		ITEM_MAID_SHEPHERD_DELIVERS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_shepherd_delivers"));
		ITEM_MAID_CHORUS_FRUITS = TagKey.of(RegistryKeys.ITEM, UMULittleMaid.identifier("maid_chorus_fruits"));

		ENTITY_MAID_GENERAL_HOSTILES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_general_hostiles"));
		ENTITY_MAID_BRAVERY_HOSTILES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_bravery_hostiles"));
		ENTITY_MAID_DILIGENT_HOSTILES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_diligent_hostiles"));
		ENTITY_MAID_AUDACIOUS_HOSTILES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_audacious_hostiles"));
		ENTITY_MAID_GENTLE_HOSTILES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_gentle_hostiles"));
		ENTITY_MAID_SHY_HOSTILES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_shy_hostiles"));
		ENTITY_MAID_LAZY_HOSTILES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_lazy_hostiles"));
		ENTITY_MAID_TSUNDERE_HOSTILES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_tsundere_hostiles"));
		ENTITY_MAID_BRAVERY_CHASES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_bravery_chases"));
		ENTITY_MAID_DILIGENT_CHASES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_diligent_chases"));
		ENTITY_MAID_AUDACIOUS_CHASES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_audacious_chases"));
		ENTITY_MAID_GENTLE_CHASES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_gentle_chases"));
		ENTITY_MAID_SHY_CHASES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_shy_chases"));
		ENTITY_MAID_LAZY_CHASES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_lazy_chases"));
		ENTITY_MAID_TSUNDERE_CHASES = TagKey.of(RegistryKeys.ENTITY_TYPE, UMULittleMaid.identifier("maid_tsundere_chases"));

		POI_MAID_FENCER_SITE = TagKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, UMULittleMaid.identifier("maid_fencer_site"));
		POI_MAID_CRACKER_SITE = TagKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, UMULittleMaid.identifier("maid_cracker_site"));
		POI_MAID_ARCHER_SITE = TagKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, UMULittleMaid.identifier("maid_archer_site"));
		POI_MAID_GUARD_SITE = TagKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, UMULittleMaid.identifier("maid_guard_site"));
		POI_MAID_FARMER_SITE = TagKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, UMULittleMaid.identifier("maid_farmer_site"));
		POI_MAID_HEALER_SITE = TagKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, UMULittleMaid.identifier("maid_healer_site"));
		POI_MAID_POSEIDON_SITE = TagKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, UMULittleMaid.identifier("maid_poseidon_site"));
		POI_MAID_HUNTER_SITE = TagKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, UMULittleMaid.identifier("maid_hunter_site"));
		POI_MAID_SHEPHERD_SITE = TagKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, UMULittleMaid.identifier("maid_shepherd_site"));

		PERSONALITY_PERSISTENCES = TagKey.of(ModRegistries.MAID_PERSONALITY.getKey(), UMULittleMaid.identifier("persistences"));
		PERSONALITY_FLUTTER_WHEN_KILLS = TagKey.of(ModRegistries.MAID_PERSONALITY.getKey(), UMULittleMaid.identifier("flutter_when_kills"));
		PERSONALITY_FLUTTER_WHEN_CRAFTS = TagKey.of(ModRegistries.MAID_PERSONALITY.getKey(), UMULittleMaid.identifier("flutter_when_crafts"));
		PERSONALITY_FLUTTER_WHEN_KINDS = TagKey.of(ModRegistries.MAID_PERSONALITY.getKey(), UMULittleMaid.identifier("flutter_when_kinds"));
		PERSONALITY_FLUTTER_WHEN_WAKE_UPS = TagKey.of(ModRegistries.MAID_PERSONALITY.getKey(), UMULittleMaid.identifier("flutter_when_wake_ups"));
		PERSONALITY_DEVOTE_WHEN_ATTACK_OWNERS_ENEMIES = TagKey.of(ModRegistries.MAID_PERSONALITY.getKey(), UMULittleMaid.identifier("devote_when_attack_owners_enemies"));
		PERSONALITY_DEVOTE_WHEN_BOW_ATTACKS = TagKey.of(ModRegistries.MAID_PERSONALITY.getKey(), UMULittleMaid.identifier("devote_when_bow_attacks"));
		PERSONALITY_DEVOTE_WHEN_HEAL_OWNERS = TagKey.of(ModRegistries.MAID_PERSONALITY.getKey(), UMULittleMaid.identifier("devote_when_heal_owners"));
	}
}
