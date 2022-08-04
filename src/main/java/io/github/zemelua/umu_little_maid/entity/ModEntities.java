package io.github.zemelua.umu_little_maid.entity;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.serialization.Codec;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.brain.*;
import io.github.zemelua.umu_little_maid.entity.brain.sensor.*;
import io.github.zemelua.umu_little_maid.entity.maid.*;
import io.github.zemelua.umu_little_maid.mixin.SpawnRestrictionAccessor;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import io.github.zemelua.umu_little_maid.sound.ModSounds;
import io.github.zemelua.umu_little_maid.tag.ModTags;
import io.github.zemelua.umu_little_maid.util.EarlyRiser;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ModEntities {
	public static final Marker MARKER = MarkerManager.getMarker("ENTITY").addParents(UMULittleMaid.MARKER);

	public static final EntityType<LittleMaidEntity> LITTLE_MAID;

	public static final TrackedDataHandler<MaidMode> MODE_HANDLER;
	public static final TrackedDataHandler<MaidPersonality> PERSONALITY_HANDLER;
	public static final TrackedDataHandler<MaidJob> JOB_HANDLER;

	public static final MemoryModuleType<UUID> MEMORY_OWNER;
	public static final MemoryModuleType<Unit> MEMORY_IS_SITTING;
	public static final MemoryModuleType<Unit> MEMORY_HAS_ARROWS;
	public static final MemoryModuleType<List<LivingEntity>> MEMORY_ATTRACTABLE_LIVINGS;
	public static final MemoryModuleType<LivingEntity> MEMORY_GUARDABLE_LIVING;
	public static final MemoryModuleType<LivingEntity> MEMORY_GUARD_TARGET;
	public static final MemoryModuleType<BlockPos> MEMORY_FARMABLE_POS;
	public static final MemoryModuleType<BlockPos> MEMORY_FARM_POS;
	public static final MemoryModuleType<Unit> MEMORY_FARM_COOLDOWN;
	public static final MemoryModuleType<GlobalPos> MEMORY_JOB_SITE;
	public static final MemoryModuleType<GlobalPos> MEMORY_JOB_SITE_CANDIDATE;
	public static final MemoryModuleType<Unit> MEMORY_SHOULD_HEAL;
	public static final MemoryModuleType<Unit> MEMORY_SHOULD_EAT;
	public static final MemoryModuleType<Unit> MEMORY_SHOULD_SLEEP;
	public static final MemoryModuleType<TridentEntity> MEMORY_THROWN_TRIDENT;
	public static final MemoryModuleType<Unit> MEMORY_THROWN_TRIDENT_COOLDOWN;
	public static final MemoryModuleType<Unit> MEMORY_SHOULD_BREATH;

	public static final SensorType<MaidAttackableSensor> SENSOR_MAID_ATTACKABLE;
	public static final SensorType<MaidAttractableLivingsSensor> SENSOR_MAID_ATTRACTABLE_LIVINGS;
	public static final SensorType<MaidGuardableLivingSensor> SENSOR_MAID_GUARDABLE_LIVING;
	public static final SensorType<MaidFarmablePosesSensor> SENSOR_MAID_FARMABLE_POSES;
	public static final SensorType<JobSiteCandidateSensor> SENSOR_JOB_SITE_CANDIDATE;
	public static final SensorType<MaidShouldEatSensor> SENSOR_SHOULD_EAT;
	public static final SensorType<HomeCandidateSensor> SENSOR_HOME_CANDIDATE;

	public static final Activity ACTIVITY_SIT;
	public static final Activity ACTIVITY_GUARD;
	public static final Activity ACTIVITY_EAT;
	public static final Activity ACTIVITY_FARM;
	public static final Activity ACTIVITY_HEAL;
	public static final Activity ACTIVITY_GO_GET_TRIDENT;
	public static final Activity ACTIVITY_BREATH;

	public static final EntityPose POSE_EATING;
	public static final EntityPose POSE_USING_DRIPLEAF;
	public static final EntityPose POSE_CHANGING_COSTUME;
	public static final EntityPose POSE_HEALING;

	public static final RegistryKey<PointOfInterestType> POI_TARGET;
	public static final RegistryKey<PointOfInterestType> POI_SCARECROW;
	public static final RegistryKey<PointOfInterestType> POI_AMETHYST_BLOCK;
	public static final RegistryKey<PointOfInterestType> POI_CONDUIT;

	public static final MaidPersonality PERSONALITY_BRAVERY;
	public static final MaidPersonality PERSONALITY_DILIGENT;
	public static final MaidPersonality PERSONALITY_AUDACIOUS;
	public static final MaidPersonality PERSONALITY_GENTLE;
	public static final MaidPersonality PERSONALITY_SHY;
	public static final MaidPersonality PERSONALITY_LAZY;
	public static final MaidPersonality PERSONALITY_TSUNDERE;

	public static final MaidJob JOB_NONE;
	public static final MaidJob JOB_FENCER;
	public static final MaidJob JOB_CRACKER;
	public static final MaidJob JOB_ARCHER;
	public static final MaidJob JOB_GUARD;
	public static final MaidJob JOB_FARMER;
	public static final MaidJob JOB_HEALER;
	public static final MaidJob JOB_POSEIDON;

	private static final Block[] WOODEN_FENCES = new Block[]{
			Blocks.OAK_FENCE, Blocks.ACACIA_FENCE, Blocks.DARK_OAK_FENCE, Blocks.SPRUCE_FENCE, Blocks.BIRCH_FENCE,
			Blocks.JUNGLE_FENCE, Blocks.CRIMSON_FENCE, Blocks.WARPED_FENCE, Blocks.MANGROVE_FENCE
	};

	private static boolean initialized = false;
	@SuppressWarnings("ConstantConditions")
	public static void initialize() {
		if (ModEntities.initialized) throw new IllegalStateException("Entities are already initialized!");

		Registry.register(Registry.ENTITY_TYPE, UMULittleMaid.identifier("little_maid"), ModEntities.LITTLE_MAID);

		TrackedDataHandlerRegistry.register(ModEntities.MODE_HANDLER);
		TrackedDataHandlerRegistry.register(ModEntities.PERSONALITY_HANDLER);
		TrackedDataHandlerRegistry.register(ModEntities.JOB_HANDLER);

		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("owner"), ModEntities.MEMORY_OWNER);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("is_sitting"), ModEntities.MEMORY_IS_SITTING);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("has_arrows"), ModEntities.MEMORY_HAS_ARROWS);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("attractable_livings"), ModEntities.MEMORY_ATTRACTABLE_LIVINGS);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("guardable_living"), ModEntities.MEMORY_GUARDABLE_LIVING);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("guard_target"), ModEntities.MEMORY_GUARD_TARGET);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("farmable_poses"), ModEntities.MEMORY_FARMABLE_POS);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("farm_pos"), ModEntities.MEMORY_FARM_POS);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("farm_cooldown"), ModEntities.MEMORY_FARM_COOLDOWN);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("job_site"), ModEntities.MEMORY_JOB_SITE);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("job_site_candidate"), ModEntities.MEMORY_JOB_SITE_CANDIDATE);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("should_heal"), ModEntities.MEMORY_SHOULD_HEAL);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("should_eat"), ModEntities.MEMORY_SHOULD_EAT);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("should_sleep"), ModEntities.MEMORY_SHOULD_SLEEP);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("thrown_trident"), ModEntities.MEMORY_THROWN_TRIDENT);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("thrown_trident_cooldown"), ModEntities.MEMORY_THROWN_TRIDENT_COOLDOWN);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("should_breath"), ModEntities.MEMORY_SHOULD_BREATH);

		Registry.register(Registry.SENSOR_TYPE, UMULittleMaid.identifier("maid_attackable"), ModEntities.SENSOR_MAID_ATTACKABLE);
		Registry.register(Registry.SENSOR_TYPE, UMULittleMaid.identifier("maid_attractable_livings"), ModEntities.SENSOR_MAID_ATTRACTABLE_LIVINGS);
		Registry.register(Registry.SENSOR_TYPE, UMULittleMaid.identifier("maid_guardable_living"), ModEntities.SENSOR_MAID_GUARDABLE_LIVING);
		Registry.register(Registry.SENSOR_TYPE, UMULittleMaid.identifier("maid_farmable_poses"), ModEntities.SENSOR_MAID_FARMABLE_POSES);
		Registry.register(Registry.SENSOR_TYPE, UMULittleMaid.identifier("farm_site_candidate"), ModEntities.SENSOR_JOB_SITE_CANDIDATE);
		Registry.register(Registry.SENSOR_TYPE, UMULittleMaid.identifier("should_eat"), ModEntities.SENSOR_SHOULD_EAT);
		Registry.register(Registry.SENSOR_TYPE, UMULittleMaid.identifier("home"), ModEntities.SENSOR_HOME_CANDIDATE);

		Registry.register(Registry.ACTIVITY, UMULittleMaid.identifier("sit"), ModEntities.ACTIVITY_SIT);
		Registry.register(Registry.ACTIVITY, UMULittleMaid.identifier("guard"), ModEntities.ACTIVITY_GUARD);
		Registry.register(Registry.ACTIVITY, UMULittleMaid.identifier("eat"), ModEntities.ACTIVITY_EAT);
		Registry.register(Registry.ACTIVITY, UMULittleMaid.identifier("farm"), ModEntities.ACTIVITY_FARM);
		Registry.register(Registry.ACTIVITY, UMULittleMaid.identifier("heal"), ModEntities.ACTIVITY_HEAL);
		Registry.register(Registry.ACTIVITY, UMULittleMaid.identifier("go_get_trident"), ModEntities.ACTIVITY_GO_GET_TRIDENT);
		Registry.register(Registry.ACTIVITY, UMULittleMaid.identifier("breath"), ModEntities.ACTIVITY_BREATH);

		FabricDefaultAttributeRegistry.register(ModEntities.LITTLE_MAID, LittleMaidEntity.createAttributes());

		PointOfInterestTypes.register(Registry.POINT_OF_INTEREST_TYPE, ModEntities.POI_TARGET,
				PointOfInterestTypes.getStatesOfBlock(Blocks.TARGET), 1, 1);
		PointOfInterestTypes.register(Registry.POINT_OF_INTEREST_TYPE, ModEntities.POI_SCARECROW,
				Arrays.stream(ModEntities.WOODEN_FENCES)
						.flatMap(block -> block.getStateManager().getStates().stream())
						.collect(Collectors.toSet()), 1, 1);
		PointOfInterestTypes.register(Registry.POINT_OF_INTEREST_TYPE, ModEntities.POI_AMETHYST_BLOCK,
				PointOfInterestTypes.getStatesOfBlock(Blocks.AMETHYST_BLOCK), 1, 1);
		PointOfInterestTypes.register(Registry.POINT_OF_INTEREST_TYPE, ModEntities.POI_CONDUIT,
				PointOfInterestTypes.getStatesOfBlock(Blocks.CONDUIT), 1, 1);

		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("bravery"), ModEntities.PERSONALITY_BRAVERY);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("diligent"), ModEntities.PERSONALITY_DILIGENT);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("audacious"), ModEntities.PERSONALITY_AUDACIOUS);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("gentle"), ModEntities.PERSONALITY_GENTLE);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("shy"), ModEntities.PERSONALITY_SHY);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("lazy"), ModEntities.PERSONALITY_LAZY);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("tsundere"), ModEntities.PERSONALITY_TSUNDERE);

		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("none"), ModEntities.JOB_NONE);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("fencer"), ModEntities.JOB_FENCER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("cracker"), ModEntities.JOB_CRACKER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("archer"), ModEntities.JOB_ARCHER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("guard"), ModEntities.JOB_GUARD);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("farmer"), ModEntities.JOB_FARMER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("heal"), ModEntities.JOB_HEALER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("poseidon"), ModEntities.JOB_POSEIDON);

		BiomeModifications.addSpawn(BiomeSelectors.includeByKey(
				BiomeKeys.PLAINS, BiomeKeys.SUNFLOWER_PLAINS, BiomeKeys.SNOWY_PLAINS, BiomeKeys.ICE_SPIKES, BiomeKeys.MEADOW,
				BiomeKeys.DESERT,
				BiomeKeys.SWAMP,
				BiomeKeys.FOREST, BiomeKeys.FLOWER_FOREST, BiomeKeys.BIRCH_FOREST, BiomeKeys.DARK_FOREST, BiomeKeys.OLD_GROWTH_BIRCH_FOREST,
				BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, BiomeKeys.TAIGA, BiomeKeys.SNOWY_TAIGA,
				BiomeKeys.SAVANNA, BiomeKeys.SAVANNA_PLATEAU, BiomeKeys.JUNGLE, BiomeKeys.SPARSE_JUNGLE, BiomeKeys.BAMBOO_JUNGLE,
				BiomeKeys.GROVE, BiomeKeys.BADLANDS, BiomeKeys.ERODED_BADLANDS, BiomeKeys.WOODED_BADLANDS,
				BiomeKeys.LUSH_CAVES
		), SpawnGroup.CREATURE, ModEntities.LITTLE_MAID, 100, 1, 1);

		SpawnRestrictionAccessor.callRegister(ModEntities.LITTLE_MAID, SpawnRestriction.Location.ON_GROUND,
				Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, LittleMaidEntity::canSpawn);

		ModEntities.initialized = true;
		UMULittleMaid.LOGGER.info(ModEntities.MARKER, "Entities are initialized!");
	}

	private ModEntities() throws IllegalAccessException {throw new IllegalAccessException();}

	static {
		LITTLE_MAID = FabricEntityTypeBuilder
				.create(SpawnGroup.CREATURE, LittleMaidEntity::new)
				.dimensions(EntityDimensions.fixed(0.6F, 1.5F))
				.build();

		MODE_HANDLER = TrackedDataHandler.ofEnum(MaidMode.class);
		PERSONALITY_HANDLER = TrackedDataHandler.of(ModRegistries.MAID_PERSONALITY);
		JOB_HANDLER = TrackedDataHandler.of(ModRegistries.MAID_JOB);

		MEMORY_OWNER = new MemoryModuleType<>(Optional.of(DynamicSerializableUuid.CODEC));
		MEMORY_IS_SITTING = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
		MEMORY_HAS_ARROWS = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
		MEMORY_ATTRACTABLE_LIVINGS = new MemoryModuleType<>(Optional.empty());
		MEMORY_GUARDABLE_LIVING = new MemoryModuleType<>(Optional.empty());
		MEMORY_GUARD_TARGET = new MemoryModuleType<>(Optional.empty());
		MEMORY_FARMABLE_POS = new MemoryModuleType<>(Optional.empty());
		MEMORY_FARM_POS = new MemoryModuleType<>(Optional.empty());
		MEMORY_FARM_COOLDOWN = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
		MEMORY_JOB_SITE = new MemoryModuleType<>(Optional.of(GlobalPos.CODEC));
		MEMORY_JOB_SITE_CANDIDATE = new MemoryModuleType<>(Optional.of(GlobalPos.CODEC));
		MEMORY_SHOULD_HEAL = new MemoryModuleType<>(Optional.empty());
		MEMORY_SHOULD_EAT = new MemoryModuleType<>(Optional.empty());
		MEMORY_SHOULD_SLEEP = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
		MEMORY_THROWN_TRIDENT = new MemoryModuleType<>(Optional.empty());
		MEMORY_THROWN_TRIDENT_COOLDOWN = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
		MEMORY_SHOULD_BREATH = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));

		SENSOR_MAID_ATTACKABLE = new SensorType<>(MaidAttackableSensor::new);
		SENSOR_MAID_ATTRACTABLE_LIVINGS = new SensorType<>(MaidAttractableLivingsSensor::new);
		SENSOR_MAID_GUARDABLE_LIVING = new SensorType<>(MaidGuardableLivingSensor::new);
		SENSOR_MAID_FARMABLE_POSES = new SensorType<>(MaidFarmablePosesSensor::new);
		SENSOR_JOB_SITE_CANDIDATE = new SensorType<>(JobSiteCandidateSensor::new);
		SENSOR_SHOULD_EAT = new SensorType<>(MaidShouldEatSensor::new);
		SENSOR_HOME_CANDIDATE = new SensorType<>(HomeCandidateSensor::new);

		ACTIVITY_SIT = new Activity("sit");
		ACTIVITY_GUARD = new Activity("guard");
		ACTIVITY_EAT = new Activity("eat");
		ACTIVITY_FARM = new Activity("farm");
		ACTIVITY_HEAL = new Activity("heal");
		ACTIVITY_GO_GET_TRIDENT = new Activity("go_get_trident");
		ACTIVITY_BREATH = new Activity("breath");

		POSE_EATING = ClassTinkerers.getEnum(EntityPose.class, EarlyRiser.ENTITY_POSE_EATING);
		POSE_USING_DRIPLEAF = ClassTinkerers.getEnum(EntityPose.class, EarlyRiser.ENTITY_POSE_USING_DRIPLEAF);
		POSE_CHANGING_COSTUME = ClassTinkerers.getEnum(EntityPose.class, EarlyRiser.ENTITY_POSE_CHANGING_COSTUME);
		POSE_HEALING = ClassTinkerers.getEnum(EntityPose.class, EarlyRiser.ENTITY_POSE_HEALING);

		POI_TARGET = RegistryKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, UMULittleMaid.identifier("target"));
		POI_SCARECROW = RegistryKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, UMULittleMaid.identifier("scarecrow"));
		POI_AMETHYST_BLOCK = RegistryKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, UMULittleMaid.identifier("amethyst_block"));
		POI_CONDUIT = RegistryKey.of(Registry.POINT_OF_INTEREST_TYPE_KEY, UMULittleMaid.identifier("conduit"));

		PERSONALITY_BRAVERY = new MaidPersonality.Builder().setMaxHealth(18.0D).setAttackDamage(1.3D).setAttackKnockback(0.7D)
				.setHostiles(living -> living.getType().isIn(ModTags.ENTITY_MAID_BRAVERY_HOSTILES))
				.setContractSound(ModSounds.ENTITY_MAID_BRAVERY_CONTRACT)
				.setFencerAttackSound(ModSounds.ENTITY_MAID_BRAVERY_FENCER_ATTACK)
				.setHurtSound(ModSounds.ENTITY_MAID_BRAVERY_HURT)
				.build();
		PERSONALITY_DILIGENT = new MaidPersonality.Builder().setArmorToughness(1.0D).setLuck(2.0D)
				.setHostiles(living -> living.getType().isIn(ModTags.ENTITY_MAID_DILIGENT_HOSTILES))
				.setContractSound(ModSounds.ENTITY_MAID_DILIGENT_CONTRACT)
				.build();
		PERSONALITY_AUDACIOUS = new MaidPersonality.Builder().setMovementSpeed(0.24D).setArmor(2.0D).setKnockbackResistance(0.5D)
				.setHostiles(living -> living.getType().isIn(ModTags.ENTITY_MAID_AUDACIOUS_HOSTILES))
				.setDeathSound(ModSounds.ENTITY_MAID_AUDACIOUS_DEATH)
				.setContractSound(ModSounds.ENTITY_MAID_AUDACIOUS_CONTRACT)
				.build();
		PERSONALITY_GENTLE = new MaidPersonality.Builder().setMaxHealth(26.0D).setLuck(1.5D)
				.setHostiles(living -> living.getType().isIn(ModTags.ENTITY_MAID_GENTLE_HOSTILES))
				.setContractSound(ModSounds.ENTITY_MAID_GENTLE_CONTRACT)
				.build();
		PERSONALITY_SHY = new MaidPersonality.Builder().setMaxHealth(24.0D).setMovementSpeed(0.42D).setKnockbackResistance(-0.4D)
				.setHostiles(living -> living.getType().isIn(ModTags.ENTITY_MAID_SHY_HOSTILES))
				.setContractSound(ModSounds.ENTITY_MAID_SHY_CONTRACT)
				.build();
		PERSONALITY_LAZY = new MaidPersonality.Builder().setMovementSpeed(0.25D).setAttackDamage(0.8D).setLuck(-0.8D)
				.setHostiles(living -> living.getType().isIn(ModTags.ENTITY_MAID_LAZY_HOSTILES))
				.setAmbientSound(ModSounds.ENTITY_MAID_LAZY_AMBIENT)
				.setDeathSound(ModSounds.ENTITY_MAID_LAZY_DEATH)
				.setContractSound(ModSounds.ENTITY_MAID_LAZY_CONTRACT)
				.build();
		PERSONALITY_TSUNDERE = new MaidPersonality.Builder().setAttackDamage(1.2D).setMovementSpeed(0.35D)
				.setHostiles(living -> living.getType().isIn(ModTags.ENTITY_MAID_TSUNDERE_HOSTILES))
				.setContractSound(ModSounds.ENTITY_MAID_TSUNDERE_CONTRACT)
				.build();

		JOB_NONE = new MaidJob(itemStack -> false,
				MaidNoneBrainManager::initializeBrain,
				MaidNoneBrainManager::tickBrain,
				PointOfInterestType.NONE,
				LittleMaidEntity.TEXTURE_NONE);
		JOB_FENCER = new MaidJob(itemStack -> itemStack.isIn(ModTags.ITEM_MAID_FENCER_TOOLS),
				MaidFencerBrainManager::initializeBrain,
				MaidFencerBrainManager::tickBrain,
				poi -> poi.isIn(ModTags.POI_MAID_FENCER_SITE),
				LittleMaidEntity.TEXTURE_FENCER);
		JOB_CRACKER = new MaidJob(itemStack -> itemStack.isIn(ModTags.ITEM_MAID_CRACKER_TOOLS),
				MaidCrackerBrainManager::initializeBrain,
				MaidCrackerBrainManager::tickBrain,
				poi -> poi.isIn(ModTags.POI_MAID_CRACKER_SITE),
				LittleMaidEntity.TEXTURE_CRACKER);
		JOB_ARCHER = new MaidJob(itemStack -> itemStack.isIn(ModTags.ITEM_MAID_ARCHER_TOOLS),
				MaidArcherBrainManager::initializeBrain,
				MaidArcherBrainManager::tickBrain,
				poi -> poi.isIn(ModTags.POI_MAID_ARCHER_SITE),
				LittleMaidEntity.TEXTURE_ARCHER);
		JOB_GUARD = new MaidJob(itemStack -> itemStack.isIn(ModTags.ITEM_MAID_GUARD_TOOLS),
				MaidGuardBrainManager::initializeBrain,
				MaidGuardBrainManager::tickBrain,
				poi -> poi.isIn(ModTags.POI_MAID_GUARD_SITE),
				LittleMaidEntity.TEXTURE_GUARD);
		JOB_FARMER = new MaidJob(itemStack -> itemStack.isIn(ModTags.ITEM_MAID_FARMER_TOOLS),
				MaidFarmerBrainManager::initializeBrain,
				MaidFarmerBrainManager::tickBrain,
				poi -> poi.isIn(ModTags.POI_MAID_FARMER_SITE),
				LittleMaidEntity.TEXTURE_FARMER);
		JOB_HEALER = new MaidJob(itemStack -> itemStack.isIn(ModTags.ITEM_MAID_HEALER_TOOLS),
				MaidHealerBrainManager::initializeBrain,
				MaidHealerBrainManager::tickBrain,
				poi -> poi.isIn(ModTags.POI_MAID_HEALER_SITE),
				LittleMaidEntity.TEXTURE_HEALER);
		JOB_POSEIDON = new PoseidonJob(itemStack -> itemStack.isIn(ModTags.ITEM_MAID_POSEIDON_TOOLS),
				MaidPoseidonBrainManager::initializeBrain,
				MaidPoseidonBrainManager::tickBrain,
				poi -> poi.isIn(ModTags.POI_MAID_POSEIDON_SITE),
				LittleMaidEntity.TEXTURE_POSEIDON);
	}
}
