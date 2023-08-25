package io.github.zemelua.umu_little_maid.entity.brain.task.fish;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.api.BetterMultiTickTask;
import io.github.zemelua.umu_little_maid.entity.ModFishingBobberEntity;
import io.github.zemelua.umu_little_maid.entity.maid.ILittleMaidEntity;
import io.github.zemelua.umu_little_maid.mixin.AccessorMultiTickTask;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.event.GameEvent;

public class MaidFishTask<M extends LivingEntity & ILittleMaidEntity> extends BetterMultiTickTask<M> {
	private boolean hitFish;

	public MaidFishTask() {
		super(ImmutableMap.of(), 999);

		this.hitFish = false;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, M entity) {
		return true;
	}

	@Override
	protected void run(ServerWorld world, M maid, long time) {
		ItemStack fishingRod = maid.getMainHandStack();

		world.playSound(null, maid.getX(), maid.getY(), maid.getZ(), SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
		if (!world.isClient()) {
			int i = EnchantmentHelper.getLure(fishingRod);
			int j = EnchantmentHelper.getLuckOfTheSea(fishingRod);
			ModFishingBobberEntity fishHook = new ModFishingBobberEntity(world, maid, j, i);

			world.spawnEntity(fishHook);
			maid.onThrowFishHook(fishHook);
		}
		maid.emitGameEvent(GameEvent.ITEM_INTERACT_START);
		// setActoin Fish waiting
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, M maid, long time) {
		return true;
	}

	@Override
	protected void keepRunning(ServerWorld world, M maid, long time) {
		if (maid.isFishFighting() && !this.hitFish) {
			((AccessorMultiTickTask) this).setEndTime(time + 60L);
			this.hitFish = true;
		}

		if (((AccessorMultiTickTask) this).getEndTime() == time) {
			maid.pullFishRod();
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, M maid, long time) {
		maid.removeAction();
		this.hitFish = false;
	}
}
