package io.github.zemelua.umu_little_maid.entity.brain.task.sleep;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
import net.minecraft.util.math.GlobalPos;

import java.util.Map;
import java.util.Optional;

public class WalkToHomeTask<E extends PathAwareEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT,
			MemoryModuleType.HOME, MemoryModuleState.VALUE_PRESENT
	);

	private final float speed;

	private Path path;

	public WalkToHomeTask(float speed) {
		super(WalkToHomeTask.REQUIRED_MEMORIES, 120);

		this.speed = speed;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		Brain<?> brain = entity.getBrain();
		Optional<GlobalPos> pos = brain.getOptionalMemory(MemoryModuleType.HOME);

		if (pos.isPresent()) {
			Path path = entity.getNavigation().findPathTo(pos.get().getPos(), 0);

			if (path != null && path.reachesTarget()) {
				return true;
			} else {
				brain.remember(ModMemories.CANT_REACH_HOME, Unit.INSTANCE, 120);
				return false;
			}
		}

		brain.remember(ModMemories.CANT_REACH_HOME, Unit.INSTANCE, 120);
		return false;
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
		return true;
	}

	//	@Override
//	protected boolean shouldRun(ServerWorld world, E entity) {
//		Brain<?> brain = entity.getBrain();
//		Optional<GlobalPos> pos = brain.getOptionalMemory(MemoryModuleType.HOME);
//
//
//		if (pos.isPresent()) {
//			for (int i = 0; i < 100; i++) {
//				this.path = entity.getNavigation().findPathTo(pos.get().getPos(), 0);
//				if (this.path != null) return true;
//			}
//		}
//
//		return false;
//	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		UMULittleMaid.LOGGER.info("srth");


		Brain<?> brain = living.getBrain();
		Optional<GlobalPos> pos = brain.getOptionalMemory(MemoryModuleType.HOME);

		pos.ifPresent(posValue -> {
			brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(posValue.getPos().down(), this.speed, 0));
		});

//
//		brain.remember(MemoryModuleType.PATH, this.path);
//		living.getNavigation().startMovingAlong(this.path, this.speed);
	}
}
