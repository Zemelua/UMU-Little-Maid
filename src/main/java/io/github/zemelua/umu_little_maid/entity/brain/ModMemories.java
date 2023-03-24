package io.github.zemelua.umu_little_maid.entity.brain;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Optional;

public final class ModMemories {
	public static final MemoryModuleType<GlobalPos> ANCHOR = new MemoryModuleType<>(Optional.of(GlobalPos.CODEC));
	public static final MemoryModuleType<List<GlobalPos>> DELIVERY_BOXES = new MemoryModuleType<>(Optional.of(ModUtils.Conversions.GLOBAL_POS_COLLECTION_CODEC));

	public static void init() {
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("anchor"), ANCHOR);
		Registry.register(Registry.MEMORY_MODULE_TYPE, UMULittleMaid.identifier("delivery_boxes"), DELIVERY_BOXES);
	}

	private ModMemories() {}
}
