package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import com.google.common.collect.ImmutableSet;
import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class MaidAttractTargetSensor extends Sensor<LittleMaidEntity> {
	@Override
	protected void sense(ServerWorld world, LittleMaidEntity maid) {
		List<LivingEntity> list = world.getEntitiesByClass(LivingEntity.class, maid.getBoundingBox().expand(6.0D, 3.0D, 6.0D), living -> {
			if (living == maid) return false;

			Entity owner = maid.getOwner();
			if (living instanceof MobEntity mob) {
				LivingEntity target = mob.getTarget();
				if (target != null && (target.equals(owner) || target.equals(maid))) return true;
			}
			if (owner instanceof LivingEntity ownerLiving) return living.equals(ownerLiving.getAttacker());

			return false;
		});

		list.sort(Comparator.comparingDouble(maid::squaredDistanceTo));
		Brain<LittleMaidEntity> brain = maid.getBrain();

		brain.remember(ModEntities.MEMORY_ATTRACT_TARGETS, list);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(ModEntities.MEMORY_ATTRACT_TARGETS);
	}
}
