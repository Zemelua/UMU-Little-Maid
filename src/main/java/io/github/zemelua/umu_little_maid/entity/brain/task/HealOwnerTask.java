package io.github.zemelua.umu_little_maid.entity.brain.task;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public class HealOwnerTask<E extends LivingEntity & Tameable> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of();

	private int cooldown;

	public HealOwnerTask() {
		super(HealOwnerTask.REQUIRED_MEMORIES);

		this.cooldown = 0;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E tameable) {
		@Nullable Entity owner = tameable.getOwner();

		if (owner == null) return false;
		if (tameable.distanceTo(owner) > 4.0D) return false;
		if (!(owner instanceof LivingEntity ownerLiving)) return false;

		return HealOwnerTask.shouldRecover(tameable, ownerLiving) || HealOwnerTask.shouldCure(tameable, ownerLiving);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
		return this.shouldRun(world, entity);
	}

	@Override
	protected void run(ServerWorld world, E tameable, long time) {
		Brain<?> brain = tameable.getBrain();
		@Nullable Entity owner = tameable.getOwner();

		brain.forget(MemoryModuleType.WALK_TARGET);
		if (owner != null) {
			brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(owner, true));
		}
	}

	@Override
	protected void keepRunning(ServerWorld world, E tameable, long time) {
		@Nullable Entity owner = tameable.getOwner();

		if (owner == null) return;
		if (!(owner instanceof LivingEntity ownerLiving)) return;

		if (ModUtils.hasHarmfulEffect(ownerLiving)) {
			Collection<StatusEffectInstance> effects = ownerLiving.getStatusEffects().stream()
					.filter(effect -> effect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL)
					.toList();

			//noinspection ForLoopReplaceableByForEach
			for (Iterator<StatusEffectInstance> iterator = effects.iterator(); iterator.hasNext();) {
				StatusEffectInstance effect = iterator.next();
				ownerLiving.removeStatusEffect(effect.getEffectType());
				tameable.addStatusEffect(effect);
			}
		}

		if (this.cooldown >= 30) {
			ownerLiving.heal(5.0F);
			tameable.damage(DamageSource.MAGIC, 3.0F);

			for (int i = 0; i < 5; i++) {
				double posX = tameable.getParticleX(1.0);
				double posY = tameable.getRandomBodyY() + 1.0;
				double posZ = tameable.getParticleZ(1.0);
				double deltaX = tameable.getRandom().nextGaussian() * 0.02;
				double deltaY = tameable.getRandom().nextGaussian() * 0.02;
				double deltaZ = tameable.getRandom().nextGaussian() * 0.02;

				((ServerWorld) tameable.getWorld()).spawnParticles(ParticleTypes.HAPPY_VILLAGER, posX, posY, posZ, 0, deltaX, deltaY, deltaZ, 1.0D);
			}

			this.cooldown = 0;
		}

		this.cooldown++;

		Brain<?> brain = tameable.getBrain();

		if (!brain.hasMemoryModule(MemoryModuleType.LOOK_TARGET)) {
			brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(owner, true));
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, E tameable, long time) {
		Brain<?> brain = tameable.getBrain();

		brain.forget(MemoryModuleType.LOOK_TARGET);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	public static <E extends PathAwareEntity & Tameable> boolean shouldHeal(E tameable) {
		@Nullable Entity owner = tameable.getOwner();

		if (owner == null) return false;
		if (!(owner instanceof LivingEntity ownerLiving)) return false;
		if (ownerLiving.isDead() || ownerLiving.isRemoved()) return false;
		if (ownerLiving.isSpectator()) return false;

		return HealOwnerTask.shouldRecover(tameable, ownerLiving) || HealOwnerTask.shouldCure(tameable, ownerLiving);
	}

	public static boolean shouldRecover(LivingEntity entity, LivingEntity owner) {
		return entity.getHealth() > 3.0D && owner.getHealth() < owner.getMaxHealth();
	}

	public static boolean shouldCure(LivingEntity entity, LivingEntity owner) {
		return entity.getHealth() > 3.0D && ModUtils.hasHarmfulEffect(owner);
	}
}
