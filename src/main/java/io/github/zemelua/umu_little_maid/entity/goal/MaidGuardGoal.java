package io.github.zemelua.umu_little_maid.entity.goal;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.mixin.MoveControlAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class MaidGuardGoal extends Goal {
	private static final double GUARD_DISTANCE = 6.0D;
	private static final double STOP_DISTANCE = 3.0D;
	private static final float NORMAL_SPEED = 1.5F;
	private static final float GUARD_SPEED = 0.7F;

	private final LittleMaidEntity maid;

	public MaidGuardGoal(LittleMaidEntity maid) {
		this.maid = maid;

		this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
	}

	@Override
	public boolean canStart() {
		this.maid.setGuardFrom(this.getNearestGuardFrom());
		return this.maid.getGuardFrom() != null;
	}

	@Override
	public boolean shouldContinue() {
		Entity owner = this.maid.getOwner();
		if (owner != null && this.maid.squaredDistanceTo(owner) > MaidGuardGoal.STOP_DISTANCE) return false;

		return this.maid.getGuardFrom() != null;
	}

	@Override
	public void tick() {
		this.getGuardFrom().forEach(living -> {
			if (living instanceof MobEntity mob) {
				mob.setTarget(this.maid);
			}
		});

		this.maid.setGuardFrom(this.getNearestGuardFrom());
		Entity owner = this.maid.getOwner();

		if (this.maid.getGuardFrom() != null) {
			boolean shouldGuard = this.maid.distanceTo(owner) < 3.0F;
			this.maid.getLookControl().lookAt(this.maid.getGuardFrom());

			// ご主人様の近くにいるとき
			if (this.maid.getMainHandStack().isOf(Items.SHIELD) && shouldGuard) {
				if (!this.maid.isBlocking()) {
					this.maid.setCurrentHand(Hand.MAIN_HAND);
				}
			} else {
				this.maid.clearActiveItem();
			}

			Vec3d guardFromPos = this.maid.getGuardFrom().getPos();
			Vec3d guardPos;

			if (owner != null) {
				Vec3d ownerPos = owner.getPos();
				guardPos = ownerPos.add(guardFromPos.subtract(ownerPos).normalize());
			} else {
				guardPos = NoPenaltyTargeting.findFrom(this.maid, 16, 7, guardFromPos);
			}

			// ご主人様の正面に移動する
			Vec3d maidPos = this.maid.getPos();
			Vec3f moveVec = new Vec3f(maidPos.subtract(guardPos));
			Vec3f lookVec = new Vec3f(this.maid.getRotationVector().multiply(1.0D, 0.0D, 1.0D));

			Vec3f forwardVec = lookVec.copy();

			Quaternion quaternion = new Quaternion(Vec3f.POSITIVE_Y, -90.0F, true);
			lookVec.rotate(quaternion);
			Vec3f sidewaysVec = lookVec.copy();

			moveVec.normalize();
			float speed = this.maid.isBlocking() ? MaidGuardGoal.GUARD_SPEED : MaidGuardGoal.NORMAL_SPEED;
			float forwardSpeed = moveVec.dot(forwardVec) * speed;
			float sidewaysSpeed = moveVec.dot(sidewaysVec) * speed;

			this.maid.getMoveControl().strafeTo(-forwardSpeed, sidewaysSpeed);
			((MoveControlAccessor) this.maid.getMoveControl()).setSpeed(0.7D);
		}
	}

	@Override
	public void stop() {
		this.maid.setGuardFromNull();
	}

	private List<LivingEntity> getGuardFrom() {
		return this.maid.getWorld().getEntitiesByClass(
				LivingEntity.class, this.maid.getBoundingBox().expand(6.0F, 3.0F, 6.0F), living -> {
					Entity owner = this.maid.getOwner();
					if (living instanceof MobEntity mob) {
						LivingEntity target = mob.getTarget();
						if (target != null && (target.equals(owner) || target.equals(this.maid))) return true;
					}
					if (owner instanceof LivingEntity ownerLiving) return living.equals(ownerLiving.getAttacker());

					return false;
				});
	}

	@Nullable
	private LivingEntity getNearestGuardFrom() {
		return this.maid.getWorld().getClosestEntity(
				this.getGuardFrom(),
				TargetPredicate.createAttackable().setBaseMaxDistance(MaidGuardGoal.GUARD_DISTANCE),
				this.maid, this.maid.getX(), this.maid.getY(), this.maid.getZ()
		);
	}
}
