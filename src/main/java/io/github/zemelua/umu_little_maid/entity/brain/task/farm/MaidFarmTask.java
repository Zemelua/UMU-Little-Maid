package io.github.zemelua.umu_little_maid.entity.brain.task.farm;

import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.brain.sensor.MaidFarmablePosesSensor;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.action.HarvestAction;
import io.github.zemelua.umu_little_maid.entity.maid.action.PlantAction;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.task.SingleTickTask;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class MaidFarmTask extends SingleTickTask<LittleMaidEntity> {
	@Override
	public boolean trigger(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();
		Optional<BlockPos> farmPos = brain.getOptionalRegisteredMemory(ModMemories.FARM_POS);

		if (farmPos.isPresent() && farmPos.get().isWithinDistance(maid.getPos(), 1.0D) && maid.getAction().isEmpty()) {
			ItemStack crop = maid.getHasCrop();

			if (MaidFarmTaskOld.isPlantable(farmPos.get(), world) && !crop.isEmpty()) {
				maid.startAction(new PlantAction(maid, farmPos.get()));

				MaidFarmTaskOld.resetMemories(brain, farmPos.get());
			} else if (MaidFarmTaskOld.isHarvestable(farmPos.get(), world) || MaidFarmTaskOld.isGourd(farmPos.get(), world)) {
				maid.startAction(new HarvestAction(maid, farmPos.get()));

				MaidFarmTaskOld.resetMemories(brain, farmPos.get());
			}

			if (MaidFarmablePosesSensor.canAnyFarm(world, maid, farmPos.get())) {
				brain.getOptionalRegisteredMemory(ModMemories.FARMABLE_POSES).ifPresent(poses -> poses.add(farmPos.get()));
			}

			return true;
		}

		return false;
	}
}
