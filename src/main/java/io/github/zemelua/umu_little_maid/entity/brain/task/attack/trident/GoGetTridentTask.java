package io.github.zemelua.umu_little_maid.entity.brain.task.attack.trident;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;
import java.util.Optional;

public class GoGetTridentTask<E extends MobEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of();

	public GoGetTridentTask() {
		super(GoGetTridentTask.REQUIRED_MEMORIES, 200);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E mob, long time) {
		Brain<?> brain = mob.getBrain();

		Optional<TridentEntity> trident = brain.getOptionalMemory(ModEntities.MEMORY_THROWN_TRIDENT);
		return trident.isPresent() && !trident.get().isRemoved();
	}

	@Override
	protected void keepRunning(ServerWorld world, E mob, long time) {
		Brain<?> brain = mob.getBrain();

		brain.getOptionalMemory(ModEntities.MEMORY_THROWN_TRIDENT)
				.ifPresent(trident -> brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(trident, 1.0F, 0), 200));
	}

	@Override
	protected void finishRunning(ServerWorld world, E mob, long time) {
		Brain<?> brain = mob.getBrain();

		brain.forget(MemoryModuleType.WALK_TARGET);
		brain.forget(ModEntities.MEMORY_THROWN_TRIDENT);
		brain.forget(ModEntities.MEMORY_THROWN_TRIDENT_COOLDOWN);
	}
}
