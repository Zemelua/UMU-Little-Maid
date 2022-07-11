package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import com.google.common.collect.ImmutableSet;
import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.task.MaidEatTask;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

import java.util.Set;

public class MaidShouldEatSensor extends Sensor<LittleMaidEntity> {
	public MaidShouldEatSensor() {
		super(1);
	}

	@Override
	protected void sense(ServerWorld world, LittleMaidEntity maid) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		if (brain.hasMemoryModule(MemoryModuleType.IS_PANICKING)
				|| MaidEatTask.searchHealItems(maid).isEmpty()) {
			brain.forget(ModEntities.MEMORY_SHOULD_EAT);

			return;
		}

		if (brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET)
				|| brain.hasMemoryModule(ModEntities.MEMORY_GUARD_TARGET)) {
			if (maid.getHealth() < maid.getMaxHealth() * 0.4D) {
				brain.remember(ModEntities.MEMORY_SHOULD_EAT, Unit.INSTANCE);

				return;
			}
		} else {
			if (maid.getHealth() < maid.getMaxHealth()) {
				brain.remember(ModEntities.MEMORY_SHOULD_EAT, Unit.INSTANCE);

				return;
			}
		}

		brain.forget(ModEntities.MEMORY_SHOULD_EAT);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(ModEntities.MEMORY_SHOULD_EAT);
	}
}
