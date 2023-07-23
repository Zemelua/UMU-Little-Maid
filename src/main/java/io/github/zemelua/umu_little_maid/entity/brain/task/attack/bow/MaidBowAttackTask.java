package io.github.zemelua.umu_little_maid.entity.brain.task.attack.bow;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

import java.util.Map;
import java.util.Optional;

public class MaidBowAttackTask extends MultiTickTask<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(
			MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT
	);

	private final double range;
	private final float speed;
	private final int interval;

	private int targetSeeingTicker;
	private int combatTicks = -1;
	private boolean movingToLeft;
	private boolean backward;

	public MaidBowAttackTask(double range, float speed, int interval) {
		super(MaidBowAttackTask.REQUIRED_MEMORIES);

		this.range = range;
		this.speed = speed;
		this.interval = interval;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		return MaidBowAttackTask.hasTarget(maid) && MaidBowAttackTask.hasArrows(maid);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		return MaidBowAttackTask.hasTarget(maid) && MaidBowAttackTask.hasArrows(maid);
	}

	@Override
	protected boolean isTimeLimitExceeded(long time) {
		return false;
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		maid.setAttacking(true);
		this.targetSeeingTicker = 0;
	}

	@Override
	protected void keepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		Optional<LivingEntity> target = maid.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET);
		if (target.isEmpty()) return;

		double distance = maid.distanceTo(target.get());
		boolean canSeeTarget = maid.getVisibilityCache().canSee(target.get());
		boolean isSeeingTarget = this.targetSeeingTicker > 0;

		if (canSeeTarget != isSeeingTarget) this.targetSeeingTicker = 0;
		if (canSeeTarget) this.targetSeeingTicker++;
		else              this.targetSeeingTicker--;

		if (distance > this.range || this.targetSeeingTicker < 20) {
			maid.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(target.get(), this.speed, 0));
			this.combatTicks = -1;
		} else {
			maid.getNavigation().stop();
			this.combatTicks++;
		}

		if (this.combatTicks >= 20) {
			if (maid.getRandom().nextDouble() < 0.3D) {
				this.movingToLeft = !this.movingToLeft;
			}
			if (maid.getRandom().nextDouble() < 0.3D) {
				this.backward = !this.backward;
			}

			this.combatTicks = 0;
		}

		if (this.combatTicks > -1) {
			if      (distance > this.range * Math.sqrt(0.75D)) this.backward = false;
			else if (distance < this.range * Math.sqrt(0.25D)) this.backward = true;

			maid.getMoveControl().strafeTo(this.backward ? -0.5F : 0.5F, this.movingToLeft ? 0.5F : -0.5F);
			maid.setYaw(MathHelper.clampAngle(maid.getYaw(), maid.getHeadYaw(), 0.0f));
		}

		maid.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(target.get(), true));

		if (maid.isUsingItem()) {
			int itemUseTime = maid.getItemUseTime();

			if (!canSeeTarget && this.targetSeeingTicker < -60) {
				maid.clearActiveItem();
			} else if (canSeeTarget && itemUseTime >= 20) {
				maid.clearActiveItem();
				maid.attack(target.get(), BowItem.getPullProgress(itemUseTime));
				maid.getBrain().remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, this.interval);
			}
		} else if (!maid.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_COOLING_DOWN) && this.targetSeeingTicker >= -60) {
			maid.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(maid, Items.BOW));
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		maid.setAttacking(false);
		this.targetSeeingTicker = 0;
		maid.clearActiveItem();
	}

	private static boolean hasTarget(LittleMaidEntity maid) {
		return maid.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).isPresent();
	}

	private static boolean hasArrows(LittleMaidEntity maid) {
		return !maid.getProjectileType(maid.getMainHandStack()).isEmpty();
	}
}
