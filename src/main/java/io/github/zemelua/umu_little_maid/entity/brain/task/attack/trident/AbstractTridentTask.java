package io.github.zemelua.umu_little_maid.entity.brain.task.attack.trident;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.mixin.TaskAccessor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

import java.util.Map;

public abstract class AbstractTridentTask<E extends MobEntity> extends Task<E> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT,
			MemoryModuleType.ATTACK_COOLING_DOWN, MemoryModuleState.VALUE_ABSENT
	);

	private final double range;
	private final double speed;
	private final int interval;

	private int combatTicks;
	private boolean movingToLeft;
	private boolean backward;

	public AbstractTridentTask(double range, double speed, int interval) {
		super(AbstractTridentTask.REQUIRED_MEMORIES);

		this.range = range;
		this.speed = speed;
		this.interval = interval;
		this.combatTicks = -1;
		this.movingToLeft = true;
		this.backward = true;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E mob) {
		Brain<?> brain = mob.getBrain();

		return brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET)
				.filter(target -> !mob.isInAttackRange(target))
				.isPresent();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected boolean shouldKeepRunning(ServerWorld world, E mob, long time) {
		UMULittleMaid.LOGGER.info(((TaskAccessor<E>) this).callHasRequiredMemoryState(mob));

		Brain<?> brain = mob.getBrain();

		return brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET)
				.filter(target -> mob.getVisibilityCache().canSee(target))
				.isPresent() && ((TaskAccessor<E>) this).callHasRequiredMemoryState(mob);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	protected void run(ServerWorld world, E mob, long time) {
		Brain<?> brain = mob.getBrain();

		brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(targetObject -> {
			brain.remember(MemoryModuleType.WALK_TARGET, new WalkTarget(targetObject, (float) this.speed, (int) this.range));
			brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(targetObject, true));
		});

		mob.setAttacking(true);
		mob.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(mob, Items.TRIDENT));
	}

	@Override
	protected void keepRunning(ServerWorld world, E mob, long time) {
		Brain<?> brain = mob.getBrain();

		brain.getOptionalMemory(MemoryModuleType.ATTACK_TARGET).ifPresent(targetObject -> {
			double distance = mob.distanceTo(targetObject);

			if (distance > this.range) {
				this.combatTicks = -1;
			} else {
				this.combatTicks++;
			}

			if (this.combatTicks >= 20) {
				if (mob.getRandom().nextDouble() < 0.3D) {
					this.movingToLeft = !this.movingToLeft;
				}
				if (mob.getRandom().nextDouble() < 0.3D) {
					this.backward = !this.backward;
				}

				this.combatTicks = 0;
			}

			if (this.combatTicks > -1) {
				if (distance > this.range * Math.sqrt(0.75D)) {
					this.backward = false;
				} else if (distance < this.range * Math.sqrt(0.25D)) {
					this.backward = true;
				}

				mob.getMoveControl().strafeTo(this.backward ? -0.5F : 0.5F, this.movingToLeft ? 0.5F : -0.5F);
				mob.setYaw(MathHelper.clampAngle(mob.getYaw(), mob.getHeadYaw(), 0.0f));
			}

			brain.remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(targetObject, true));

			if (mob.isUsingItem()) {
				int itemUseTime = mob.getItemUseTime();

				if (itemUseTime >= 20) {
					this.attack(world, mob, itemUseTime, targetObject);
					brain.remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, this.interval);
				}
			}
		});
	}

	@Override
	protected void finishRunning(ServerWorld world, E mob, long time) {
		mob.setAttacking(false);
		mob.clearActiveItem();
	}

	protected abstract void attack(ServerWorld world, E mob, int itemUseTime, LivingEntity target);
}
