package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;

import java.util.Map;
import java.util.Optional;

public class ForgetHomeTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			MemoryModuleType.HOME, MemoryModuleState.VALUE_PRESENT
	);

	public ForgetHomeTask() {
		super(ForgetHomeTask.REQUIRED_MEMORIES, 1);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		Optional<GlobalPos> pos = brain.getOptionalMemory(MemoryModuleType.HOME);

		pos.ifPresent(posObject -> {
			if (!RememberHomeTask.isHome(world.getBlockState(pos.get().getPos()))) {
				brain.forget(MemoryModuleType.HOME);
			}
		});
	}
}
