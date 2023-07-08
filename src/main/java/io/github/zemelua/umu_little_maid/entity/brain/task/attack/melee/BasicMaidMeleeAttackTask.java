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

public class BasicMaidMeleeAttackTask extends MultiTickTask<LittleMaidEntity> {
	private static final Map<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT);

	private final MaidAttackType[] attacks;

	private int attackProgress = 0;

	public BasicMaidMeleeAttackTask(MaidAttackType... attacks) {
		super(REQUIRED_MEMORIES);

		this.attacks = attacks;
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		LivingEntity target = maid.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).orElseThrow();
		return LookTargetUtil.isVisibleInMemory(maid, target) && maid.isInAttackRange(target);
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		LivingEntity target = maid.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).orElseThrow();
		maid.startAttack(target, this.attacks[0]);
		this.attackProgress++;
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		if (maid.getAttackType() != MaidAttackType.NO_ATTACKING) return true;

		Optional<LivingEntity> target = maid.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET);
		return target.filter(livingEntity -> LookTargetUtil.isVisibleInMemory(maid, livingEntity) && maid.isInAttackRange(livingEntity))
				.isPresent();

	}

	@Override
	protected void keepRunning(ServerWorld world, LittleMaidEntity maid, long time) {
		if (maid.getAttackType() == MaidAttackType.NO_ATTACKING) {
			Optional<LivingEntity> target = maid.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET);

			target.ifPresent(t -> {
				maid.startAttack(t, this.attacks[this.attackProgress % this.attacks.length]);

				this.attackProgress++;
			});
		}
	}
}
