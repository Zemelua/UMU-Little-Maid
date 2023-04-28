package io.github.zemelua.umu_little_maid.entity.brain.task.guard;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.attack.MaidAttackType;
import io.github.zemelua.umu_little_maid.util.ModWorldUtils;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MaidHeadbuttTask extends Task<LittleMaidEntity> {
	private static final ImmutableMap<MemoryModuleType<?>, MemoryModuleState> REQUIRED_MEMORIES = ImmutableMap.of();

	public MaidHeadbuttTask() {
		super(REQUIRED_MEMORIES);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, LittleMaidEntity maid) {
		List<MobEntity> entities = world.getEntitiesByClass(MobEntity.class, ModWorldUtils.box(maid.getPos(), 1.0D), mob -> {
			if (mob.getTarget() != null) {
				return mob.getTarget().equals(maid);
			}

			return false;
		});

		return maid.getContinuityAttackedCount() >= 3 && !entities.isEmpty();
	}

	@Override
	protected void run(ServerWorld world, LittleMaidEntity maid, long time) {
		Optional<MobEntity> target = world.getEntitiesByClass(MobEntity.class, ModWorldUtils.box(maid.getPos(), 1.0D), mob -> {
			if (mob.getTarget() != null) {
				return mob.getTarget().equals(maid);
			}

			return false;
		}).stream().min(Comparator.comparingInt(mob -> (int) Math.floor(mob.distanceTo(maid) * 100000)));

		target.ifPresent(targetArg -> maid.startAttack(targetArg, MaidAttackType.HEADBUTT));
		maid.resetContinuityAttackedCount();
	}
}
