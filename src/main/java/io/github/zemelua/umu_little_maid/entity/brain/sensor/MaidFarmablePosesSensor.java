package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.farm.MaidFarmTask;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MaidFarmablePosesSensor extends Sensor<LittleMaidEntity> {
	@Override
	protected void sense(ServerWorld world, LittleMaidEntity maid) {
		Brain<LittleMaidEntity> brain = maid.getBrain();
		List<BlockPos> list = Lists.newArrayList();
		Optional<GlobalPos> sitePos = brain.getOptionalMemory(ModEntities.MEMORY_JOB_SITE);

		if (sitePos.isEmpty()) {
			for (int x = -5; x < 6; x++) {
				for (int y = -2; y < 3; y++) {
					for (int z = -5; z < 6; z++) {
						BlockPos pos = maid.getBlockPos().add(x, y, z);

						if (MaidFarmTask.isPlantable(pos, world) && !maid.getHasCrop().isEmpty() || MaidFarmTask.isHarvestable(pos, world)) {
							list.add(pos);
						}
					}
				}
			}
		} else {
			for (int x = -16; x < 17; x++) {
				for (int y = -3; y < 4; y++) {
					for (int z = -16; z < 17; z++) {
						BlockPos pos = sitePos.get().getPos().add(x, y, z);

						if (MaidFarmTask.isPlantable(pos, world) && !maid.getHasCrop().isEmpty() || MaidFarmTask.isHarvestable(pos, world)) {
							list.add(pos);
						}
					}
				}
			}
		}

		if (!list.isEmpty()) {
			brain.remember(ModEntities.MEMORY_FARMABLE_POS, ModUtils.getNearestPos(list, maid));
		} else {
			brain.forget(ModEntities.MEMORY_FARMABLE_POS);
		}
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(ModEntities.MEMORY_FARMABLE_POS);
	}
}
