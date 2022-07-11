package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.NearestVisibleLivingEntitySensor;
import net.minecraft.entity.ai.brain.sensor.Sensor;

import java.util.function.Predicate;

public class MaidAttackableSensor extends NearestVisibleLivingEntitySensor {
	public static final Predicate<LivingEntity> IS_ENEMY = (living
		-> ModUtils.isMonster(living) && living.getType() != EntityType.CREEPER);

	@Override
	protected boolean matches(LivingEntity littleMaid, LivingEntity target) {
		return MaidAttackableSensor.IS_ENEMY.test(target)
				&& target.isInRange(littleMaid, 10.0)
				&& Sensor.testAttackableTargetPredicate(littleMaid, target);
	}

	@Override
	protected MemoryModuleType<LivingEntity> getOutputMemoryModule() {
		return MemoryModuleType.NEAREST_ATTACKABLE;
	}
}
