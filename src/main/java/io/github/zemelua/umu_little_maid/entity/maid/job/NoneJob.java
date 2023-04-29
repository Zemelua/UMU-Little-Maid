package io.github.zemelua.umu_little_maid.entity.maid.job;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;

import java.util.function.Consumer;

public class NoneJob implements IMaidJob {
	private final Consumer<Brain<LittleMaidEntity>> brainInitializer;
	private final Consumer<Brain<LittleMaidEntity>> brainTicker;

	public NoneJob(Consumer<Brain<LittleMaidEntity>> brainInitializer, Consumer<Brain<LittleMaidEntity>> brainTicker) {
		this.brainInitializer = brainInitializer;
		this.brainTicker = brainTicker;
	}

	@Override
	public boolean canAssume(LittleMaidEntity maid) {
		return true;
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
		return Integer.MIN_VALUE;
	}
}
