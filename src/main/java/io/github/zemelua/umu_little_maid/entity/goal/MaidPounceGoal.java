package io.github.zemelua.umu_little_maid.entity.goal;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;

import java.util.EnumSet;

public class MaidPounceGoal extends PounceAtTargetGoal {
	private final LittleMaidEntity maid;

	public MaidPounceGoal(LittleMaidEntity maid) {
		super(maid, 0.4F);

		this.maid = maid;
		this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
	}

	@Override
	public boolean canStart() {
		return this.maid.getPersonality().canPounceAtTarget(this.maid)
				&& this.maid.getJob().canPounceAtTarget()
				&& super.canStart();
	}
}
