package io.github.zemelua.umu_little_maid.mixin;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity.PickupPermission;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntity {
	@Shadow public PersistentProjectileEntity.PickupPermission pickupType;

	@Inject(method = "setOwner", at = @At(value = "TAIL"))
	private void setOwner(Entity entity, CallbackInfo callback) {
		if (entity instanceof LittleMaidEntity) {
			this.pickupType = PickupPermission.ALLOWED;
		}
	}

	@Deprecated
	private PersistentProjectileEntityMixin(EntityType<? extends PersistentProjectileEntity> type, World world) {
		super(type, world);

		UMULittleMaid.LOGGER.error("PersistentProjectileEntityMixin はMixinクラスだよ！インスタンス化しないで！");
	}
}
