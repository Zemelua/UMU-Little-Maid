package io.github.zemelua.umu_little_maid.entity.goal;

import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.PathAwareEntity;

import java.util.function.Predicate;

public class MaidTsundereAvoidGoal extends FleeEntityGoal {
	public MaidTsundereAvoidGoal(PathAwareEntity fleeingEntity, Class classToFleeFrom, float fleeDistance, double fleeSlowSpeed, double fleeFastSpeed, Predicate inclusionSelector) {
		super(fleeingEntity, classToFleeFrom, fleeDistance, fleeSlowSpeed, fleeFastSpeed, inclusionSelector);
	}
}
