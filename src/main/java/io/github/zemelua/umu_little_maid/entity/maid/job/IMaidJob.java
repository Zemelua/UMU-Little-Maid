package io.github.zemelua.umu_little_maid.entity.maid.job;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;

public interface IMaidJob {
	boolean canAssume(LittleMaidEntity maid);
	void initBrain(Brain<LittleMaidEntity> brain, LittleMaidEntity maid);
	void tickBrain(Brain<LittleMaidEntity> brain, LittleMaidEntity maid);
	int getPriority();
}
