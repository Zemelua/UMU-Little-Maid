package io.github.zemelua.umu_little_maid.entity.brain.task.attack.melee;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Optional;

public class PounceAtTargetTask<E extends LivingEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT
	);

	public PounceAtTargetTask() {
		super(PounceAtTargetTask.REQUIRED_MEMORIES);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E living) {
		Brain<?> brain = living.getBrain();

		if (living.hasPassengers()) return false;
		if (!living.isOnGround()) return false;

		Optional<LivingEntity> target = brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
		if (target.isEmpty()) return false;

		double distance = living.distanceTo(target.get());
		if (distance < 2.0D || distance > 4.0D) return false;

		return living.getRandom().nextInt(3) == 0;
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();
		Vec3d velocity = living.getVelocity();
		Optional<LivingEntity> target = brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
		if (target.isEmpty()) return;

		Vec3d jumpVec = new Vec3d(target.get().getX() - living.getX(), 0.0, target.get().getZ() - living.getZ());
		if (jumpVec.lengthSquared() > 1.0E-7) {
			jumpVec = jumpVec.normalize().multiply(0.4).add(velocity.multiply(0.2));
		}

		living.setVelocity(jumpVec.getX(), 0.4D, jumpVec.getZ());
	}
}
