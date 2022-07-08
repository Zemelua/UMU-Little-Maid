package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import com.google.common.collect.ImmutableSet;
import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;

import java.util.Set;

public class OwnerSensor extends Sensor<LittleMaidEntity> {
	public OwnerSensor() {
		super(1);
	}

	@Override
	protected void sense(ServerWorld world, LittleMaidEntity entity) {
		Brain<?> brain = entity.getBrain();

		brain.remember(ModEntities.OWNER, entity.getOwnerUuid());
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.<MemoryModuleType<?>>builder()
				.add(ModEntities.OWNER)
				.build();
	}
}
