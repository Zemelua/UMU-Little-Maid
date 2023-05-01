package io.github.zemelua.umu_little_maid.entity.brain.task.attack.crossbow;

import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.mixin.CrossbowAttackTaskAccessor;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.task.CrossbowAttackTask;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

import static net.minecraft.entity.ai.brain.task.CrossbowAttackTask.CrossbowState.*;

public class SucceedCrossbowAttackTask<E extends MobEntity & CrossbowUser, UNUSED extends LivingEntity> extends CrossbowAttackTask<E, UNUSED> {
	@Override
	protected boolean shouldRun(ServerWorld world, E living) {
		Brain<?> brain = living.getBrain();

		return brain.hasMemoryModule(ModMemories.HAS_ARROWS) && super.shouldRun(world, living);
	}

	@Override
	protected void run(ServerWorld world, E mob, long time) {
		ItemStack crossbow = mob.getStackInHand(ProjectileUtil.getHandPossiblyHolding(mob, Items.CROSSBOW));

		if (CrossbowItem.isCharged(crossbow)) {
			((CrossbowAttackTaskAccessor) this).setState(CHARGED);
			((CrossbowAttackTaskAccessor) this).setChargingCooldown(10);
		}
	}
}
