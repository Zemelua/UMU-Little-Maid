package io.github.zemelua.umu_little_maid.entity.brain.task.attack.trident;

import io.github.zemelua.umu_little_maid.util.IPoseidonMob;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public class ThrowTridentTask<E extends MobEntity & IPoseidonMob> extends AbstractTridentTask<E> {
	public ThrowTridentTask(double range, double speed, int interval) {
		super(range, speed, interval);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E ranged) {
		return super.shouldRun(world, ranged) && ThrowTridentTask.shouldThrow(ranged.getMainHandStack());
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E ranged, long time) {
		return super.shouldKeepRunning(world, ranged, time) && ThrowTridentTask.shouldThrow(ranged.getMainHandStack());
	}

	@Override
	protected void attack(ServerWorld world, E mob, int itemUseTime, LivingEntity target) {
		mob.throwTrident(target);
	}

	public static boolean shouldThrow(ItemStack tridentStack) {
		return EnchantmentHelper.getRiptide(tridentStack) <= 0;
	}
}
