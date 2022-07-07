package io.github.zemelua.umu_little_maid.entity;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.job.MaidJob;
import io.github.zemelua.umu_little_maid.entity.maid.personality.MaidPersonality;
import io.github.zemelua.umu_little_maid.entity.maid.personality.ShyPersonality;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Optional;
import java.util.UUID;

public final class ModEntities {
	public static final Marker MARKER = MarkerManager.getMarker("ENTITY").addParents(UMULittleMaid.MARKER);

	public static final EntityType<LittleMaidEntity> LITTLE_MAID;

	public static final TrackedDataHandler<MaidPersonality> PERSONALITY_HANDLER;
	public static final TrackedDataHandler<MaidJob> JOB_HANDLER;

	public static final MemoryModuleType<UUID> OWNER;

	public static final MaidPersonality BRAVERY;
	public static final MaidPersonality TSUNDERE;
	public static final MaidPersonality SHY;

	public static final MaidJob NONE;
	public static final MaidJob FENCER;
	public static final MaidJob CRACKER;
	public static final MaidJob ARCHER;
	public static final MaidJob GUARD;

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

		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("owner"), ModEntities.OWNER);

		FabricDefaultAttributeRegistry.register(ModEntities.LITTLE_MAID, LittleMaidEntity.createAttributes());

		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("bravery"), ModEntities.BRAVERY);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("tsundere"), ModEntities.TSUNDERE);
		Registry.register(ModRegistries.MAID_PERSONALITY, UMULittleMaid.identifier("shy"), ModEntities.SHY);

		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("none"), ModEntities.NONE);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("fencer"), ModEntities.FENCER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("cracker"), ModEntities.CRACKER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("archer"), ModEntities.ARCHER);
		Registry.register(ModRegistries.MAID_JOB, UMULittleMaid.identifier("guard"), ModEntities.GUARD);

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

		OWNER = new MemoryModuleType<>(Optional.of(DynamicSerializableUuid.CODEC));

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

		NONE = new MaidJob.Builder()
				.build();
		FENCER = new MaidJob.Builder()
				.setItemStackPredicate(itemStack -> itemStack.getItem() instanceof SwordItem)
				.setActive()
				.setPounce()
				.build();
		CRACKER = new MaidJob.Builder()
				.setItemStackPredicate(itemStack -> itemStack.getItem() instanceof AxeItem)
				.setActive()
				.build();
		ARCHER = new MaidJob.Builder()
				.setItemStackPredicate(itemStack -> itemStack.getItem() instanceof BowItem)
				.setActive()
				.build();
		GUARD = new MaidJob.Builder()
				.setItemStackPredicate(itemStack -> itemStack.isOf(Items.SHIELD))
				.setActive()
				.build();
	}
}
