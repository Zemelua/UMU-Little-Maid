package io.github.zemelua.umu_little_maid.entity.maid.job;

import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;

import java.util.function.Consumer;

public class PoseidonJob extends BasicMaidJob {
	public PoseidonJob(TagKey<Item> items, Consumer<Brain<LittleMaidEntity>> brainInitializer, Consumer<Brain<LittleMaidEntity>> brainTicker) {
		super(items, brainInitializer, brainTicker);
	}

	@Override
	public boolean canAssume(LittleMaidEntity maid) {
		return super.canAssume(maid) || maid.getBrain().hasMemoryModule(ModMemories.THROWN_TRIDENT_COOLDOWN);
	}
}
