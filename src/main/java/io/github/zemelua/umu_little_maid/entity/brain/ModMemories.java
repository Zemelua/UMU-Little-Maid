package io.github.zemelua.umu_little_maid.entity.brain;

import com.mojang.serialization.Codec;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Optional;

public final class ModMemories {
	public static final MemoryModuleType<BlockPos> AVAILABLE_BED = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<BlockPos> SLEEP_POS = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<GlobalPos> ANCHOR = new MemoryModuleType<>(Optional.of(GlobalPos.CODEC));
	public static final MemoryModuleType<List<GlobalPos>> DELIVERY_BOXES = new MemoryModuleType<>(Optional.of(ModUtils.Conversions.GLOBAL_POS_COLLECTION_CODEC));
	public static final MemoryModuleType<Unit> CANT_REACH_HOME = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<LivingEntity> GUARD_AGAINST = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<Unit> IS_SITTING = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
	public static final MemoryModuleType<Unit> HAS_ARROWS = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
	public static final MemoryModuleType<List<LivingEntity>> ATTRACTABLE_LIVINGS = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<LivingEntity> GUARDABLE_LIVING = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<List<BlockPos>> FARMABLE_POSES = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<BlockPos> FARM_POS = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<Unit> FARM_COOLDOWN = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
	public static final MemoryModuleType<Unit> SHOULD_HEAL = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<Unit> SHOULD_EAT = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<Unit> SHOULD_SLEEP = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
	public static final MemoryModuleType<TridentEntity> THROWN_TRIDENT = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<Unit> THROWN_TRIDENT_COOLDOWN = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
	public static final MemoryModuleType<Unit> SHOULD_BREATH = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
	public static final MemoryModuleType<Unit> IS_HUNTING = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));

	public static void init() {
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("available_bed"), AVAILABLE_BED);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("sleep_bed"), SLEEP_POS);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("anchor"), ANCHOR);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("delivery_boxes"), DELIVERY_BOXES);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("cant_reach_home"), CANT_REACH_HOME);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("guard_against"), GUARD_AGAINST);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("is_sitting"), IS_SITTING);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("has_arrows"), HAS_ARROWS);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("attractable_livings"), ATTRACTABLE_LIVINGS);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("guardable_living"), GUARDABLE_LIVING);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("farmable_poses"), FARMABLE_POSES);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("farm_pos"), FARM_POS);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("farm_cooldown"), FARM_COOLDOWN);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("should_heal"), SHOULD_HEAL);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("should_eat"), SHOULD_EAT);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("should_sleep"), SHOULD_SLEEP);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("thrown_trident"), THROWN_TRIDENT);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("thrown_trident_cooldown"), THROWN_TRIDENT_COOLDOWN);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("should_breath"), SHOULD_BREATH);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("is_hunting"), IS_HUNTING);
	}

	private ModMemories() {}
}
