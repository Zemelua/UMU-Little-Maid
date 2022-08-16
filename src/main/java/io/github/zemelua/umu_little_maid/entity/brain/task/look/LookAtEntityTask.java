package io.github.zemelua.umu_little_maid.entity.brain.task.look;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

import static net.minecraft.entity.ai.brain.MemoryModuleState.*;
import static net.minecraft.entity.ai.brain.MemoryModuleType.*;

public class LookAtEntityTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			LOOK_TARGET, VALUE_ABSENT,
			WALK_TARGET, VALUE_ABSENT,
			VISIBLE_MOBS, VALUE_PRESENT
	);

	private final BiPredicate<E, LivingEntity> validTarget;

	public LookAtEntityTask(BiPredicate<E, LivingEntity> validTarget) {
		super(REQUIRED_MEMORIES, 60);

		this.validTarget = validTarget;
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		Optional<LivingTargetCache> livings = brain.getOptionalMemory(VISIBLE_MOBS);
		Optional<LivingEntity> target = livings.flatMap(livingsValue -> livingsValue.stream(livingValue -> true)
						.filter(livingValue -> this.validTarget.test(living, livingValue))
						.findFirst());

		target.ifPresent(targetValue -> brain.remember(LOOK_TARGET, new EntityLookTarget(targetValue, true)));
	}
}
