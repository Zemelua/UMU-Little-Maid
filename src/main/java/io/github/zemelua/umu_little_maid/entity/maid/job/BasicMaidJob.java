package io.github.zemelua.umu_little_maid.entity.maid.job;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;

import java.util.function.Consumer;

public class BasicMaidJob implements IMaidJob {
	private final TagKey<Item> items;
	private final Consumer<Brain<LittleMaidEntity>> brainInitializer;
	private final Consumer<Brain<LittleMaidEntity>> brainTicker;

	public BasicMaidJob(TagKey<Item> items,
	                    Consumer<Brain<LittleMaidEntity>> brainInitializer,
	                    Consumer<Brain<LittleMaidEntity>> brainTicker) {
		this.items = items;
		this.brainInitializer = brainInitializer;
		this.brainTicker = brainTicker;
	}

	@Override
	public boolean canAssume(LittleMaidEntity maid) {
		return maid.getMainHandStack().isIn(this.items);
	}

	@Override
	public void initBrain(Brain<LittleMaidEntity> brain) {
		this.brainInitializer.accept(brain);
	}

	@Override
	public void tickBrain(Brain<LittleMaidEntity> brain) {
		this.brainTicker.accept(brain);
	}

	@Override
	public int getPriority() {
		return 0;
	}
}
