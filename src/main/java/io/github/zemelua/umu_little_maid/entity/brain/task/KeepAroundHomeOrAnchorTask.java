package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.MaidMode;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Optional;

public class KeepAroundHomeOrAnchorTask extends MultiTickTask<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			MemoryModuleType.IS_PANICKING, MemoryModuleState.VALUE_ABSENT,
			MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT
	);

	public KeepAroundHomeOrAnchorTask() {
		super(REQUIRED_MEMORIES, 0);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		return maid.getMode() == MaidMode.FREE;
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<?> brain = maid.getBrain();

		Optional<GlobalPos> home = maid.getHome();
		Optional<GlobalPos> anchor = maid.getAnchor();
		Optional<GlobalPos> pos = anchor.isPresent() ? anchor : home;


		if (pos.isPresent() && this.exceedsMaxRange(maid, pos.get())) {
			Optional<Vec3d> walkTo = Optional.empty();
			final int tryCount = 1000;
			int i;
			for (i = 0; i < tryCount && (walkTo.isEmpty() || this.exceedsMaxRange(maid, GlobalPos.create(world.getRegistryKey(), BlockPos.ofFloored(walkTo.get())))); i++) {
				walkTo = Optional.ofNullable(NoPenaltyTargeting.findTo(maid, 15, 7, Vec3d.ofBottomCenter(pos.get().getPos()), 1.5707963705062866));
			}

			if (i != tryCount && walkTo.isPresent()) {
				brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(walkTo.get(), 0.8F, 1));
			}
		}
	}

	private boolean exceedsMaxRange(LittleMaidEntity maid, GlobalPos pos) {
		return pos.getPos().getManhattanDistance(maid.getBlockPos()) > 18;
	}
}
