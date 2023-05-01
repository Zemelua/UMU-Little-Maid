package io.github.zemelua.umu_little_maid.entity.brain.sensor;

import com.google.common.collect.ImmutableSet;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.brain.task.eat.MaidEatTask;
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
			brain.forget(ModMemories.SHOULD_EAT);

			return;
		}

		if (brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET)
				|| brain.hasMemoryModule(ModMemories.GUARD_AGAINST)
				|| brain.hasMemoryModule(ModMemories.SHOULD_HEAL)) {
			if (maid.getHealth() < maid.getMaxHealth() * 0.4D) {
				brain.remember(ModMemories.SHOULD_EAT, Unit.INSTANCE);

				return;
			}
		} else {
			if (maid.getHealth() < maid.getMaxHealth()) {
				brain.remember(ModMemories.SHOULD_EAT, Unit.INSTANCE);

				return;
			}
		}

		brain.forget(ModMemories.SHOULD_EAT);
	}

	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(ModMemories.SHOULD_EAT);
	}
}
