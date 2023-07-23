package io.github.zemelua.umu_little_maid.entity.maid.job;

import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.TagKey;

import java.util.function.BiConsumer;

public class PoseidonJob extends BasicMaidJob {
	public PoseidonJob(TagKey<Item> items, BiConsumer<Brain<LittleMaidEntity>, LittleMaidEntity> brainInitializer, BiConsumer<Brain<LittleMaidEntity>, LittleMaidEntity> brainTicker) {
		super(items, brainInitializer, brainTicker);
	}

	@Override
	public boolean canAssume(LittleMaidEntity maid) {
		return super.canAssume(maid) || maid.getBrain().hasMemoryModule(ModMemories.THROWN_TRIDENT_COOLDOWN);
	}
}
