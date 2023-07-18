package io.github.zemelua.umu_little_maid.entity.maid;

import io.github.zemelua.umu_little_maid.api.IHeadpattable;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.maid.action.MaidAction;
import io.github.zemelua.umu_little_maid.util.ITameable;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.Optional;

// LittleMaidEntity クラスがめっっちゃ長くなってきたので、こっちに書けるものはこっちに書いときます。
public sealed interface ILittleMaidEntity extends GeoAnimatable, ITameable, IHeadpattable, InventoryOwner permits LittleMaidEntity {
	RawAnimation GLIDE = RawAnimation.begin().thenWait(5).thenLoop("glide");
	RawAnimation TRANSFORM = RawAnimation.begin().thenPlay("transform");
	RawAnimation HEADPATTED = RawAnimation.begin().thenLoop("headpatted");
	RawAnimation EAT = RawAnimation.begin().thenLoop("eat");
	RawAnimation SLEEP = RawAnimation.begin().thenLoop("sleeping");
	RawAnimation SWING_SWORD_DOWNWARD_RIGHT = RawAnimation.begin().thenPlay("swing_sword_downward_right");
	RawAnimation SWING_SWORD_DOWNWARD_LEFT = RawAnimation.begin().thenPlay("swing_sword_downward_left");
	RawAnimation SPEAR_RIGHT = RawAnimation.begin().thenPlay("attack.spear.right");
	RawAnimation HEADBUTT = RawAnimation.begin().thenPlay("headbutt");
	RawAnimation CHASE_SWORD = RawAnimation.begin().thenLoop("chase_sword");
	RawAnimation GLIDE_ROOT = RawAnimation.begin().thenLoop("glide_root");
	RawAnimation HARVEST = RawAnimation.begin().thenPlay("farm.harvest");
	RawAnimation PLANT = RawAnimation.begin().thenPlay("farm.plant");
	RawAnimation HEAL = RawAnimation.begin().thenLoop("heal");

	Optional<MaidAction> getAction();

	void setAction(MaidAction value);

	default boolean canAction() {
		return this.getAction().isEmpty();
	}

	default boolean isEating() {
		return this.getAction()
				.map(action -> action == MaidAction.EATING)
				.orElse(false);
	}

	default boolean isHarvesting() {
		return this.getAction()
				.map(action -> action == MaidAction.HARVESTING)
				.orElse(false);
	}

	default boolean isPlanting() {
		return this.getAction()
				.map(action -> action == MaidAction.PLANTING)
				.orElse(false);
	}

	default boolean isHealing() {
		return this.getAction()
				.map(action -> action == MaidAction.HEALING)
				.orElse(false);
	}

	default boolean isTransforming() {
		return this.getPose().equals(ModEntities.POSE_CHANGING_COSTUME);
	}

	default ItemStack searchItem(TagKey<Item> tag) {
		if (this.getOffHandStack().isIn(tag)) {
			return this.getOffHandStack();
		}

		return ModUtils.searchInInventory(this.getInventory(), tag);
	}

	default ItemStack getHasSugar() {
		return searchItem(ModTags.ITEM_MAID_HEAL_FOODS);
	}

	default boolean hasSugar() {
		return !this.getHasSugar().isEmpty();
	}

	default ItemStack getHasCrop() {
		return searchItem(ModTags.ITEM_MAID_CROPS);
	}

	default boolean hasCrop() {
		return !this.getHasCrop().isEmpty();
	}

	/**
	 * @see LittleMaidEntity#getPoses()
	 */
	EntityPose getPose();

	ItemStack getOffHandStack();
}
