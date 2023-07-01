package io.github.zemelua.umu_little_maid.entity.brain.task.heal;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.util.IHasMaster;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;

public class ForgetShouldHealTask<E extends LivingEntity & IHasMaster> extends MultiTickTask<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModMemories.SHOULD_HEAL, MemoryModuleState.VALUE_PRESENT
	);

	public ForgetShouldHealTask() {
		super(ForgetShouldHealTask.REQUIRED_MEMORIES, 0);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E living) {
		return !HealOwnerTask.shouldHeal(living);
	}

	@Override
	protected void run(ServerWorld world, E tameable, long time) {
		Brain<?> brain = tameable.getBrain();

		brain.forget(ModMemories.SHOULD_HEAL);
	}
}
