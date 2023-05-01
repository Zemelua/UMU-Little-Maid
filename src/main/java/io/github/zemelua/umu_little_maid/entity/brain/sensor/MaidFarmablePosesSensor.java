package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.MaidFarmTask;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class MaidFarmablePosesSensor extends Sensor<LittleMaidEntity> {
	@Override
	protected void sense(ServerWorld world, LittleMaidEntity maid) {
		Brain<LittleMaidEntity> brain = maid.getBrain();
		List<BlockPos> list = Lists.newArrayList();

		for (int x = -16; x < 17; x++) {
			for (int y = -3; y < 4; y++) {
				for (int z = -16; z < 17; z++) {
					BlockPos pos = maid.getBlockPos().add(x, y, z);

					if (canAnyFarm(world, maid, pos) && canReach(maid, pos)) {
						list.add(pos);
					}
				}
			}
		}

		if (!list.isEmpty()) {
			brain.remember(ModMemories.FARMABLE_POSES, list);
		} else {
			brain.forget(ModMemories.FARMABLE_POSES);
		}
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(ModMemories.FARMABLE_POSES);
	}

	public static boolean canAnyFarm(ServerWorld world, LittleMaidEntity maid, BlockPos pos) {
		return MaidFarmTask.isPlantable(pos, world) && !maid.getHasCrop().isEmpty()
				|| MaidFarmTask.isHarvestable(pos, world)
				|| MaidFarmTask.isGourd(pos, world) && maid.canBreakGourd();
	}

	public static boolean canReach(LittleMaidEntity maid, BlockPos pos) {
		@Nullable Path path = maid.getNavigation().findPathTo(pos, 0);

		return path != null && path.reachesTarget();
	}
}
