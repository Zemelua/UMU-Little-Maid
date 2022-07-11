package io.github.zemelua.umu_little_maid.entity;

import com.mojang.serialization.Codec;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.brain.sensor.*;
import io.github.zemelua.umu_little_maid.entity.maid.job.MaidJob;
import io.github.zemelua.umu_little_maid.entity.maid.personality.MaidPersonality;
import io.github.zemelua.umu_little_maid.entity.maid.personality.ShyPersonality;
import io.github.zemelua.umu_little_maid.mixin.ActivityAccessor;
import io.github.zemelua.umu_little_maid.mixin.SensorTypeAccessor;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
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

	public static final MaidPersonality BRAVERY;
	public static final MaidPersonality TSUNDERE;
	public static final MaidPersonality SHY;

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

		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("bravery"), ModEntities.BRAVERY);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("tsundere"), ModEntities.TSUNDERE);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("shy"), ModEntities.SHY);

		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("none"), ModEntities.JOB_NONE);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("fencer"), ModEntities.JOB_FENCER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("cracker"), ModEntities.JOB_CRACKER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("archer"), ModEntities.JOB_ARCHER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("guard"), ModEntities.JOB_GUARD);

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

		MEMORY_OWNER = new MemoryModuleType<>(Optional.of(DynamicSerializableUuid.CODEC));
		MEMORY_IS_SITTING = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
		MEMORY_ATTRACTABLE_LIVINGS = new MemoryModuleType<>(Optional.empty());
		MEMORY_GUARDABLE_LIVING = new MemoryModuleType<>(Optional.empty());
		MEMORY_ATTRACT_TARGETS = new MemoryModuleType<>(Optional.empty());
		MEMORY_GUARD_TARGET = new MemoryModuleType<>(Optional.empty());
		MEMORY_SHOULD_EAT = new MemoryModuleType<>(Optional.empty());

		SENSOR_MAID_ATTACKABLE = SensorTypeAccessor.constructor(MaidAttackableSensor::new);
		SENSOR_MAID_ATTRACTABLE_LIVINGS = SensorTypeAccessor.constructor(MaidAttractableLivingsSensor::new);
		SENSOR_MAID_GUARDABLE_LIVING = SensorTypeAccessor.constructor(MaidGuardableLivingSensor::new);
		SENSOR_SHOULD_EAT = SensorTypeAccessor.constructor(MaidShouldEatSensor::new);

		ACTIVITY_SIT = ActivityAccessor.constructor("sit");
		ACTIVITY_GUARD = ActivityAccessor.constructor("guard");
		ACTIVITY_EAT = ActivityAccessor.constructor("eat");

		BRAVERY = new MaidPersonality.Builder()
				.setPounce()
				.build();
		TSUNDERE = new MaidPersonality.Builder()
				.setCurt()
				.setMinFollowDistance(10.0D)
				.setMaxFollowDistance(5.0D)
				.build();
		SHY = new ShyPersonality(new MaidPersonality.Builder()
				.setMinFollowDistance(8.0D)
				.setMaxFollowDistance(5.0D));

		JOB_NONE = new MaidJob(itemStack -> false);
		JOB_FENCER = new MaidJob(itemStack -> itemStack.getItem() instanceof SwordItem);
		JOB_CRACKER = new MaidJob(itemStack -> itemStack.getItem() instanceof AxeItem);
		JOB_ARCHER = new MaidJob(itemStack -> itemStack.getItem() instanceof BowItem);
		JOB_GUARD = new MaidJob(itemStack -> itemStack.isOf(Items.SHIELD));
	}
}
