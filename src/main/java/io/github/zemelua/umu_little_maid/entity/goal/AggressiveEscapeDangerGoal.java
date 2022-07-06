package io.github.zemelua.umu_little_maid.entity.goal;

import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.mob.PathAwareEntity;

public class AggressiveEscapeDangerGoal extends EscapeDangerGoal {
	public AggressiveEscapeDangerGoal(PathAwareEntity mob, double speed) {
		super(mob, speed);
	}

	@Override
	protected boolean isInDanger() {
		return this.mob.shouldEscapePowderSnow() || this.mob.isOnFire();
	}
}
