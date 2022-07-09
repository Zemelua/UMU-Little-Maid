package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.job.MaidJob;
import io.github.zemelua.umu_little_maid.mixin.TaskAccessor;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Map;

public class MaidJobTask extends Task<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of();

	private final Task<? super LittleMaidEntity> delegate;
	private final List<MaidJob> jobs;

	public MaidJobTask(Task<? super LittleMaidEntity> delegate, MaidJob... jobs) {
		super(MaidJobTask.REQUIRED_MEMORIES);

		this.delegate = delegate;
		this.jobs = ImmutableList.<MaidJob>builder().add(jobs).build();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity entity) {
		return ((TaskAccessor<? super LittleMaidEntity>) this.delegate).callShouldRun(world, entity)
				&& this.jobs.stream().anyMatch(job -> job == entity.getJob());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected boolean shouldKeepRunning(ServerWorld world, LittleMaidEntity entity, long time) {
		return ((TaskAccessor<? super LittleMaidEntity>) this.delegate).callShouldKeepRunning(world, entity, time)
				&& this.jobs.stream().anyMatch(job -> job == entity.getJob());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void run(ServerWorld world, LittleMaidEntity entity, long time) {
		((TaskAccessor<? super LittleMaidEntity>) this.delegate).callRun(world, entity, time);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void keepRunning(ServerWorld world, LittleMaidEntity entity, long time) {
		((TaskAccessor<? super LittleMaidEntity>) this.delegate).callKeepRunning(world, entity, time);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void finishRunning(ServerWorld world, LittleMaidEntity entity, long time) {
		((TaskAccessor<? super LittleMaidEntity>) this.delegate).callFinishRunning(world, entity, time);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected boolean isTimeLimitExceeded(long time) {
		return ((TaskAccessor<? super LittleMaidEntity>) this.delegate).callIsTimeLimitExceeded(time);
	}
}
