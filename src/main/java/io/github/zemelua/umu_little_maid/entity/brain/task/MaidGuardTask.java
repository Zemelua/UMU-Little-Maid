package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.mixin.TaskAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import javax.annotation.Nullable;
import java.util.Optional;

public class MaidGuardTask<E extends MobEntity & Tameable> extends Task<E> {
	private static final ImmutableMap<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_ATTRACT_TARGETS, MemoryModuleState.VALUE_PRESENT,
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
	protected boolean shouldRun(ServerWorld world, E tameable) {
		@Nullable Entity owner = tameable.getOwner();

		if (owner != null) {
			return !owner.isSpectator() && tameable.distanceTo(owner) <= this.moveStartDistance;
		}

		return false;
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected boolean shouldKeepRunning(ServerWorld world, E tameable, long time) {
		return this.shouldRun(world, tameable) && ((TaskAccessor<E>) this).callHasRequiredMemoryState(tameable);
	}

	@Override
	protected void run(ServerWorld world, E tameable, long time) {
		Brain<?> brain = tameable.getBrain();

		brain.forget(MemoryModuleType.WALK_TARGET);
		brain.forget(MemoryModuleType.LOOK_TARGET);
	}

	@Override
	protected void keepRunning(ServerWorld world, E tameable, long time) {
		Brain<?> brain = tameable.getBrain();

		@Nullable Entity owner = tameable.getOwner();
		if (owner == null) return;

		brain.getOptionalMemory(ModEntities.MEMORY_ATTRACT_TARGETS).ifPresent(
				list -> {
					for (LivingEntity living : list) {
						if (living instanceof MobEntity mob) {
							mob.setTarget(tameable);
						}
					}
				}
		);

		Optional<LivingEntity> guardFrom = brain.getOptionalMemory(ModEntities.MEMORY_GUARD_TARGET);

		if (guardFrom.isPresent()) {
			boolean shouldGuard = tameable.distanceTo(owner) <= this.guardStartDistance;
			brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(guardFrom.get(), true));

			// ご主人様の近くにいるとき
			if (tameable.getMainHandStack().isOf(Items.SHIELD) && shouldGuard) {
				if (!tameable.isBlocking()) {
					tameable.setCurrentHand(Hand.MAIN_HAND);
				}
			} else {
				tameable.clearActiveItem();
			}

			Vec3d guardFromPos = guardFrom.get().getPos();
			Vec3d guardPos;

			Vec3d ownerPos = owner.getPos();
			guardPos = ownerPos.add(guardFromPos.subtract(ownerPos).normalize());

			// ご主人様の正面に移動する
			Vec3d maidPos = tameable.getPos();
			Vec3f moveVec = new Vec3f(maidPos.subtract(guardPos));
			Vec3f lookVec = new Vec3f(tameable.getRotationVector().multiply(1.0D, 0.0D, 1.0D));

			Vec3f forwardVec = lookVec.copy();

			Quaternion quaternion = new Quaternion(Vec3f.POSITIVE_Y, -90.0F, true);
			lookVec.rotate(quaternion);
			Vec3f sidewaysVec = lookVec.copy();

			moveVec.normalize();
			float speed = tameable.isBlocking() ? this.guardSpeed : this.normalSpeed;
			float forwardSpeed = moveVec.dot(forwardVec) * speed;
			float sidewaysSpeed = moveVec.dot(sidewaysVec) * speed;

			tameable.getMoveControl().strafeTo(-forwardSpeed, sidewaysSpeed);
			tameable.setYaw(MathHelper.clampAngle(tameable.getYaw(), tameable.getHeadYaw(), 0.0f));
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, E tameable, long time) {
		tameable.clearActiveItem();
	}
}
