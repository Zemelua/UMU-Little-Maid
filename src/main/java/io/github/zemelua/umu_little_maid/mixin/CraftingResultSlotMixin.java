package io.github.zemelua.umu_little_maid.mixin;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Predicate;

@Mixin(CraftingResultSlot.class)
public abstract class CraftingResultSlotMixin extends Slot {
	@Inject(method = "onTakeItem", at = @At(value = "HEAD"))
	public void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo callback) {
		if (!player.getWorld().isClient()) {
			Box box = player.getBoundingBox().expand(10, 3, 10);
			Predicate<LittleMaidEntity> filter = maid -> {
				if (maid.getPersonality().isIn(ModTags.PERSONALITY_FLUTTER_WHEN_CRAFTS)) {
					return maid.getOwner() == player;
				}

				return false;
			};

			List<LittleMaidEntity> maids = player.getWorld().getEntitiesByClass(LittleMaidEntity.class, box, filter);
			for (LittleMaidEntity maid : maids) {
				maid.increaseIntimacy(2, false);
			}
		}
	}

	@Deprecated
	public CraftingResultSlotMixin(Inventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);

		UMULittleMaid.LOGGER.error("CraftingResultSlotMixin はMixinクラスだよ！インスタンス化しないで！");
	}
}
