package io.github.zemelua.umu_little_maid.entity.maid;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public class MaidJob {
	private final Predicate<ItemStack> itemStackPredicate;
	private final Supplier<Brain.Profile<LittleMaidEntity>> profileCreator;
	private final Consumer<Brain<LittleMaidEntity>> brainInitializer;
	private final Consumer<Brain<LittleMaidEntity>> brainTicker;
	private final Identifier texture;

	public MaidJob(Predicate<ItemStack> itemStackPredicate,
	               Supplier<Brain.Profile<LittleMaidEntity>> profileCreator,
	               Consumer<Brain<LittleMaidEntity>> brainInitializer,
	               Consumer<Brain<LittleMaidEntity>> brainTicker,
	               Identifier texture) {
		this.itemStackPredicate = itemStackPredicate;
		this.profileCreator = profileCreator;
		this.brainInitializer = brainInitializer;
		this.brainTicker = brainTicker;
		this.texture = texture;
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

	public Identifier getTexture() {
		return this.texture;
	}
}
