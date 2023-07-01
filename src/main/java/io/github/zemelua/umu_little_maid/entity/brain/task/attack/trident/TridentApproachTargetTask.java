package io.github.zemelua.umu_little_maid.entity.brain.task.attack.trident;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.ai.brain.task.SingleTickTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.MobEntity;

public class TridentApproachTargetTask {
	public static Task<MobEntity> create(float speed) {
		return TaskTriggerer.runIf(TridentApproachTargetTask::shouldApproach, (SingleTickTask<? super MobEntity>) RangedApproachTask.create(entity -> speed));
	}

	public static boolean shouldApproach(LivingEntity living) {
		return !ThrowTridentTask.shouldThrow(living.getMainHandStack()) && !RiptideTridentTask.shouldRiptide(living, living.getMainHandStack());
	}
}
