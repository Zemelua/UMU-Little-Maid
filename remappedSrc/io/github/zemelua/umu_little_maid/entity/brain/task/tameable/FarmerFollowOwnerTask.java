package io.github.zemelua.umu_little_maid.entity.brain.task.tameable;

import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;

public class FarmerFollowOwnerTask<E extends PathAwareEntity & Tameable> extends FollowOwnerTask<E> {
	public FarmerFollowOwnerTask(float minDistance, float maxDistance) {
		super(minDistance, maxDistance);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E tameable) {
		return super.shouldRun(world, tameable) && !tameable.getBrain().hasMemoryModule(ModEntities.MEMORY_FARM_SITE);
	}
}
