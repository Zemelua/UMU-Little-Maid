package io.github.zemelua.umu_little_maid.entity.brain.task.farm.deliver;

import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.ILittleMaidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.SingleTickTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public class MaidWalkToBoxTask<M extends LivingEntity & ILittleMaidEntity> extends SingleTickTask<M> {
	@Override
	public boolean trigger(ServerWorld world, M maid, long time) {
		Brain<?> brain = maid.getBrain();

		if (maid.isDelivering()) return false;
		if (brain.hasMemoryModule(MemoryModuleType.WALK_TARGET)) return false;

		Optional<BlockPos> boxPos = brain.getOptionalRegisteredMemory(ModMemories.DELIVERY_BOX);
		if (boxPos.isPresent() && !boxPos.get().isWithinDistance(maid.getBlockPos(), 1)) {
			brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(boxPos.get(), 0.8F, 1));

			return true;
		}

		return false;
	}
}
