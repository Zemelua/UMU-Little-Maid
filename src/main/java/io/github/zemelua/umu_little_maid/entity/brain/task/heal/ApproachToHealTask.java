package io.github.zemelua.umu_little_maid.entity.brain.task.heal;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.util.IHasMaster;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;
import java.util.Optional;

public class ApproachToHealTask<E extends LivingEntity & IHasMaster> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT,
			MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED,
			ModMemories.SHOULD_HEAL, MemoryModuleState.VALUE_PRESENT
	);

	private final float speed;

	public ApproachToHealTask(float speed) {
		super(ApproachToHealTask.REQUIRED_MEMORIES);

		this.speed = speed;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E living) {
		Optional<PlayerEntity> master = living.getMaster();
		if (master.isEmpty()) return false;

		return living.distanceTo(master.get()) > 4.0D;
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();
		Optional<PlayerEntity> master = living.getMaster();

		if (master.isPresent()) {
			brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(master.get(), this.speed, 3));
			brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(master.get(), true));
		}
	}
}
