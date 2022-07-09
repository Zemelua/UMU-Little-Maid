package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleLivingEntitySensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;

public class MaidAttackablesSensor extends NearestVisibleLivingEntitySensor {
	@Override
	protected boolean matches(LivingEntity littleMaid, LivingEntity target) {
		return !target.getType().getSpawnGroup().isPeaceful()
				&& target.isInRange(littleMaid, 10.0)
				&& Sensor.testAttackableTargetPredicate(littleMaid, target);
	}

	@Override
	protected MemoryModuleType<LivingEntity> getOutputMemoryModule() {
		return MemoryModuleType.NEAREST_ATTACKABLE;
	}
}
