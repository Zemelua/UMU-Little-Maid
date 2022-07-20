package io.github.zemelua.umu_little_maid.entity.maid;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MaidJob {
	private final Predicate<ItemStack> itemStackPredicate;
	private final Supplier<Brain.Profile<LittleMaidEntity>> profileCreator;
	private final Consumer<Brain<LittleMaidEntity>> brainInitializer;
	private final Consumer<Brain<LittleMaidEntity>> brainTicker;

	public MaidJob(Predicate<ItemStack> itemStackPredicate) {
		this(itemStackPredicate,
				() -> Brain.createProfile(ImmutableList.of(), ImmutableList.of()),
				brain -> {},
				brain -> {}
		);
	}

	public MaidJob(Predicate<ItemStack> itemStackPredicate,
	               Supplier<Brain.Profile<LittleMaidEntity>> profileCreator,
	               Consumer<Brain<LittleMaidEntity>> brainInitializer,
	               Consumer<Brain<LittleMaidEntity>> brainTicker) {
		this.itemStackPredicate = itemStackPredicate;
		this.profileCreator = profileCreator;
		this.brainInitializer = brainInitializer;
		this.brainTicker = brainTicker;
	}

	public boolean canApply(LivingEntity entity) {
		return entity.isHolding(this.itemStackPredicate);
	}

	public Brain.Profile<LittleMaidEntity> createProfile() {
		return this.profileCreator.get();
	}

	public void initializeBrain(Brain<LittleMaidEntity> brain) {
		this.brainInitializer.accept(brain);
	}

	public void tickBrain(Brain<LittleMaidEntity> brain) {
		this.brainTicker.accept(brain);
	}
}
