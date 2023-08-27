package io.github.zemelua.umu_little_maid.entity.maid;

import com.mojang.datafixers.util.Pair;
import io.github.zemelua.umu_little_maid.api.IFisher;
import io.github.zemelua.umu_little_maid.api.IHeadpattable;
import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.action.MaidAction;
import io.github.zemelua.umu_little_maid.util.IInstructable;
import io.github.zemelua.umu_little_maid.util.ITameable;
import io.github.zemelua.umu_little_maid.util.ModUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// LittleMaidEntity クラスがめっっちゃ長くなってきたので、こっちに書けるものはこっちに書いときます。
public interface ILittleMaidEntity extends GeoAnimatable, ITameable, IHeadpattable, InventoryOwner, IInstructable, IFisher {
	RawAnimation GLIDE = RawAnimation.begin().thenWait(5).thenLoop("glide");
	RawAnimation TRANSFORM = RawAnimation.begin().thenPlay("transform");
	RawAnimation HEADPATTED = RawAnimation.begin().thenLoop("headpatted");
	RawAnimation EAT = RawAnimation.begin().thenLoop("eat");
	RawAnimation SLEEP = RawAnimation.begin().thenLoop("sleeping");
	RawAnimation ANIMATION_AXE_ATTACK_RIGHT = RawAnimation.begin().thenPlay("attack.axe.right");
	RawAnimation SPEAR_RIGHT = RawAnimation.begin().thenPlay("attack.spear.right");
	RawAnimation HOLD_SPEAR_LEFT = RawAnimation.begin().thenPlay("attack.hold.spear.left");
	RawAnimation HOLD_SPEAR_RIGHT = RawAnimation.begin().thenPlay("attack.hold.spear.right");
	RawAnimation HOLD_BOW_LEFT = RawAnimation.begin().thenPlay("attack.hold.bow.left");
	RawAnimation HOLD_BOW_RIGHT = RawAnimation.begin().thenPlay("attack.hold.bow.right");
	RawAnimation ANIMATION_CHARGE_CROSSBOW_LEFT = RawAnimation.begin().thenPlay("attack.charge.crossbow.left");
	RawAnimation ANIMATION_CHARGE_CROSSBOW_RIGHT = RawAnimation.begin().thenPlay("attack.charge.crossbow.right");
	RawAnimation ANIMATION_HOLD_CROSSBOW_LEFT = RawAnimation.begin().thenPlay("attack.hold.crossbow.left");
	RawAnimation ANIMATION_HOLD_CROSSBOW_RIGHT = RawAnimation.begin().thenPlay("attack.hold.crossbow.right");
	RawAnimation HEADBUTT = RawAnimation.begin().thenPlay("headbutt");
	RawAnimation CHASE_SWORD = RawAnimation.begin().thenLoop("chase_sword");
	RawAnimation GLIDE_ROOT = RawAnimation.begin().thenLoop("glide_root");
	RawAnimation HARVEST = RawAnimation.begin().thenPlay("farm.harvest");
	RawAnimation PLANT = RawAnimation.begin().thenPlay("farm.plant");
	RawAnimation HEAL = RawAnimation.begin().thenLoop("heal");
	RawAnimation ANIMATION_DELIVER = RawAnimation.begin().thenLoop("deliver");
	RawAnimation ANIMATION_SWORD_ATTACK = RawAnimation.begin().thenPlay("swing_sword_downward_right")
			.thenPlay("swing_sword_downward_left")
			.thenPlay("swing_sword_downward_right");

	Optional<MaidAction> getAction();
	void setAction(MaidAction value);
	void removeAction();

	void headbutt(LivingEntity target);
	int getContinuityAttackedCount();
	void resetContinuityAttackedCount();

	default boolean canAction() {
		return this.getAction().isEmpty();
	}

	default boolean isEating() {
		return this.getAction()
				.map(action -> action == MaidAction.EATING)
				.orElse(false);
	}

	default boolean isTransforming() {
		return this.getAction()
				.map(action -> action == MaidAction.TRANSFORMING)
				.orElse(false);
	}

	default boolean isGliding() {
		return this.getAction()
				.map(action -> action.equals(MaidAction.GLIDING))
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

	default boolean isShearing() {
		return this.getAction()
				.map(action -> action == MaidAction.SHEARING)
				.orElse(false);
	}

	default boolean isFishWaiting() {
		return this.getAction()
				.map(action -> action == MaidAction.FISH_WAITING)
				.orElse(false);
	}

	default boolean isFishFighting() {
		return this.getAction()
				.map(action -> action == MaidAction.FISH_FIGHTING)
				.orElse(false);
	}

	default boolean isHealing() {
		return this.getAction()
				.map(action -> action == MaidAction.HEALING)
				.orElse(false);
	}

	default boolean isDelivering() {
		return this.getAction()
				.map(action -> action == MaidAction.DELIVERING)
				.orElse(false);
	}

	default boolean isSwordAttacking() {
		return this.getAction()
				.map(action -> action == MaidAction.SWORD_ATTACKING)
				.orElse(false);
	}

	default boolean isAxeAttacking() {
		return this.getAction()
				.map(action -> action == MaidAction.AXE_ATTACKING)
				.orElse(false);
	}

	default boolean isSpearing() {
		return this.getAction()
				.map(action -> action == MaidAction.SPEARING)
				.orElse(false);
	}

	default boolean isHeadbutting() {
		return this.getAction()
				.map(action -> action == MaidAction.HEADBUTTING)
				.orElse(false);
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

	default ItemStack getHasWool() {
		return searchItem(ModTags.ITEM_MAID_SHEPHERD_DELIVERS);
	}

	default boolean hasWool() {
		return !this.getHasWool().isEmpty();
	}

	default List<ItemStack> getHasHarvests() {
		List<ItemStack> harvests = new ArrayList<>();

		for (int i = 0; i < this.getInventory().size(); i++) {
			ItemStack itemStack = this.getInventory().getStack(i);
			if (itemStack.isIn(ModTags.ITEM_MAID_HARVESTS)) {
				harvests.add(itemStack);
			}
		}

		return harvests;
	}

	default boolean hasHarvests() {
		return !this.getHasHarvests().isEmpty();
	}

	default boolean isDeliverableBox(GlobalPos pos) {
		if (!this.getWorld().getRegistryKey().equals(pos.getDimension())) return false;

		World world = this.getWorld();
		Brain<?> brain = this.getBrain();
		Optional<List<Pair<GlobalPos, Long>>> undeliverableBoxes = brain.getOptionalRegisteredMemory(ModMemories.UNDELIVERABLE_BOXES);
		if (undeliverableBoxes.orElse(List.of()).stream()
				.map(Pair::getFirst)
				.collect(Collectors.toSet())
				.contains(pos)) return false;

		BlockState state = world.getBlockState(pos.getPos());
		if (state.isOf(Blocks.CHEST) || state.isOf(Blocks.TRAPPED_CHEST)) return !ChestBlock.isChestBlocked(world, pos.getPos());
		if (state.isIn(BlockTags.SHULKER_BOXES) && world.getBlockEntity(pos.getPos()) instanceof ShulkerBoxBlockEntity tile)
			return ShulkerBoxBlock.canOpen(state, world, pos.getPos(), tile);

		return true;
	}

	Brain<?> getBrain();
	World getWorld();
	EntityNavigation getNavigation();
	ItemStack getOffHandStack();
}
