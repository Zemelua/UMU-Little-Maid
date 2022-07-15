package io.github.zemelua.umu_little_maid.entity;

import com.mojang.serialization.Codec;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.brain.sensor.MaidAttackableSensor;
import io.github.zemelua.umu_little_maid.entity.brain.sensor.MaidAttractableLivingsSensor;
import io.github.zemelua.umu_little_maid.entity.brain.sensor.MaidGuardableLivingSensor;
import io.github.zemelua.umu_little_maid.entity.brain.sensor.MaidShouldEatSensor;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.MaidJob;
import io.github.zemelua.umu_little_maid.entity.maid.MaidPersonality;
import io.github.zemelua.umu_little_maid.entity.maid.MaidPose;
import io.github.zemelua.umu_little_maid.mixin.SpawnRestrictionAccessor;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import io.github.zemelua.umu_little_maid.sound.ModSounds;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Unit;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.BiomeKeys;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class ModEntities {
	public static final Marker MARKER = MarkerManager.getMarker("ENTITY").addParents(UMULittleMaid.MARKER);

	public static final EntityType<LittleMaidEntity> LITTLE_MAID;

	public static final TrackedDataHandler<MaidPersonality> PERSONALITY_HANDLER;
	public static final TrackedDataHandler<MaidJob> JOB_HANDLER;
	public static final TrackedDataHandler<MaidPose> DATA_MAID_POSE;

	public static final MemoryModuleType<UUID> MEMORY_OWNER;
	public static final MemoryModuleType<Unit> MEMORY_IS_SITTING;
	public static final MemoryModuleType<List<LivingEntity>> MEMORY_ATTRACTABLE_LIVINGS;
	public static final MemoryModuleType<LivingEntity> MEMORY_GUARDABLE_LIVING;
	public static final MemoryModuleType<List<LivingEntity>> MEMORY_ATTRACT_TARGETS;
	public static final MemoryModuleType<LivingEntity> MEMORY_GUARD_TARGET;
	public static final MemoryModuleType<Unit> MEMORY_SHOULD_EAT;

	public static final SensorType<MaidAttackableSensor> SENSOR_MAID_ATTACKABLE;
	public static final SensorType<MaidAttractableLivingsSensor> SENSOR_MAID_ATTRACTABLE_LIVINGS;
	public static final SensorType<MaidGuardableLivingSensor> SENSOR_MAID_GUARDABLE_LIVING;
	public static final SensorType<MaidShouldEatSensor> SENSOR_SHOULD_EAT;

	public static final Activity ACTIVITY_SIT;
	public static final Activity ACTIVITY_GUARD;
	public static final Activity ACTIVITY_EAT;

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

	private ModEntities() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	private static boolean initialized = false;

	@SuppressWarnings("ConstantConditions")
	public static void initialize() {
		if (ModEntities.initialized) throw new IllegalStateException("Entities are already initialized!");

		Registry.register(Registry.ENTITY_TYPE, UMULittleMaid.identifier("little_maid"), ModEntities.LITTLE_MAID);

		TrackedDataHandlerRegistry.register(ModEntities.PERSONALITY_HANDLER);
		TrackedDataHandlerRegistry.register(ModEntities.JOB_HANDLER);
		TrackedDataHandlerRegistry.register(ModEntities.DATA_MAID_POSE);

		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("owner"), ModEntities.MEMORY_OWNER);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("is_sitting"), ModEntities.MEMORY_IS_SITTING);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("attractable_livings"), ModEntities.MEMORY_ATTRACTABLE_LIVINGS);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("guardable_living"), ModEntities.MEMORY_GUARDABLE_LIVING);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("attract_targets"), ModEntities.MEMORY_ATTRACT_TARGETS);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("guard_target"), ModEntities.MEMORY_GUARD_TARGET);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("should_eat"), ModEntities.MEMORY_SHOULD_EAT);

		Registry.register(Registry.SENSOR_TYPE, UMULittleMaid.identifier("maid_attackable"), ModEntities.SENSOR_MAID_ATTACKABLE);
		Registry.register(Registry.SENSOR_TYPE, UMULittleMaid.identifier("maid_attractable_livings"), ModEntities.SENSOR_MAID_ATTRACTABLE_LIVINGS);
		Registry.register(Registry.SENSOR_TYPE, UMULittleMaid.identifier("maid_guardable_living"), ModEntities.SENSOR_MAID_GUARDABLE_LIVING);
		Registry.register(Registry.SENSOR_TYPE, UMULittleMaid.identifier("should_eat"), ModEntities.SENSOR_SHOULD_EAT);

		Registry.register(Registry.ACTIVITY, UMULittleMaid.identifier("sit"), ModEntities.ACTIVITY_SIT);
		Registry.register(Registry.ACTIVITY, UMULittleMaid.identifier("guard"), ModEntities.ACTIVITY_GUARD);
		Registry.register(Registry.ACTIVITY, UMULittleMaid.identifier("eat"), ModEntities.ACTIVITY_EAT);

		FabricDefaultAttributeRegistry.register(ModEntities.LITTLE_MAID, LittleMaidEntity.createAttributes());

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

	static {
		LITTLE_MAID = FabricEntityTypeBuilder
				.create(SpawnGroup.CREATURE, LittleMaidEntity::new)
				.dimensions(EntityDimensions.fixed(0.6F, 1.5F))
				.build();

		PERSONALITY_HANDLER = TrackedDataHandler.of(ModRegistries.MAID_PERSONALITY);
		JOB_HANDLER = TrackedDataHandler.of(ModRegistries.MAID_JOB);
		DATA_MAID_POSE = TrackedDataHandler.ofEnum(MaidPose.class);

		MEMORY_OWNER = new MemoryModuleType<>(Optional.of(DynamicSerializableUuid.CODEC));
		MEMORY_IS_SITTING = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
		MEMORY_ATTRACTABLE_LIVINGS = new MemoryModuleType<>(Optional.empty());
		MEMORY_GUARDABLE_LIVING = new MemoryModuleType<>(Optional.empty());
		MEMORY_ATTRACT_TARGETS = new MemoryModuleType<>(Optional.empty());
		MEMORY_GUARD_TARGET = new MemoryModuleType<>(Optional.empty());
		MEMORY_SHOULD_EAT = new MemoryModuleType<>(Optional.empty());

		SENSOR_MAID_ATTACKABLE = new SensorType<>(MaidAttackableSensor::new);
		SENSOR_MAID_ATTRACTABLE_LIVINGS = new SensorType<>(MaidAttractableLivingsSensor::new);
		SENSOR_MAID_GUARDABLE_LIVING = new SensorType<>(MaidGuardableLivingSensor::new);
		SENSOR_SHOULD_EAT = new SensorType<>(MaidShouldEatSensor::new);

		ACTIVITY_SIT = new Activity("sit");
		ACTIVITY_GUARD = new Activity("guard");
		ACTIVITY_EAT = new Activity("eat");

		PERSONALITY_BRAVERY = new MaidPersonality.Builder().setMaxHealth(18.0D).setAttackDamage(1.3D).setAttackKnockback(0.7D)
				.setContractSound(ModSounds.ENTITY_MAID_BRAVERY_CONTRACT)
				.setFencerAttackSound(ModSounds.ENTITY_MAID_BRAVERY_FENCER_ATTACK)
				.setHurtSound(ModSounds.ENTITY_MAID_BRAVERY_HURT)
				.build();
		PERSONALITY_DILIGENT = new MaidPersonality.Builder().setArmorToughness(1.0D).setLuck(2.0D)
				.setContractSound(ModSounds.ENTITY_MAID_DILIGENT_CONTRACT)
				.build();
		PERSONALITY_AUDACIOUS = new MaidPersonality.Builder().setMovementSpeed(0.24D).setArmor(2.0D).setKnockbackResistance(0.5D)
				.setDeathSound(ModSounds.ENTITY_MAID_AUDACIOUS_DEATH)
				.setContractSound(ModSounds.ENTITY_MAID_AUDACIOUS_CONTRACT)
				.build();
		PERSONALITY_GENTLE = new MaidPersonality.Builder().setMaxHealth(26.0D).setLuck(1.5D)
				.setContractSound(ModSounds.ENTITY_MAID_GENTLE_CONTRACT)
				.build();
		PERSONALITY_SHY = new MaidPersonality.Builder().setMaxHealth(24.0D).setMovementSpeed(0.42D).setKnockbackResistance(-0.4D)
				.setContractSound(ModSounds.ENTITY_MAID_SHY_CONTRACT)
				.build();
		PERSONALITY_LAZY = new MaidPersonality.Builder().setMovementSpeed(0.25D).setAttackDamage(0.8D).setLuck(-0.8D)
				.setAmbientSound(ModSounds.ENTITY_MAID_LAZY_AMBIENT)
				.setDeathSound(ModSounds.ENTITY_MAID_LAZY_DEATH)
				.setContractSound(ModSounds.ENTITY_MAID_LAZY_CONTRACT)
				.build();
		PERSONALITY_TSUNDERE = new MaidPersonality.Builder().setAttackDamage(1.2D).setMovementSpeed(0.35D)
				.setContractSound(ModSounds.ENTITY_MAID_TSUNDERE_CONTRACT)
				.build();

		JOB_NONE = new MaidJob(itemStack -> false);
		JOB_FENCER = new MaidJob(itemStack -> itemStack.getItem() instanceof SwordItem);
		JOB_CRACKER = new MaidJob(itemStack -> itemStack.getItem() instanceof AxeItem);
		JOB_ARCHER = new MaidJob(itemStack -> itemStack.getItem() instanceof BowItem);
		JOB_GUARD = new MaidJob(itemStack -> itemStack.isOf(Items.SHIELD));
	}
}
