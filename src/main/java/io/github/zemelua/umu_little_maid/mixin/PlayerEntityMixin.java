package io.github.zemelua.umu_little_maid.mixin;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.entity.maid.MaidPersonality;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
	@Unique private static final MaidPersonality[] LOVE_ON_WAKE_UPS = new MaidPersonality[] {
			ModEntities.PERSONALITY_LAZY
	};

	@Inject(method = "attack", at = @At(value = "RETURN"))
	public void attack(Entity target, CallbackInfo callback) {
		if (!this.world.isClient() && target instanceof LivingEntity targetLiving && ModUtils.isMonster(targetLiving) && targetLiving.isDead()) {
			Box box = this.getBoundingBox().expand(10, 3, 10);
			Predicate<LittleMaidEntity> filter = maid -> {
				if (maid.getPersonality().isIn(ModTags.PERSONALITY_FLUTTER_WHEN_KILLS)) {
					return maid.getOwner() == this;
				}

				return false;
			};

			List<LittleMaidEntity> maids = this.world.getEntitiesByClass(LittleMaidEntity.class, box, filter);
			for (LittleMaidEntity maid : maids) {
				maid.increaseIntimacy(3);
			}
		}
	}

	@Inject(method = "wakeUp(ZZ)V", at = @At(value = "RETURN"))
	public void wakeUp(boolean skipSleepTimer, boolean updateSleepingPlayers, CallbackInfo callback) {
		if (!this.world.isClient()) {
			Box box = this.getBoundingBox().expand(10, 3, 10);
			Predicate<LittleMaidEntity> filter = maid -> {
				if (maid.getPersonality().isIn(ModTags.PERSONALITY_FLUTTER_WHEN_WAKE_UPS)) {
					return maid.getOwner() == this;
				}

				return false;
			};

			List<LittleMaidEntity> maids = this.world.getEntitiesByClass(LittleMaidEntity.class, box, filter);
			for (LittleMaidEntity maid : maids) {
				maid.increaseIntimacy(20);
			}
		}
	}

	@Deprecated
	protected PlayerEntityMixin(EntityType<PlayerEntity> type, World world) {
		super(type, world);

		UMULittleMaid.LOGGER.error("PlayerEntityMixin はMixinクラスだよ！インスタンス化しないで！");
	}
}
