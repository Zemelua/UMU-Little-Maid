package io.github.zemelua.umu_little_maid.entity.brain.task.guard;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.util.IHasMaster;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.Optional;

public class MaidGuardTask<E extends MobEntity & IHasMaster> extends MultiTickTask<E> {
	private static final ImmutableMap<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModMemories.GUARD_AGAINST, MemoryModuleState.VALUE_PRESENT
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
		return this.shouldRun(world, mob) && this.hasRequiredMemoryState(mob);
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

		brain.getOptionalMemory(ModMemories.ATTRACTABLE_LIVINGS).ifPresent(list -> list.stream()
				.filter(living -> living instanceof MobEntity)
				.map(living -> (MobEntity) living)
				.forEach(target -> target.setTarget(mob))
		);

		Optional<LivingEntity> guardTarget = brain.getOptionalMemory(ModMemories.GUARD_AGAINST);

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
			Vector3f moveVec = maidPos.subtract(guardPos).toVector3f();
			Vector3f lookVec = mob.getRotationVector().multiply(1.0D, 0.0D, 1.0D).toVector3f();

			Vector3f forwardVec = new Vector3f(lookVec);

			Quaternionfc quaternion = RotationAxis.POSITIVE_Y.rotationDegrees(-90);
			quaternion.transform(lookVec);
			Vector3f sidewaysVec = new Vector3f(lookVec);

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
