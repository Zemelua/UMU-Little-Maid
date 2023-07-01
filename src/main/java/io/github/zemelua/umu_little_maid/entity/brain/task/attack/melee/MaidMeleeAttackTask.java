package io.github.zemelua.umu_little_maid.entity.brain.task.attack.melee;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.attack.MaidAttackType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.server.world.ServerWorld;

import java.util.Map;
import java.util.Optional;

/**
 * メイドさんが攻撃中のときタスクもKeepRunningされます。
 * メイドさんが攻撃できる状態にあるとき、一度タスクを中断することなくそのタスク内で次の攻撃を実行します。
 * これは、中断することで他のタスクが実行されないようにするためです。
 */
public class MaidMeleeAttackTask extends MultiTickTask<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT);

	private int attackProgress = 0;

	public MaidMeleeAttackTask() {
		super(REQUIRED_MEMORIES);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		LivingEntity target = maid.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElseThrow();
		return LookTargetUtil.isVisibleInMemory(maid, target) && maid.isInAttackRange(target);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		LivingEntity target = maid.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).orElseThrow();
		maid.startAttack(target, MaidAttackType.SWING_SWORD_DOWNWARD_RIGHT);
		this.attackProgress++;
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		if (maid.getAttackType() != MaidAttackType.NO_ATTACKING) return true;

		Optional<LivingEntity> target = maid.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);
		if (target.isEmpty()) return false;

		return LookTargetUtil.isVisibleInMemory(maid, target.get()) && maid.isInAttackRange(target.get());
	}

	@Override
	protected void keepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		if (maid.getAttackType() == MaidAttackType.NO_ATTACKING) {
			Optional<LivingEntity> target = maid.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET);

			target.ifPresent(t -> {
				switch (this.attackProgress % 2) {
					case 0 -> maid.startAttack(t, MaidAttackType.SWING_SWORD_DOWNWARD_RIGHT);
					case 1 -> maid.startAttack(t, MaidAttackType.SWING_SWORD_DOWNWARD_LEFT);
					case 2 -> maid.startAttack(t, MaidAttackType.SWEEP_SWORD);
				}

				this.attackProgress++;
			});
		}
	}
}
