package io.github.zemelua.umu_little_maid.entity.maid;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class MaidJob {
	private final Predicate<ItemStack> itemStackPredicate;
	private final Consumer<Brain<LittleMaidEntity>> brainInitializer;
	private final Consumer<Brain<LittleMaidEntity>> brainTicker;
	private final Predicate<RegistryEntry<PointOfInterestType>> jobSite;
	private final Identifier texture;

	public MaidJob(Predicate<ItemStack> itemStackPredicate,
	               Consumer<Brain<LittleMaidEntity>> brainInitializer,
	               Consumer<Brain<LittleMaidEntity>> brainTicker,
	               Predicate<RegistryEntry<PointOfInterestType>> jobSite,
	               Identifier texture) {
		this.itemStackPredicate = itemStackPredicate;
		this.brainInitializer = brainInitializer;
		this.brainTicker = brainTicker;
		this.jobSite = jobSite;
		this.texture = texture;
	}

	public boolean canApply(LivingEntity entity) {
		return entity.isHolding(this.itemStackPredicate);
	}

	public void initializeBrain(Brain<LittleMaidEntity> brain) {
		this.brainInitializer.accept(brain);
	}

	public void tickBrain(Brain<LittleMaidEntity> brain) {
		this.brainTicker.accept(brain);
	}

	public boolean isJobSite(RegistryEntry<PointOfInterestType> poi) {
		return this.jobSite.test(poi);
	}

	public Identifier getTexture() {
		return this.texture;
	}
}
