package io.github.zemelua.umu_little_maid.entity.maid;

import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class PoseidonJob extends MaidJob {
	public PoseidonJob(Predicate<ItemStack> itemStackPredicate,
	                   Consumer<Brain<LittleMaidEntity>> brainInitializer,
	                   Consumer<Brain<LittleMaidEntity>> brainTicker,
	                   Predicate<RegistryEntry<PointOfInterestType>> jobSite,
	                   Identifier texture) {
		super(itemStackPredicate, brainInitializer, brainTicker, jobSite, texture);
	}

	@Override
	public boolean canApply(LivingEntity entity) {
		return super.canApply(entity) || entity.getBrain().hasMemoryModule(ModEntities.MEMORY_THROWN_TRIDENT_COOLDOWN);
	}
}
