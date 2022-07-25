package io.github.zemelua.umu_little_maid.entity.brain.task.farm;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Optional;

public class KeepAroundFarmSiteTask<E extends PathAwareEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_FARM_SITE, MemoryModuleState.VALUE_PRESENT,
			MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT,
			MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT
	);

	public KeepAroundFarmSiteTask() {
		super(KeepAroundFarmSiteTask.REQUIRED_MEMORIES, 0);
	}

	@Override
	protected void run(ServerWorld world, E pathAware, long time) {
		Brain<?> brain = pathAware.getBrain();

		Optional<GlobalPos> pos = brain.getOptionalMemory(ModEntities.MEMORY_FARM_SITE);
		if (pos.isPresent() && this.exceedsMaxRange(pathAware, pos.get())) {
			Optional<Vec3d> walkTo = Optional.empty();
			final int tryCount = 1000;
			int i;
			for (i = 0; i < tryCount && (walkTo.isEmpty() || this.exceedsMaxRange(pathAware, GlobalPos.create(world.getRegistryKey(), new BlockPos(walkTo.get())))); i++) {
				walkTo = Optional.ofNullable(NoPenaltyTargeting.findTo(pathAware, 15, 7, Vec3d.ofBottomCenter(pos.get().getPos()), 1.5707963705062866));
			}

			if (i == tryCount || walkTo.isEmpty()) {
				brain.forget(ModEntities.MEMORY_FARM_SITE);
				ForgetFarmSiteTask.playLostSiteParticles(world, pathAware);
			} else {
				brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(walkTo.get(), 0.8F, 1));
			}
		}
	}

	private boolean exceedsMaxRange(E pathAware, GlobalPos pos) {
		return pos.getPos().getManhattanDistance(pathAware.getBlockPos()) > 18;
	}
}
