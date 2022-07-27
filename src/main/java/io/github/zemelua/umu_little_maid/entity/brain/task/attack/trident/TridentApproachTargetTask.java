package io.github.zemelua.umu_little_maid.entity.brain.task.attack.trident;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.RangedApproachTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class TridentApproachTargetTask extends RangedApproachTask {
	public TridentApproachTargetTask(float speed) {
		super(speed);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, MobEntity mob) {
		return super.shouldRun(world, mob) && TridentApproachTargetTask.shouldApproach(mob, mob.getMainHandStack());
	}

	public static boolean shouldApproach(LivingEntity living, ItemStack tridentStack) {
		return !ThrowTridentTask.shouldThrow(tridentStack) && !RiptideTridentTask.shouldRiptide(living, tridentStack);
	}
}
