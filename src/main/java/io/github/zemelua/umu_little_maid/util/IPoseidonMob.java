package io.github.zemelua.umu_little_maid.util;

import io.github.zemelua.umu_little_maid.mixin.LivingEntityAccessor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public interface IPoseidonMob extends IMob {
	default void riptideTrident(Entity target) {
		MobEntity self = this.self();
		ItemStack mainStack = this.self().getMainHandStack();
		int riptide = EnchantmentHelper.getRiptide(mainStack);
		if (riptide <= 0) return;

		Vec3d velocity = target.getPos().subtract(self.getPos()).normalize();
		double x = velocity.getX();
		double y = velocity.getY();
		double z = velocity.getZ();
		float length = MathHelper.sqrt((float) (x * x + y * y + z * z));
		float power = 3.0F * ((1.0F + riptide) / 4.0F);

		self.addVelocity(x * (power / length), y * (power / length), z * (power / length));
		if (self.isOnGround()) {
			self.move(MovementType.SELF, new Vec3d(0.0D, 1.2D, 0.0D));
		}

		((LivingEntityAccessor) self).setRiptideTicks(20);
		self.setLivingFlag(4, true);

		SoundEvent sound = switch (riptide) {
			case 1 -> SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
			case 2 -> SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
			default -> SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
		};
		self.getWorld().playSoundFromEntity(null, self, sound, SoundCategory.PLAYERS, 1.0F, 1.0F);
	}

	default void throwTrident(Entity target) {
		MobEntity self = this.self();
		ItemStack mainStack = self.getMainHandStack();
		if (EnchantmentHelper.getRiptide(mainStack) > 0) return;

		TridentEntity trident = new TridentEntity(self.getWorld(), self, mainStack);

		double x = target.getX() - self.getX();
		double y = target.getBodyY(1.0D / 3.0D) - trident.getY();
		double z = target.getZ() - self.getZ();
		double power = Math.sqrt(x * x + z * z);
		trident.setVelocity(x, y + power * 0.2D, z, 1.6F, 6.0F);
		trident.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;

		self.getWorld().spawnEntity(trident);

		self.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (self.getRandom().nextFloat() * 0.4F + 0.8F));
		mainStack.decrement(1);
	}
}
