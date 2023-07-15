package io.github.zemelua.umu_little_maid.entity.brain.task.heal;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.action.EMaidAction;
import io.github.zemelua.umu_little_maid.mixin.AccessorMultiTickTask;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Optional;

public class HealMasterTask extends MultiTickTask<LittleMaidEntity> {
	public HealMasterTask() {
		super(ImmutableMap.of(), 60);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		return maid.getMaster()
				.map(m -> maid.distanceTo(m) < 2.0D)
				.orElse(false);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		maid.setActionE(EMaidAction.HEALING);

		Brain<?> brain = maid.getBrain();
		brain.forget(MemoryModuleType.WALK_TARGET);
		maid.getMaster().ifPresent(player -> brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(player, true)));
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		return maid.getMaster()
				.map(m -> maid.distanceTo(m) < 4.0D && HealOwnerTask.shouldHeal(maid))
				.orElse(false);
	}

	@Override
	protected void keepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		Optional<PlayerEntity> master = maid.getMaster();
		if (master.isEmpty()) return;

		if ((60L - (((AccessorMultiTickTask) this).getEndTime() - time)) % 30L == 0L) {
			if (ModUtils.hasHarmfulEffect(master.get()) && maid.getHealth() >= 3.0D) {
				List<StatusEffectInstance> effects = master.get().getStatusEffects().stream()
						.filter(effect -> effect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL)
						.toList();

				StatusEffectInstance effectToCure = effects.get(maid.getRandom().nextInt(effects.size()));
				master.get().removeStatusEffect(effectToCure.getEffectType());
				maid.addStatusEffect(effectToCure);
			}
		}

		if ((60L - (((AccessorMultiTickTask) this).getEndTime() - time)) % 20L == 0L) {
			master.get().heal(6.0F);
			maid.damage(maid.getDamageSources().magic(), 3.0F);

			for (int i = 0; i < 5; i++) {
				double posX = maid.getParticleX(1.0);
				double posY = maid.getRandomBodyY() + 1.0;
				double posZ = maid.getParticleZ(1.0);
				double deltaX = maid.getRandom().nextGaussian() * 0.02;
				double deltaY = maid.getRandom().nextGaussian() * 0.02;
				double deltaZ = maid.getRandom().nextGaussian() * 0.02;

				((ServerWorld) maid.getWorld()).spawnParticles(ParticleTypes.HAPPY_VILLAGER, posX, posY, posZ, 0, deltaX, deltaY, deltaZ, 1.0D);
			}
		}

		maid.setBodyYaw(maid.getHeadYaw());

		Brain<LittleMaidEntity> brain = maid.getBrain();
		if (!brain.hasMemoryModule(MemoryModuleType.LOOK_TARGET)) {
			brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(master.get(), true));
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		maid.removeActionE();

		Brain<LittleMaidEntity> brain = maid.getBrain();
		brain.forget(MemoryModuleType.LOOK_TARGET);
	}
}
