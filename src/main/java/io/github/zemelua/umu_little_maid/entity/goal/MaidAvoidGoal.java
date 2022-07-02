package io.github.zemelua.umu_little_maid.entity.goal;

import io.github.zemelua.umu_little_maid.entity.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class MaidAvoidGoal extends FleeEntityGoal<MobEntity> {
	public MaidAvoidGoal(LittleMaidEntity maid) {
		super(maid, MobEntity.class, 24.0F, 1.0D, 1.0D, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test);

		this.setControls(EnumSet.of(Goal.Control.MOVE));
	}

	@Override
	public boolean canStart() {
		if (!((LittleMaidEntity) this.mob).getJob().isAvoid() || ((LittleMaidEntity) this.mob).isSitting()) return false;

		this.targetEntity = this.mob.getWorld().getClosestEntity(this.mob.getWorld().getEntitiesByClass(
						MobEntity.class, this.mob.getBoundingBox().expand(24.0D, 3.0D, 24.0D), (mob
								-> !mob.getType().getSpawnGroup().isPeaceful()
						)),
				this.withinRangePredicate, this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());
		if (this.targetEntity == null) return false;

		Entity owner = ((LittleMaidEntity) this.mob).getOwner();
		Vec3d awayPos;
		if (owner != null && ((LittleMaidEntity) this.mob).getPersonality() == ModEntities.SHY) {
			Vec3d behindOwnerPos = targetEntity.getPos().subtract(owner.getPos()).normalize();
			awayPos = new Vec3d(owner.getX() + behindOwnerPos.getX(), owner.getY(), owner.getZ() + behindOwnerPos.getZ());
		} else {
			awayPos = NoPenaltyTargeting.findFrom(this.mob, 16, 7, this.targetEntity.getPos());
		}

		if (awayPos != null && this.targetEntity.squaredDistanceTo(awayPos) >= this.targetEntity.squaredDistanceTo(this.mob)) {
			this.fleePath = this.mob.getNavigation().findPathTo(new BlockPos(awayPos), 0);

			return this.fleePath != null;
		}

		return false;
	}
}
