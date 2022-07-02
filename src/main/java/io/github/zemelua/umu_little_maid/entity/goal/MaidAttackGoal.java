package io.github.zemelua.umu_little_maid.entity.goal;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;

import java.util.EnumSet;

public class MaidAttackGoal extends MeleeAttackGoal {
	public MaidAttackGoal(LittleMaidEntity mob) {
		super(mob, 1.0D, true);

		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
	}

	@Override
	public boolean canStart() {
		return ((LittleMaidEntity) this.mob).getJob().isActive()
				|| ((LittleMaidEntity) this.mob).getJob().isMad()
				|| super.canStart();
	}
}
