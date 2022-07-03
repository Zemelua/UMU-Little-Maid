package io.github.zemelua.umu_little_maid.entity.goal;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MaidSitGoal extends Goal {
	private final LittleMaidEntity maid;

	public MaidSitGoal(LittleMaidEntity maid) {
		this.maid = maid;
		this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
	}

	@Override
	public boolean canStart() {
		if (!this.maid.isTamed()) return false;
		if (this.maid.isInsideWaterOrBubbleColumn()) return false;
		if (!this.maid.isOnGround()) return false;

		Entity owner = this.maid.getOwner();
		if (owner == null) return true;

		if (owner instanceof LivingEntity ownerLiving) {
			if (this.maid.distanceTo(owner) >= 12.0F && ownerLiving.getAttacker() == null) return false;
		}

		return this.maid.isSitting();
	}

	@Override
	public boolean shouldContinue() {
		return this.maid.isSitting();
	}

	@Override
	public void start() {
		this.maid.getNavigation().stop();
	}

	@Override
	public void stop() {
		this.maid.setSitting(false);
	}
}
