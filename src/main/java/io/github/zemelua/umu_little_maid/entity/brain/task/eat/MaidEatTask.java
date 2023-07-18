package io.github.zemelua.umu_little_maid.entity.brain.task.eat;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;

public class MaidEatTask extends MultiTickTask<LittleMaidEntity> {
	public MaidEatTask() {
		super(ImmutableMap.of(ModMemories.SHOULD_EAT, MemoryModuleState.VALUE_PRESENT), 16);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		return maid.canAction();
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		maid.eatFood(maid.getHasSugar().split(1), food -> maid.heal(5.0F));
	}
}
