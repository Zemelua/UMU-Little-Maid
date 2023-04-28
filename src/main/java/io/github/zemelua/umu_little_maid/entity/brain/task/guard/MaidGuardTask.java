package io.github.zemelua.umu_little_maid.entity.brain.task.guard;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.mixin.TaskAccessor;
import io.github.zemelua.umu_little_maid.util.IHasMaster;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.Optional;

public class MaidGuardTask<E extends MobEntity & IHasMaster> extends Task<E> {
	private static final ImmutableMap<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_GUARD_TARGET, MemoryModuleState.VALUE_PRESENT
	);

	private final double moveStartDistance;
	private final double guardStartDistance;
	private final float normalSpeed;
	private final float guardSpeed;

	public MaidGuardTask(double moveStartDistance, double guardStartDistance, float speed) {
		super(MaidGuardTask.REQUIRED_MEMORIES);

		this.moveStartDistance = moveStartDistance;
		this.guardStartDistance = guardStartDistance;
		this.normalSpeed = speed;
		this.guardSpeed = speed * 0.46666666666F;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E mob) {
		Optional<PlayerEntity> master = mob.getMaster();

		return master
				.filter(player -> !player.isSpectator() && mob.distanceTo(player) <= this.moveStartDistance)
				.isPresent();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected boolean shouldKeepRunning(ServerWorld world, E mob, long time) {
		return this.shouldRun(world, mob) && ((TaskAccessor<E>) this).callHasRequiredMemoryState(mob);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	protected void run(ServerWorld world, E tameable, long time) {
		Brain<?> brain = tameable.getBrain();

		brain.forget(MemoryModuleType.WALK_TARGET);
		brain.forget(MemoryModuleType.LOOK_TARGET);
	}

	@Override
	protected void keepRunning(ServerWorld world, E mob, long time) {
		Brain<?> brain = mob.getBrain();

		Optional<PlayerEntity> master = mob.getMaster();
		if (master.isEmpty()) return;

		brain.getOptionalMemory(ModEntities.MEMORY_ATTRACTABLE_LIVINGS).ifPresent(list -> list.stream()
				.filter(living -> living instanceof MobEntity)
				.map(living -> (MobEntity) living)
				.forEach(target -> target.setTarget(mob))
		);

		Optional<LivingEntity> guardTarget = brain.getOptionalMemory(ModEntities.MEMORY_GUARD_TARGET);

		if (guardTarget.isPresent()) {
			boolean shouldGuard = mob.distanceTo(master.get()) <= this.guardStartDistance;
			brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(guardTarget.get(), true));

			if (shouldGuard) {
				if (!mob.isBlocking()) {
					mob.setCurrentHand(Hand.MAIN_HAND);
				}
			} else {
				if (mob.isBlocking()) {
					mob.clearActiveItem();
				}
			}

			Vec3d ownerPos = master.get().getPos();
			Vec3d guardTargetPos = guardTarget.get().getPos();
			Vec3d guardPos = ownerPos.add(guardTargetPos.subtract(ownerPos).normalize());

			Vec3d maidPos = mob.getPos();
			Vec3f moveVec = new Vec3f(maidPos.subtract(guardPos));
			Vec3f lookVec = new Vec3f(mob.getRotationVector().multiply(1.0D, 0.0D, 1.0D));

			Vec3f forwardVec = lookVec.copy();

			Quaternion quaternion = new Quaternion(Vec3f.POSITIVE_Y, -90.0F, true);
			lookVec.rotate(quaternion);
			Vec3f sidewaysVec = lookVec.copy();

			moveVec.normalize();
			float speed = mob.isBlocking() ? this.guardSpeed : this.normalSpeed;
			float forwardSpeed = -moveVec.dot(forwardVec) * speed;
			float sidewaysSpeed = moveVec.dot(sidewaysVec) * speed;

			mob.getMoveControl().strafeTo(forwardSpeed, sidewaysSpeed);
			mob.setYaw(MathHelper.clampAngle(mob.getYaw(), mob.getHeadYaw(), 0.0F));
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, E mob, long time) {
		mob.clearActiveItem();
	}
}
