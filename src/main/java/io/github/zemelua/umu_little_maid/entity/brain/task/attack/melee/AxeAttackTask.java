package io.github.zemelua.umu_little_maid.entity.brain.task.attack.melee;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.api.BetterMultiTickTask;
import io.github.zemelua.umu_little_maid.entity.maid.AbstractLittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.action.MaidAction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public class AxeAttackTask extends BetterMultiTickTask<AbstractLittleMaidEntity> {
	public AxeAttackTask() {
		super(ImmutableMap.of(), 35);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, AbstractLittleMaidEntity maid) {
		if (!maid.canAction()) return false;

		LivingEntity target = maid.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).orElseThrow();
		return LookTargetUtil.isVisibleInMemory(maid, target) && maid.isInAttackRange(target);
	}

	@Override
	protected void run(ServerWorld world, AbstractLittleMaidEntity maid, long time) {
		maid.setAction(MaidAction.AXE_ATTACKING);
		maid.setTarget(maid.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).orElseThrow());
		maid.setAttacking(true);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, AbstractLittleMaidEntity maid, long time) {
		if (!maid.isAttacking()) return false;

		if (this.getPassedTicks(time) < 25L) {
			Optional<LivingEntity> target = Optional.ofNullable(maid.getTarget());
			return target.filter(t -> LookTargetUtil.isVisibleInMemory(maid, t) && maid.isInAttackRange(t))
					.isPresent();
		} else {
			return true;
		}
	}

	@Override
	protected void keepRunning(ServerWorld world, AbstractLittleMaidEntity maid, long time) {
		LivingEntity target = maid.getTargetOptional().orElseThrow();

		if (this.getPassedTicks(time) == 25L) {
			maid.tryAttack(target);
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, AbstractLittleMaidEntity maid, long time) {
		maid.removeAction();
		maid.setTarget(null);
		maid.setAttacking(false);
	}
}
