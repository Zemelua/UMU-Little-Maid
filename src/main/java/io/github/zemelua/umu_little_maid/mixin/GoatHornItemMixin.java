package io.github.zemelua.umu_little_maid.mixin;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Unit;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static io.github.zemelua.umu_little_maid.entity.brain.ModMemories.IS_HUNTING;
import static io.github.zemelua.umu_little_maid.mixin.MixinUtils.*;

@Mixin(GoatHornItem.class)
public abstract class GoatHornItemMixin extends Item {
	@Inject(method = METHOD_GOAT_HORN_ITEM_USE, at = @At(value = VALUE_INVOKE, target = PATH_GOAT_HORN_ITEM_PLAY_SOUND, ordinal = 0))
	public void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> callback) {
		if (!world.isClient()) {
			Box box = user.getBoundingBox().expand(10, 3, 10);
			List<LittleMaidEntity> maids = world.getEntitiesByClass(LittleMaidEntity.class, box, maid -> maid.getOwner() == user);
			for (LittleMaidEntity maid : maids) {
				maid.getBrain().remember(IS_HUNTING, Unit.INSTANCE, 600);
			}
		}
	}

	@Deprecated
	private GoatHornItemMixin(Settings properties) {
		super(properties);

		UMULittleMaid.LOGGER.error("GoatHornItemMixin はMixinクラスだよ！インスタンス化しないで！");
	}
}
