package io.github.zemelua.umu_little_maid.entity.brain.task.eat;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;
import java.util.function.Predicate;

public class ForgetShouldEatTask extends MultiTickTask<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModMemories.SHOULD_EAT, MemoryModuleState.VALUE_PRESENT
	);

	private final Predicate<LittleMaidEntity> postpone;

	public ForgetShouldEatTask() {
		this(living -> false);
	}

	public ForgetShouldEatTask(Predicate<LittleMaidEntity> postpone) {
		super(ForgetShouldEatTask.REQUIRED_MEMORIES, 0);

		this.postpone = postpone;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		return !RememberShouldEatTask.shouldEat(maid, this.postpone);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<?> brain = maid.getBrain();

		brain.forget(ModMemories.SHOULD_EAT);
	}
}