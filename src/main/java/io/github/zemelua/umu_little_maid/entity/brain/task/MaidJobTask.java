package io.github.zemelua.umu_little_maid.entity.brain.task;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.MaidJob;
import io.github.zemelua.umu_little_maid.mixin.TaskAccessor;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.Task;

import java.util.List;

public class MaidJobTask extends ConditionalTask<LittleMaidEntity> {
	public MaidJobTask(Task<? super LittleMaidEntity> delegate, MaidJob... jobs) {
		super((maid -> List.of(jobs).contains(maid.getJob())), delegate, true);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return time > ((TaskAccessor) this).getEndTime();
	}
}
