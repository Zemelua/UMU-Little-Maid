package io.github.zemelua.umu_little_maid.entity.brain.task.shear;

import io.github.zemelua.umu_little_maid.api.EveryTickTask;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Comparator;
import java.util.Optional;

public class UpdateShearTargetTask<E extends LivingEntity> extends EveryTickTask<E> {
	@Override
	public void tick(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();
		Optional<ShearableMobWrapper<?>> target = brain.getOptionalRegisteredMemory(ModMemories.SHEAR_TARGET);

		target.ifPresent(t -> {
			if (!t.get().isShearable() || !t.get().isLiving()) {
				brain.forget(ModMemories.SHEAR_TARGET);
			}
		});

		if (target.isEmpty()) {
			Optional<ShearableMobWrapper<SheepEntity>> candidate = brain.getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS).stream()
					.flatMap(c -> c.stream(l -> l instanceof SheepEntity && l.isLiving()))
					.map(l -> new ShearableMobWrapper<>((SheepEntity) l))
					.filter(IShearableWrapper::isShearable)
					.min(Comparator.comparingDouble(s -> s.get().distanceTo(living)));
			candidate.ifPresent(c -> brain.remember(ModMemories.SHEAR_TARGET, c, 100L));
		}
	}
}
