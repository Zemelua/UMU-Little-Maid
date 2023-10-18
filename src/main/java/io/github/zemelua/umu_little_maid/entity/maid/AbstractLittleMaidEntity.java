package io.github.zemelua.umu_little_maid.entity.maid;

import io.github.zemelua.umu_little_maid.api.IHeadpattable;
import io.github.zemelua.umu_little_maid.util.IAvoidRain;
import io.github.zemelua.umu_little_maid.util.IInstructable;
import io.github.zemelua.umu_little_maid.util.IPoseidonMob;
import io.github.zemelua.umu_little_maid.util.ITameable;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;

import java.util.Optional;

public abstract class AbstractLittleMaidEntity extends PathAwareEntity implements ILittleMaidEntity, InventoryOwner, RangedAttackMob, IPoseidonMob, CrossbowUser, ITameable, IAvoidRain, IInstructable, IHeadpattable, GeoEntity {
	protected AbstractLittleMaidEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
		super(entityType, world);
	}

	public Optional<LivingEntity> getTargetOptional() {
		return Optional.ofNullable(this.getTarget());
	}

	public boolean isHoldingChargedCrossbow() {
		return CrossbowItem.isCharged(this.getMainHandStack());
	}

	@Override public Brain<?> getBrain() {return super.getBrain();}
	@Override public World getWorld() {return super.getWorld();}
	@Override public EntityPose getPose() {return super.getPose();}
	@Override public EntityNavigation getNavigation() {return super.getNavigation();}
	@Override public ItemStack getOffHandStack() {return super.getOffHandStack();}
}
