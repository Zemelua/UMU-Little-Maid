package io.github.zemelua.umu_little_maid.mixin;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity {
	@Shadow public abstract ItemStack asItemStack();

	@Inject(method = "onPlayerCollision", at = @At(value = "HEAD"), cancellable = true)
	private void onPlayerCollision(PlayerEntity player, CallbackInfo callback) {
		if (this.getOwner() instanceof LittleMaidEntity maid) {
			if (maid.getMaster().filter(master -> master.equals(player)).isPresent() && maid.getJob() != ModEntities.JOB_POSEIDON) {
				super.onPlayerCollision(player);

				callback.cancel();
			}
		}
	}

	@Inject(method = "tryPickup", at = @At(value = "HEAD"), cancellable = true)
	private void tryPickup(PlayerEntity player, CallbackInfoReturnable<Boolean> callback) {
		if (this.isNoClip()) {
			if (this.getOwner() instanceof LittleMaidEntity maid) {
				if (maid.getMaster().filter(master -> master.equals(player)).isPresent() && maid.getJob() != ModEntities.JOB_POSEIDON) {
					if (player.getInventory().insertStack(this.asItemStack())) {
						callback.setReturnValue(true);
					}
				}
			}
		}
	}

	@Deprecated
	protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> type, World world) {
		super(type, world);

		UMULittleMaid.LOGGER.error("TridentEntityMixin はMixinクラスだよ！インスタンス化しないで！");
	}
}
