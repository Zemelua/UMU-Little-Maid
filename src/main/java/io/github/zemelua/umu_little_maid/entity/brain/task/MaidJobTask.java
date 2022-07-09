package io.github.zemelua.umu_little_maid.entity.brain.task;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.job.MaidJob;
import net.minecraft.entity.ai.brain.task.ConditionalTask;
import net.minecraft.entity.ai.brain.task.Task;

import java.util.List;

public class MaidJobTask extends ConditionalTask<LittleMaidEntity> {
	public MaidJobTask(Task<? super LittleMaidEntity> delegate, MaidJob... jobs) {
		super((maid -> List.of(jobs).contains(maid.getJob())), delegate, true);
	}
}
