package io.github.zemelua.umu_little_maid.entity.brain.task.wander;

import io.github.zemelua.umu_little_maid.util.IAvoidRain;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.task.StrollTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class AvoidRainStrollTask extends StrollTask {
	public AvoidRainStrollTask(float speed) {
		super(speed);
	}

	@Nullable
	@Override
	protected Vec3d findWalkTarget(PathAwareEntity mob) {
		if (mob.isBeingRainedOn() && mob instanceof IAvoidRain avoidRain && avoidRain.shouldAvoidRain()) {
			return FuzzyTargeting.find(mob, this.horizontalRadius, this.verticalRadius, posValue -> !mob.getWorld().hasRain(posValue)
					? mob.getPathfindingFavor(posValue) + 99.0F
					: mob.getPathfindingFavor(posValue));
		}

		return super.findWalkTarget(mob);
	}
}
