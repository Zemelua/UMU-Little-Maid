package io.github.zemelua.umu_little_maid.entity.brain.task.guard;

import com.google.common.collect.ImmutableMap;
import io.github.zemelua.umu_little_maid.api.BetterMultiTickTask;
import io.github.zemelua.umu_little_maid.entity.maid.AbstractLittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.action.MaidAction;
import io.github.zemelua.umu_little_maid.util.ModWorldUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class HeadbuttTask extends BetterMultiTickTask<AbstractLittleMaidEntity> {
	public HeadbuttTask() {
		super(ImmutableMap.of(), 40);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, AbstractLittleMaidEntity maid) {
		List<MobEntity> entities = world.getEntitiesByClass(MobEntity.class, ModWorldUtils.box(maid.getPos(), 1.0D), mob -> {
			if (mob.getTarget() != null) {
				return mob.getTarget().equals(maid);
			}

			return false;
		});

		return maid.getContinuityAttackedCount() >= 3 && !entities.isEmpty();
	}

	@Override
	protected void run(ServerWorld world, AbstractLittleMaidEntity maid, long time) {
		Optional<MobEntity> target = world.getEntitiesByClass(MobEntity.class, ModWorldUtils.box(maid.getPos(), 1.0D), mob
						-> Optional.ofNullable(mob.getTarget()).filter(t -> t.equals(maid)).isPresent()
				).stream()
				.min(Comparator.comparingDouble(mob -> mob.distanceTo(maid)));

		target.ifPresent(t -> {
			maid.setAction(MaidAction.HEADBUTTING);
			maid.setTarget(t);
			maid.resetContinuityAttackedCount();
		});
	}

	@Override
	protected boolean shouldKeepRunning(ServerWorld world, AbstractLittleMaidEntity maid, long time) {
		return maid.getTargetOptional()
				.filter(t -> maid.distanceTo(t) < 3.0D)
				.isPresent();
	}

	@Override
	protected void keepRunning(ServerWorld world, AbstractLittleMaidEntity maid, long time) {
		Optional<LivingEntity> target = maid.getTargetOptional();

		if (this.getPassedTicks(time) == 24L) {
			target.ifPresent(maid::headbutt);
		}
	}

	@Override
	protected void finishRunning(ServerWorld world, AbstractLittleMaidEntity maid, long time) {
		maid.removeAction();
	}
}
