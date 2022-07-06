package io.github.zemelua.umu_little_maid.entity.goal;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;

import java.util.EnumSet;

public class MaidBowAttackGoal extends Goal {
	private static final double RANGE = 15.0D;
	private static final double SPEED = 1.0D;
	private static final int INTERVAL = 20;

	private final LittleMaidEntity maid;

	private int targetSeeingTicker;
	private int coolDown = -1;
	private int combatTicks = -1;
	private boolean movingToLeft;
	private boolean backward;

	public MaidBowAttackGoal(LittleMaidEntity maid) {
		this.maid = maid;

		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
	}

	@Override
	public boolean canStart() {
		return this.maid.getTarget() != null
				&& !this.maid.getArrowType(this.maid.getMainHandStack()).isEmpty();
	}

	@Override
	public boolean shouldContinue() {
		return (this.canStart() || !this.maid.getNavigation().isIdle())
				&& !this.maid.getArrowType(this.maid.getMainHandStack()).isEmpty();
	}

	@Override
	public void start() {
		this.maid.setAttacking(true);
	}

	@Override
	public void stop() {
		this.maid.setAttacking(false);
		this.targetSeeingTicker = 0;
		this.coolDown = -1;
		this.maid.clearActiveItem();
	}

	@Override
	public boolean shouldRunEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		LivingEntity target = this.maid.getTarget();
		if (target == null) return;

		double distance = this.maid.distanceTo(target);
		boolean canSeeTarget = this.maid.getVisibilityCache().canSee(target);
		boolean isSeeingTarget = this.targetSeeingTicker > 0;

		if (canSeeTarget != isSeeingTarget) this.targetSeeingTicker = 0;
		if (canSeeTarget) this.targetSeeingTicker++;
		else              this.targetSeeingTicker--;

		if (distance > MaidBowAttackGoal.RANGE || this.targetSeeingTicker < 20) {
			this.maid.getNavigation().startMovingTo(target, MaidBowAttackGoal.SPEED);
			this.combatTicks = -1;
		} else {
			this.maid.getNavigation().stop();
			this.combatTicks++;
		}

		if (this.combatTicks >= 20) {
			if (this.maid.getRandom().nextDouble() < 0.3D) {
				this.movingToLeft = !this.movingToLeft;
			}
			if (this.maid.getRandom().nextDouble() < 0.3D) {
				this.backward = !this.backward;
			}

			this.combatTicks = 0;
		}

		if (this.combatTicks > -1) {
			if      (distance > MaidBowAttackGoal.RANGE * Math.sqrt(0.75D)) this.backward = false;
			else if (distance < MaidBowAttackGoal.RANGE * Math.sqrt(0.25D)) this.backward = true;

			this.maid.getMoveControl().strafeTo(this.backward ? -0.5F : 0.5F, this.movingToLeft ? 0.5F : -0.5F);
			this.maid.lookAtEntity(target, 30.0F, 30.0F);
		} else {
			this.maid.getLookControl().lookAt(target, 30.0F, 30.0F);
		}

		if (this.maid.isUsingItem()) {
			int itemUseTime = this.maid.getItemUseTime();

			if (!canSeeTarget && this.targetSeeingTicker < -60) {
				this.maid.clearActiveItem();
			} else if (canSeeTarget && itemUseTime >= 20) {
				this.maid.clearActiveItem();
				this.maid.attack(target, BowItem.getPullProgress(itemUseTime));
				this.coolDown = MaidBowAttackGoal.INTERVAL;
			}
		} else if (--this.coolDown <= 0 && this.targetSeeingTicker >= -60) {
			this.maid.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(this.maid, Items.BOW));
		}
	}
}
