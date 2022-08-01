package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleLivingEntitySensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;

public class MaidAttackableSensor extends NearestVisibleLivingEntitySensor {
	@Override
	protected boolean matches(LivingEntity living, LivingEntity target) {
		return living instanceof LittleMaidEntity maid && maid.getPersonality().isHostile(target)
				&& target.isInRange(maid, 10.0)
				&& Sensor.testAttackableTargetPredicate(maid, target);
	}

	@Override
	protected MemoryModuleType<LivingEntity> getOutputMemoryModule() {
		return MemoryModuleType.NEAREST_ATTACKABLE;
	}
}
