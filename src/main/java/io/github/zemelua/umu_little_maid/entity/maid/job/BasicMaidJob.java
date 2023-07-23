package io.github.zemelua.umu_little_maid.entity.maid.job;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

import java.util.function.BiConsumer;

public class BasicMaidJob implements IMaidJob {
	private final TagKey<Item> items;
	private final BiConsumer<Brain<LittleMaidEntity>, LittleMaidEntity> brainInitializer;
	private final BiConsumer<Brain<LittleMaidEntity>, LittleMaidEntity> brainTicker;

	public BasicMaidJob(TagKey<Item> items,
	                    BiConsumer<Brain<LittleMaidEntity>, LittleMaidEntity> brainInitializer,
	                    BiConsumer<Brain<LittleMaidEntity>, LittleMaidEntity> brainTicker) {
		this.items = items;
		this.brainInitializer = brainInitializer;
		this.brainTicker = brainTicker;
	}

	@Override
	public boolean canAssume(LittleMaidEntity maid) {
		return maid.getMainHandStack().isIn(this.items);
	}

	@Override
	public void initBrain(Brain<LittleMaidEntity> brain, LittleMaidEntity maid) {
		this.brainInitializer.accept(brain, maid);
	}

	@Override
	public void tickBrain(Brain<LittleMaidEntity> brain, LittleMaidEntity maid) {
		this.brainTicker.accept(brain, maid);
	}

	@Override
	public int getPriority() {
		return 0;
	}
}
