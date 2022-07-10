package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import java.util.Optional;
import java.util.UUID;

public class MaidGuardTask extends Task<LittleMaidEntity> {
	private static final ImmutableMap<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			ModEntities.MEMORY_ATTRACT_TARGETS, MemoryModuleState.VALUE_PRESENT,
			ModEntities.MEMORY_GUARD_FROM_TARGET, MemoryModuleState.VALUE_PRESENT
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
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		return brain.getOptionalMemory(ModEntities.MEMORY_ATTRACT_TARGETS).isPresent()
				&& brain.getOptionalMemory(ModEntities.MEMORY_GUARD_FROM_TARGET).isPresent()
				&& this.shouldMove(world, maid);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		Optional<UUID> ownerUUID = brain.getOptionalMemory(ModEntities.MEMORY_OWNER);
		if (ownerUUID.isEmpty()) return false;
		PlayerEntity owner = world.getPlayerByUuid(ownerUUID.get());
		if (owner == null) return false;

		// return this.shouldRun(world, maid) && maid.squaredDistanceTo(owner) <= 3.0D;
		return this.shouldRun(world, maid);
	}

	@Override
	protected void keepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		Brain<LittleMaidEntity> brain = maid.getBrain();

		Optional<UUID> ownerUUID = brain.getOptionalMemory(ModEntities.MEMORY_OWNER);
		if (ownerUUID.isEmpty()) return;
		PlayerEntity owner = world.getPlayerByUuid(ownerUUID.get());
		if (owner == null) return;

		brain.getOptionalMemory(ModEntities.MEMORY_ATTRACT_TARGETS).ifPresent(
				list -> list.forEach(living -> {
					if (living instanceof MobEntity mob) {
						mob.setTarget(maid);
					}
				})
		);

		Optional<LivingEntity> guardFrom = brain.getOptionalMemory(ModEntities.MEMORY_GUARD_FROM_TARGET);

		if (guardFrom.isPresent()) {
			boolean shouldGuard = maid.distanceTo(owner) <= this.guardStartDistance;
			brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(guardFrom.get(), true));

			// ご主人様の近くにいるとき
			if (maid.getMainHandStack().isOf(Items.SHIELD) && shouldGuard) {
				if (!maid.isBlocking()) {
					maid.setCurrentHand(Hand.MAIN_HAND);
				}
			} else {
				maid.clearActiveItem();
			}

			Vec3d guardFromPos = guardFrom.get().getPos();
			Vec3d guardPos;

			Vec3d ownerPos = owner.getPos();
			guardPos = ownerPos.add(guardFromPos.subtract(ownerPos).normalize());

			// ご主人様の正面に移動する
			Vec3d maidPos = maid.getPos();
			Vec3f moveVec = new Vec3f(maidPos.subtract(guardPos));
			Vec3f lookVec = new Vec3f(maid.getRotationVector().multiply(1.0D, 0.0D, 1.0D));

			Vec3f forwardVec = lookVec.copy();

			Quaternion quaternion = new Quaternion(Vec3f.POSITIVE_Y, -90.0F, true);
			lookVec.rotate(quaternion);
			Vec3f sidewaysVec = lookVec.copy();

			moveVec.normalize();
			float speed = maid.isBlocking() ? this.guardSpeed : this.normalSpeed;
			float forwardSpeed = moveVec.dot(forwardVec) * speed;
			float sidewaysSpeed = moveVec.dot(sidewaysVec) * speed;

			maid.getMoveControl().strafeTo(-forwardSpeed, sidewaysSpeed);
			maid.setYaw(MathHelper.clampAngle(maid.getYaw(), maid.getHeadYaw(), 0.0f));
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		maid.clearActiveItem();
	}

	private boolean shouldMove(ServerWorld world, LittleMaidEntity maid) {
		Brain<LittleMaidEntity> brain = maid.getBrain();
		Optional<UUID> ownerUUID = brain.getOptionalMemory(ModEntities.MEMORY_OWNER);
		if (ownerUUID.isEmpty()) return false;

		PlayerEntity owner = world.getPlayerByUuid(ownerUUID.get());

		if (owner != null) {
			return !owner.isSpectator() && maid.distanceTo(owner) <= this.moveStartDistance;
		}

		return false;
	}
}
