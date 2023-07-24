package io.github.zemelua.umu_little_maid.entity.brain.task.attack.melee;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.api.BetterMultiTickTask;
import io.github.zemelua.umu_little_maid.entity.maid.AbstractLittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.action.MaidAction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.server.world.ServerWorld;

import java.util.Optional;

public class SwordAttackTask extends BetterMultiTickTask<AbstractLittleMaidEntity> {
	public SwordAttackTask() {
		super(ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT), 35);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, AbstractLittleMaidEntity maid) {
		if (!maid.canAction()) return false;

		LivingEntity target = maid.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).orElseThrow();
		return LookTargetUtil.isVisibleInMemory(maid, target) && maid.isInAttackRange(target);
	}

	@Override
	protected void run(ServerWorld world, AbstractLittleMaidEntity maid, long time) {
		maid.setAction(MaidAction.SWORD_ATTACKING);
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, AbstractLittleMaidEntity maid, long time) {
		Optional<LivingEntity> target = maid.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET);
		return target.filter(t -> LookTargetUtil.isVisibleInMemory(maid, t) && maid.isInAttackRange(t))
				.isPresent();
	}

	@Override
	protected void keepRunning(ServerWorld world, AbstractLittleMaidEntity maid, long time) {
		LivingEntity target = maid.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).orElseThrow();

		if (this.getPassedTicks(time) == 3L) {
			maid.tryAttack(target);
		} else if (this.getPassedTicks(time) == 13) {
			maid.tryAttack(target);
		} else if (this.getPassedTicks(time) == 25) {
			maid.tryAttack(target);
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, AbstractLittleMaidEntity maid, long time) {
		maid.removeAction();
	}
}
