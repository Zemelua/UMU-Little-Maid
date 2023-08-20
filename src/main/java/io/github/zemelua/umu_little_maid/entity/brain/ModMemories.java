package io.github.zemelua.umu_little_maid.entity.brain;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.brain.task.shear.ShearableMobWrapper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class ModMemories {
	public static final MemoryModuleType<BlockPos> AVAILABLE_BED = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<BlockPos> SLEEP_POS = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<GlobalPos> ANCHOR = new MemoryModuleType<>(Optional.of(GlobalPos.CODEC));
	public static final MemoryModuleType<Unit> CANT_REACH_HOME = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<LivingEntity> GUARD_AGAINST = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<Unit> IS_SITTING = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
	public static final MemoryModuleType<Unit> HAS_ARROWS = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
	public static final MemoryModuleType<List<LivingEntity>> ATTRACTABLE_LIVINGS = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<LivingEntity> GUARDABLE_LIVING = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<List<BlockPos>> FARMABLE_POSES = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<BlockPos> FARM_POS = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<Collection<ShearableMobWrapper<?>>> SHEARABLE_LIVINGS = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<ShearableMobWrapper<?>> SHEAR_TARGET = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<BlockPos> DELIVERY_BOX = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<List<Pair<GlobalPos, Long>>> UNDELIVERABLE_BOXES = new MemoryModuleType<>(Optional.of(Codec.pair(GlobalPos.CODEC, Codec.LONG).listOf()));
	public static final MemoryModuleType<Unit> SHOULD_HEAL = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<Unit> SHOULD_EAT = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<Unit> SHOULD_SLEEP = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
	public static final MemoryModuleType<TridentEntity> THROWN_TRIDENT = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<Unit> THROWN_TRIDENT_COOLDOWN = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
	public static final MemoryModuleType<Unit> SHOULD_BREATH = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));
	public static final MemoryModuleType<Unit> IS_HUNTING = new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE)));

	public static void init() {
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("available_bed"), AVAILABLE_BED);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("sleep_bed"), SLEEP_POS);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("anchor"), ANCHOR);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("cant_reach_home"), CANT_REACH_HOME);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("guard_against"), GUARD_AGAINST);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("is_sitting"), IS_SITTING);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("has_arrows"), HAS_ARROWS);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("attractable_livings"), ATTRACTABLE_LIVINGS);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("guardable_living"), GUARDABLE_LIVING);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("farmable_poses"), FARMABLE_POSES);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("farm_pos"), FARM_POS);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("shearable_livings"), SHEARABLE_LIVINGS);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("shear_target"), SHEAR_TARGET);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("delivery_box"), DELIVERY_BOX);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("undeliverable_boxes"), UNDELIVERABLE_BOXES);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("should_heal"), SHOULD_HEAL);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("should_eat"), SHOULD_EAT);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("should_sleep"), SHOULD_SLEEP);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("thrown_trident"), THROWN_TRIDENT);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("thrown_trident_cooldown"), THROWN_TRIDENT_COOLDOWN);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("should_breath"), SHOULD_BREATH);
		Registry.register(Registries.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("is_hunting"), IS_HUNTING);
	}

	private ModMemories() {}
}
