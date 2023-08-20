package io.github.zemelua.umu_little_maid.entity.brain.task.shear;

import io.github.zemelua.umu_little_maid.api.EveryTickTask;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public class WalkToShearTargetTask<E extends LivingEntity> extends EveryTickTask<E> {
	@Override
	public void tick(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();
		Optional<ShearableMobWrapper<?>> target = brain.getOptionalRegisteredMemory(ModMemories.SHEAR_TARGET);

		if (!brain.hasMemoryModule(MemoryModuleType.WALK_TARGET)) {
			target.ifPresent(t -> LookTargetUtil.walkTowards(living, t.get(), 1.0F, 1));
		}
	}
}
