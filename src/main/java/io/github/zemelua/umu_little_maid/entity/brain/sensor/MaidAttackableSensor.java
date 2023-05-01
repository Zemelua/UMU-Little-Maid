package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import com.google.common.collect.ImmutableSet;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;
import java.util.Set;

import static io.github.zemelua.umu_little_maid.entity.brain.ModMemories.IS_HUNTING;
import static net.minecraft.entity.ai.brain.MemoryModuleType.NEAREST_ATTACKABLE;
import static net.minecraft.entity.ai.brain.MemoryModuleType.VISIBLE_MOBS;

public class MaidAttackableSensor extends Sensor<LittleMaidEntity> {
	@Override
	protected void sense(ServerWorld world, LittleMaidEntity maid) {
		Brain<LittleMaidEntity> brain = maid.getBrain();
		Optional<LivingTargetCache> mobs = brain.getOptionalMemory(VISIBLE_MOBS);

		Optional<LivingEntity> hostile = mobs
				.flatMap(mobsObj -> mobsObj
						.findFirst(living -> isHostile(maid, living)));
		if (hostile.isPresent()) {
			brain.remember(NEAREST_ATTACKABLE, hostile.get());

			return;
		}

		if (brain.hasMemoryModule(IS_HUNTING)) {
			Optional<LivingEntity> chase = mobs
					.flatMap(mobsObj -> mobsObj
							.findFirst(living -> isChase(maid, living)));
			chase.ifPresent(entity -> brain.remember(NEAREST_ATTACKABLE, entity));
		}
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(VISIBLE_MOBS, NEAREST_ATTACKABLE);
	}

	public static boolean isHostile(LittleMaidEntity maid, LivingEntity living) {
		return maid.getPersonality().isHostile(living)
				&& living.isInRange(maid, 10.0)
				&& Sensor.testAttackableTargetPredicate(maid, living);
	}

	public static boolean isChase(LittleMaidEntity maid, LivingEntity living) {
		return maid.getPersonality().isChase(living)
				&& living.isInRange(maid, 10.0)
				&& Sensor.testAttackableTargetPredicate(maid, living);
	}
}
