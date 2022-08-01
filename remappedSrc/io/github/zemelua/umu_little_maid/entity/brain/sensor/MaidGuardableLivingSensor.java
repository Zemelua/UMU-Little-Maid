package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleLivingEntitySensor;

import java.util.List;
import java.util.Optional;

public class MaidGuardableLivingSensor extends NearestVisibleLivingEntitySensor {
	@Override
	protected boolean matches(LivingEntity maid, LivingEntity target) {
		Brain<?> brain = maid.getBrain();
		Optional<List<LivingEntity>> attractTargets = brain.getOptionalMemory(ModEntities.MEMORY_ATTRACTABLE_LIVINGS);

		return attractTargets.map(livingEntities -> livingEntities.contains(target)).orElse(false);
	}

	@Override
	protected MemoryModuleType<LivingEntity> getOutputMemoryModule() {
		return ModEntities.MEMORY_GUARDABLE_LIVING;
	}
}
