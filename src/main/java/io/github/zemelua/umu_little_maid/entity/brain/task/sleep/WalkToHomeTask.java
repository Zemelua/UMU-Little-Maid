package io.github.zemelua.umu_little_maid.entity.brain.task.sleep;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;

import java.util.Map;
import java.util.Optional;

public class WalkToHomeTask<E extends PathAwareEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT,
			MemoryModuleType.HOME, MemoryModuleState.VALUE_PRESENT
	);

	private final float speed;

	public WalkToHomeTask(float speed) {
		super(WalkToHomeTask.REQUIRED_MEMORIES);

		this.speed = speed;
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();
		Optional<GlobalPos> pos = brain.getOptionalMemory(MemoryModuleType.HOME);

		pos.ifPresent(posValue -> {
			brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(posValue.getPos(), this.speed, 0));
		});

//		pos.ifPresent(posValue -> {
//			for (int i = 0; i < 100; i++) {
//				boolean foundPath = NoPenaltyTargeting.findTo(living, 15, 7, Vec3d.ofBottomCenter(posValue.getPos()), Math.toRadians(90.0D)) != null;
//				brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(posValue.getPos(), this.speed, 0));
//			}
//		});
	}
}
