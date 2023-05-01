package io.github.zemelua.umu_little_maid.entity.brain.task.attack.crossbow;

import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.brain.task.attack.bow.RememberHasArrowsTask;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;

public class HunterRememberHasArrowsTask<E extends LivingEntity> extends RememberHasArrowsTask<E> {
	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();

		if (hasArrow(living)) {
			brain.remember(ModMemories.HAS_ARROWS, Unit.INSTANCE);
		}
	}

	public static <E extends LivingEntity> boolean hasArrow(E living) {
		ItemStack crossbow = living.getStackInHand(ProjectileUtil.getHandPossiblyHolding(living, Items.CROSSBOW));
		boolean hasArrowInInventory = !living.getArrowType(living.getMainHandStack()).isEmpty();
		boolean hasArrowOnCrossbow = CrossbowItem.isCharged(crossbow);

		return hasArrowInInventory || hasArrowOnCrossbow;
	}
}
