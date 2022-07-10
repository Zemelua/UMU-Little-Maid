package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import com.google.common.collect.ImmutableSet;
import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

import java.util.Set;

public class IsSittingSensor extends Sensor<LittleMaidEntity> {
	public IsSittingSensor() {
		super(1);
	}

	@Override
	protected void sense(ServerWorld world, LittleMaidEntity entity) {
		Brain<?> brain = entity.getBrain();

		if (entity.isSitting()) {
			brain.remember(ModEntities.MEMORY_IS_SITTING, Unit.INSTANCE);
		} else {
			brain.forget(ModEntities.MEMORY_IS_SITTING);
		}
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.<MemoryModuleType<?>>builder()
				.add(ModEntities.MEMORY_IS_SITTING)
				.build();
	}
}
