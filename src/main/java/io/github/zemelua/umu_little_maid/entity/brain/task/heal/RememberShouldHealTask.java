package io.github.zemelua.umu_little_maid.entity.brain.task.heal;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.util.IHasMaster;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

import java.util.Map;

public class RememberShouldHealTask<E extends LivingEntity & IHasMaster> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_SHOULD_HEAL, MemoryModuleState.VALUE_ABSENT
	);

	public RememberShouldHealTask() {
		super(RememberShouldHealTask.REQUIRED_MEMORIES, 0);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E living) {
		return HealOwnerTask.shouldHeal(living);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		brain.remember(ModEntities.MEMORY_SHOULD_HEAL, Unit.INSTANCE);
	}
}
