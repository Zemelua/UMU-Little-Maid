package io.github.zemelua.umu_little_maid.entity.brain.task.heal;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.util.IHasMaster;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class HealOwnerTask<E extends LivingEntity & IHasMaster> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of();

	protected int cooldown;

	public HealOwnerTask() {
		super(HealOwnerTask.REQUIRED_MEMORIES);

		this.cooldown = 0;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E living) {
		Optional<PlayerEntity> master = living.getMaster();

		if (master.isEmpty()) return false;
		if (living.distanceTo(master.get()) > 4.0D) return false;

		return HealOwnerTask.shouldRecover(living, master.get()) || HealOwnerTask.shouldCure(living, master.get());
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
		return this.shouldRun(world, entity);
	}

	@Override
	protected void run(ServerWorld world, E living, long time) {
		Brain<?> brain = living.getBrain();
		Optional<PlayerEntity> master = living.getMaster();

		brain.forget(MemoryModuleType.WALK_TARGET);
		master.ifPresent(player -> brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(player, true)));
	}

	@Override
	protected void keepRunning(ServerWorld world, E living, long time) {
		Optional<PlayerEntity> master = living.getMaster();

		if (master.isEmpty()) return;

		if (ModUtils.hasHarmfulEffect(master.get())) {
			Collection<StatusEffectInstance> effects = master.get().getStatusEffects().stream()
					.filter(effect -> effect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL)
					.toList();

			//noinspection ForLoopReplaceableByForEach
			for (Iterator<StatusEffectInstance> iterator = effects.iterator(); iterator.hasNext();) {
				StatusEffectInstance effect = iterator.next();
				master.get().removeStatusEffect(effect.getEffectType());
				living.addStatusEffect(effect);
			}
		}

		if (this.cooldown >= 30) {
			master.get().heal(5.0F);
			living.damage(DamageSource.MAGIC, 3.0F);

			for (int i = 0; i < 5; i++) {
				double posX = living.getParticleX(1.0);
				double posY = living.getRandomBodyY() + 1.0;
				double posZ = living.getParticleZ(1.0);
				double deltaX = living.getRandom().nextGaussian() * 0.02;
				double deltaY = living.getRandom().nextGaussian() * 0.02;
				double deltaZ = living.getRandom().nextGaussian() * 0.02;

				((ServerWorld) living.getWorld()).spawnParticles(ParticleTypes.HAPPY_VILLAGER, posX, posY, posZ, 0, deltaX, deltaY, deltaZ, 1.0D);
			}

			this.cooldown = 0;
		}

		living.setBodyYaw(living.getHeadYaw());
		this.cooldown++;

		Brain<?> brain = living.getBrain();

		if (!brain.hasMemoryModule(MemoryModuleType.LOOK_TARGET)) {
			brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(master.get(), true));
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

	public static <E extends LivingEntity & IHasMaster> boolean shouldHeal(E living) {
		Optional<PlayerEntity> master = living.getMaster();

		if (master.isEmpty()) return false;
		if (master.get().isDead() || master.get().isRemoved()) return false;
		if (master.get().isSpectator()) return false;

		return HealOwnerTask.shouldRecover(living, master.get()) || HealOwnerTask.shouldCure(living, master.get());
	}

	public static boolean shouldRecover(LivingEntity entity, LivingEntity owner) {
		return entity.getHealth() > 3.0D && owner.getHealth() < owner.getMaxHealth();
	}

	public static boolean shouldCure(LivingEntity entity, LivingEntity owner) {
		return entity.getHealth() > 3.0D && ModUtils.hasHarmfulEffect(owner);
	}
}
