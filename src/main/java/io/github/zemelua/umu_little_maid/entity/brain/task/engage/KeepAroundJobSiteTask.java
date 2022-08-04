package io.github.zemelua.umu_little_maid.entity.brain.task.engage;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Optional;

public class KeepAroundJobSiteTask extends Task<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_JOB_SITE, MemoryModuleState.VALUE_PRESENT,
			MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT,
			MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT
	);

	public KeepAroundJobSiteTask() {
		super(KeepAroundJobSiteTask.REQUIRED_MEMORIES, 0);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		return maid.isEngaging();
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<?> brain = maid.getBrain();

		Optional<GlobalPos> pos = brain.getOptionalMemory(ModEntities.MEMORY_JOB_SITE);
		if (pos.isPresent() && this.exceedsMaxRange(maid, pos.get())) {
			Optional<Vec3d> walkTo = Optional.empty();
			final int tryCount = 1000;
			int i;
			for (i = 0; i < tryCount && (walkTo.isEmpty() || this.exceedsMaxRange(maid, GlobalPos.create(world.getRegistryKey(), new BlockPos(walkTo.get())))); i++) {
				walkTo = Optional.ofNullable(NoPenaltyTargeting.findTo(maid, 15, 7, Vec3d.ofBottomCenter(pos.get().getPos()), 1.5707963705062866));
			}

			if (i == tryCount || walkTo.isEmpty()) {
				brain.forget(ModEntities.MEMORY_JOB_SITE);
				ForgetJobSiteTask.playLostSiteParticles(world, maid);
			} else {
				brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(walkTo.get(), 0.8F, 1));
			}
		}
	}

	private boolean exceedsMaxRange(LittleMaidEntity maid, GlobalPos pos) {
		return pos.getPos().getManhattanDistance(maid.getBlockPos()) > 18;
	}
}
