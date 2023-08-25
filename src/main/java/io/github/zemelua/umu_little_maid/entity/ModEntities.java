package io.github.zemelua.umu_little_maid.entity;

import com.chocohead.mm.api.ClassTinkerers;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.MaidMode;
import io.github.zemelua.umu_little_maid.entity.maid.MaidPersonality;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import io.github.zemelua.umu_little_maid.sound.ModSounds;
import io.github.zemelua.umu_little_maid.tinker.EarlyRiser;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public final class ModEntities {
	public static final Marker MARKER = MarkerManager.getMarker("ENTITY").addParents(UMULittleMaid.MARKER);

	public static final TrackedDataHandler<MaidMode> MODE_HANDLER = TrackedDataHandler.ofEnum(MaidMode.class);
	public static final TrackedDataHandler<MaidPersonality> PERSONALITY_HANDLER = TrackedDataHandler.of(ModRegistries.MAID_PERSONALITY);

	public static final EntityPose POSE_EATING;
	public static final EntityPose POSE_USING_DRIPLEAF;
	public static final EntityPose POSE_CHANGING_COSTUME;
	public static final EntityPose POSE_HEALING;

	public static final MaidPersonality PERSONALITY_BRAVERY;
	public static final MaidPersonality PERSONALITY_DILIGENT;
	public static final MaidPersonality PERSONALITY_AUDACIOUS;
	public static final MaidPersonality PERSONALITY_GENTLE;
	public static final MaidPersonality PERSONALITY_SHY;
	public static final MaidPersonality PERSONALITY_LAZY;
	public static final MaidPersonality PERSONALITY_TSUNDERE;

	public static final EntityType<LittleMaidEntity> LITTLE_MAID;
	public static final EntityType<ModFishingBobberEntity> SIMPLE_FISHING_BOBBER;

	@SuppressWarnings({"ConstantValue", "DataFlowIssue"})
	public static void initialize() {
		Registry.register(Registries.ENTITY_TYPE, UMULittleMaid.identifier("little_maid"), LITTLE_MAID);
		Registry.register(Registries.ENTITY_TYPE, UMULittleMaid.identifier("fish_bobber"), SIMPLE_FISHING_BOBBER);

		TrackedDataHandlerRegistry.register(MODE_HANDLER);
		TrackedDataHandlerRegistry.register(ModEntities.PERSONALITY_HANDLER);

		// FabricEntityTypeBuilder#defaultAttributesが動作しないためここで登録します。修正され次第移行します。
		FabricDefaultAttributeRegistry.register(LITTLE_MAID, LittleMaidEntity.createAttributes());

		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("bravery"), ModEntities.PERSONALITY_BRAVERY);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("diligent"), ModEntities.PERSONALITY_DILIGENT);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("audacious"), ModEntities.PERSONALITY_AUDACIOUS);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("gentle"), ModEntities.PERSONALITY_GENTLE);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("shy"), ModEntities.PERSONALITY_SHY);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("lazy"), ModEntities.PERSONALITY_LAZY);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("tsundere"), ModEntities.PERSONALITY_TSUNDERE);

		BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
				BiomeKeys.PLAINS, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.SNOWY_PLAINS, BiomeKeys.ICE_SPIKES, BiomeKeys.MEADOW,
				BiomeKeys.DESERT,
				BiomeKeys.SWAMP,
				BiomeKeys.FOREST, BiomeKeys.FLOWER_FOREST, BiomeKeys.BIRCH_FOREST, BiomeKeys.DARK_FOREST, BiomeKeys.OLD_GROWTH_BIRCH_FOREST,
				BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, BiomeKeys.TAIGA, BiomeKeys.SNOWY_TAIGA,
				BiomeKeys.SAVANNA, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.JUNGLE, BiomeKeys.SPARSE_JUNGLE, BiomeKeys.BAMBOO_JUNGLE,
				BiomeKeys.GROVE, BiomeKeys.BADLANDS, BiomeKeys.ERODED_BADLANDS, BiomeKeys.WOODED_BADLANDS,
				BiomeKeys.LUSH_CAVES
		), SpawnGroup.CREATURE, LITTLE_MAID, 8, 3, 3);

		UMULittleMaid.LOGGER.info(ModEntities.MARKER, "Entities are initialized!");
	}

	private ModEntities() {}

	static {
		LITTLE_MAID = FabricEntityTypeBuilder.createMob()
				.spawnGroup(SpawnGroup.CREATURE)
				.entityFactory(LittleMaidEntity::new)
				.dimensions(EntityDimensions.fixed(0.6F, 1.5F))
				.trackRangeChunks(10)
				.spawnRestriction(SpawnRestriction.Location.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, LittleMaidEntity::canSpawn)
				.build();
		SIMPLE_FISHING_BOBBER = FabricEntityTypeBuilder.<ModFishingBobberEntity>create()
				.spawnGroup(SpawnGroup.MISC)
				.entityFactory(ModFishingBobberEntity::new)
				.disableSaving()
				// .disableSummon()
				.dimensions(EntityDimensions.fixed(0.25F, 0.25F))
				.trackRangeChunks(4)
				.trackedUpdateRate(5)
				.build();


		POSE_EATING = ClassTinkerers.getEnum(EntityPose.class, EarlyRiser.ENTITY_POSE_EATING);
		POSE_USING_DRIPLEAF = ClassTinkerers.getEnum(EntityPose.class, EarlyRiser.ENTITY_POSE_USING_DRIPLEAF);
		POSE_CHANGING_COSTUME = ClassTinkerers.getEnum(EntityPose.class, EarlyRiser.ENTITY_POSE_CHANGING_COSTUME);
		POSE_HEALING = ClassTinkerers.getEnum(EntityPose.class, EarlyRiser.ENTITY_POSE_HEALING);

		PERSONALITY_BRAVERY = new MaidPersonality.Builder().setMaxHealth(18.0D).setAttackDamage(1.3D).setAttackKnockback(0.7D)
				.setHostiles(living -> living.getType().isIn(ModTags.ENTITY_MAID_BRAVERY_HOSTILES))
				.build(living -> living.getType().isIn(ModTags.ENTITY_MAID_BRAVERY_CHASES),
						ModSounds.ENTITY_MAID_BRAVERY_AMBIENT,
						ModSounds.ENTITY_MAID_BRAVERY_FENCER_ATTACK,
						ModSounds.ENTITY_MAID_BRAVERY_CRACKER_ATTACK,
						ModSounds.ENTITY_MAID_BRAVERY_ARCHER_ATTACK,
						ModSounds.ENTITY_MAID_BRAVERY_KILLED,
						ModSounds.ENTITY_MAID_BRAVERY_KILLED_BARELY,
						ModSounds.ENTITY_MAID_BRAVERY_HURT,
						ModSounds.ENTITY_MAID_BRAVERY_DEATH,
						ModSounds.ENTITY_MAID_BRAVERY_EAT,
						ModSounds.ENTITY_MAID_BRAVERY_CONTRACT,
						ModSounds.ENTITY_MAID_BRAVERY_SIT,
						ModSounds.ENTITY_MAID_BRAVERY_ENGAGE);
		PERSONALITY_DILIGENT = new MaidPersonality.Builder().setArmorToughness(1.0D).setLuck(2.0D)
				.setHostiles(living -> living.getType().isIn(ModTags.ENTITY_MAID_DILIGENT_HOSTILES))
				.build(living -> living.getType().isIn(ModTags.ENTITY_MAID_DILIGENT_CHASES),
						ModSounds.ENTITY_MAID_DILIGENT_AMBIENT,
						ModSounds.ENTITY_MAID_DILIGENT_FENCER_ATTACK,
						ModSounds.ENTITY_MAID_DILIGENT_CRACKER_ATTACK,
						ModSounds.ENTITY_MAID_DILIGENT_ARCHER_ATTACK,
						ModSounds.ENTITY_MAID_DILIGENT_KILLED,
						ModSounds.ENTITY_MAID_DILIGENT_KILLED_BARELY,
						ModSounds.ENTITY_MAID_DILIGENT_HURT,
						ModSounds.ENTITY_MAID_DILIGENT_DEATH,
						ModSounds.ENTITY_MAID_DILIGENT_EAT,
						ModSounds.ENTITY_MAID_DILIGENT_CONTRACT,
						ModSounds.ENTITY_MAID_DILIGENT_SIT,
						ModSounds.ENTITY_MAID_DILIGENT_ENGAGE);
		PERSONALITY_AUDACIOUS = new MaidPersonality.Builder().setMovementSpeed(0.24D).setArmor(2.0D).setKnockbackResistance(0.5D)
				.setHostiles(living -> living.getType().isIn(ModTags.ENTITY_MAID_AUDACIOUS_HOSTILES))
				.build(living -> living.getType().isIn(ModTags.ENTITY_MAID_AUDACIOUS_CHASES),
						ModSounds.ENTITY_MAID_AUDACIOUS_AMBIENT,
						ModSounds.ENTITY_MAID_AUDACIOUS_FENCER_ATTACK,
						ModSounds.ENTITY_MAID_AUDACIOUS_CRACKER_ATTACK,
						ModSounds.ENTITY_MAID_AUDACIOUS_ARCHER_ATTACK,
						ModSounds.ENTITY_MAID_AUDACIOUS_KILLED,
						ModSounds.ENTITY_MAID_AUDACIOUS_KILLED_BARELY,
						ModSounds.ENTITY_MAID_AUDACIOUS_HURT,
						ModSounds.ENTITY_MAID_AUDACIOUS_DEATH,
						ModSounds.ENTITY_MAID_AUDACIOUS_EAT,
						ModSounds.ENTITY_MAID_AUDACIOUS_CONTRACT,
						ModSounds.ENTITY_MAID_AUDACIOUS_SIT,
						ModSounds.ENTITY_MAID_AUDACIOUS_ENGAGE);
		PERSONALITY_GENTLE = new MaidPersonality.Builder().setMaxHealth(26.0D).setLuck(1.5D)
				.setHostiles(living -> living.getType().isIn(ModTags.ENTITY_MAID_GENTLE_HOSTILES))
				.build(living -> living.getType().isIn(ModTags.ENTITY_MAID_GENTLE_CHASES),
						ModSounds.ENTITY_MAID_GENTLE_AMBIENT,
						ModSounds.ENTITY_MAID_GENTLE_FENCER_ATTACK,
						ModSounds.ENTITY_MAID_GENTLE_CRACKER_ATTACK,
						ModSounds.ENTITY_MAID_GENTLE_ARCHER_ATTACK,
						ModSounds.ENTITY_MAID_GENTLE_KILLED,
						ModSounds.ENTITY_MAID_GENTLE_KILLED_BARELY,
						ModSounds.ENTITY_MAID_GENTLE_HURT,
						ModSounds.ENTITY_MAID_GENTLE_DEATH,
						ModSounds.ENTITY_MAID_GENTLE_EAT,
						ModSounds.ENTITY_MAID_GENTLE_CONTRACT,
						ModSounds.ENTITY_MAID_GENTLE_SIT,
						ModSounds.ENTITY_MAID_GENTLE_ENGAGE);
		PERSONALITY_SHY = new MaidPersonality.Builder().setMaxHealth(24.0D).setMovementSpeed(0.42D).setKnockbackResistance(-0.4D)
				.setHostiles(living -> living.getType().isIn(ModTags.ENTITY_MAID_SHY_HOSTILES))
				.build(living -> living.getType().isIn(ModTags.ENTITY_MAID_SHY_CHASES),
						ModSounds.ENTITY_MAID_SHY_AMBIENT,
						ModSounds.ENTITY_MAID_SHY_FENCER_ATTACK,
						ModSounds.ENTITY_MAID_SHY_CRACKER_ATTACK,
						ModSounds.ENTITY_MAID_SHY_ARCHER_ATTACK,
						ModSounds.ENTITY_MAID_SHY_KILLED,
						ModSounds.ENTITY_MAID_SHY_KILLED_BARELY,
						ModSounds.ENTITY_MAID_SHY_HURT,
						ModSounds.ENTITY_MAID_SHY_DEATH,
						ModSounds.ENTITY_MAID_SHY_EAT,
						ModSounds.ENTITY_MAID_SHY_CONTRACT,
						ModSounds.ENTITY_MAID_SHY_SIT,
						ModSounds.ENTITY_MAID_SHY_ENGAGE);
		PERSONALITY_LAZY = new MaidPersonality.Builder().setMovementSpeed(0.25D).setAttackDamage(0.8D).setLuck(-0.8D)
				.setHostiles(living -> living.getType().isIn(ModTags.ENTITY_MAID_LAZY_HOSTILES))
				.build(living -> living.getType().isIn(ModTags.ENTITY_MAID_LAZY_CHASES),
						ModSounds.ENTITY_MAID_LAZY_AMBIENT,
						ModSounds.ENTITY_MAID_LAZY_FENCER_ATTACK,
						ModSounds.ENTITY_MAID_LAZY_CRACKER_ATTACK,
						ModSounds.ENTITY_MAID_LAZY_ARCHER_ATTACK,
						ModSounds.ENTITY_MAID_LAZY_KILLED,
						ModSounds.ENTITY_MAID_LAZY_KILLED_BARELY,
						ModSounds.ENTITY_MAID_LAZY_HURT,
						ModSounds.ENTITY_MAID_LAZY_DEATH,
						ModSounds.ENTITY_MAID_LAZY_EAT,
						ModSounds.ENTITY_MAID_LAZY_CONTRACT,
						ModSounds.ENTITY_MAID_LAZY_SIT,
						ModSounds.ENTITY_MAID_LAZY_ENGAGE);
		PERSONALITY_TSUNDERE = new MaidPersonality.Builder().setAttackDamage(1.2D).setMovementSpeed(0.35D)
				.setHostiles(living -> living.getType().isIn(ModTags.ENTITY_MAID_TSUNDERE_HOSTILES))
				.build(living -> living.getType().isIn(ModTags.ENTITY_MAID_TSUNDERE_CHASES),
						ModSounds.ENTITY_MAID_TSUNDERE_AMBIENT,
						ModSounds.ENTITY_MAID_TSUNDERE_FENCER_ATTACK,
						ModSounds.ENTITY_MAID_TSUNDERE_CRACKER_ATTACK,
						ModSounds.ENTITY_MAID_TSUNDERE_ARCHER_ATTACK,
						ModSounds.ENTITY_MAID_TSUNDERE_KILLED,
						ModSounds.ENTITY_MAID_TSUNDERE_KILLED_BARELY,
						ModSounds.ENTITY_MAID_TSUNDERE_HURT,
						ModSounds.ENTITY_MAID_TSUNDERE_DEATH,
						ModSounds.ENTITY_MAID_TSUNDERE_EAT,
						ModSounds.ENTITY_MAID_TSUNDERE_CONTRACT,
						ModSounds.ENTITY_MAID_TSUNDERE_SIT,
						ModSounds.ENTITY_MAID_TSUNDERE_ENGAGE);
	}
}
