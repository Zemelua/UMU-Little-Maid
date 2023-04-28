package io.github.zemelua.umu_little_maid.entity.brain;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.Unit;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Optional;

public final class ModMemories {
	public static final MemoryModuleType<GlobalPos> ANCHOR = new MemoryModuleType<>(Optional.of(GlobalPos.CODEC));
	public static final MemoryModuleType<List<GlobalPos>> DELIVERY_BOXES = new MemoryModuleType<>(Optional.of(ModUtils.Conversions.GLOBAL_POS_COLLECTION_CODEC));
	public static final MemoryModuleType<Unit> CANT_REACH_HOME = new MemoryModuleType<>(Optional.empty());
	public static final MemoryModuleType<LivingEntity> GUARD_AGAINST = new MemoryModuleType<>(Optional.empty());

	public static void init() {
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("anchor"), ANCHOR);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("delivery_boxes"), DELIVERY_BOXES);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("cant_reach_home"), CANT_REACH_HOME);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("guard_against"), GUARD_AGAINST);
	}

	private ModMemories() {}
}
