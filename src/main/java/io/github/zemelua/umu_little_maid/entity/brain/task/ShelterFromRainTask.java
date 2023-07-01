package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.util.IAvoidRain;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static net.minecraft.entity.ai.brain.MemoryModuleState.*;
import static net.minecraft.entity.ai.brain.MemoryModuleType.*;

public class ShelterFromRainTask<E extends PathAwareEntity & IAvoidRain> extends MultiTickTask<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			WALK_TARGET, VALUE_ABSENT
	);

	private final BlockPos.Mutable targetPos = new BlockPos.Mutable();

	public ShelterFromRainTask() {
		super(REQUIRED_MEMORIES, 40);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E mob) {
		if (!mob.isBeingRainedOn()) return false;
		if (!mob.shouldAvoidRain()) return false;

		@Nullable Vec3d targetPos = FuzzyTargeting.find(mob, 10, 3, posValue -> !world.hasRain(posValue)
				? mob.getPathfindingFavor(posValue) + 99.0F
				: mob.getPathfindingFavor(posValue));

		if (targetPos != null) {
			this.targetPos.set(targetPos.getX(), targetPos.getY(), targetPos.getZ());

			return true;
		}

		return false;
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		brain.remember(WALK_TARGET, new WalkTarget(this.targetPos.toImmutable(), 1.0F, 0));
	}
}
