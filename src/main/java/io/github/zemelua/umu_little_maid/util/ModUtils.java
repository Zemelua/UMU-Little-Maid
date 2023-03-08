package io.github.zemelua.umu_little_maid.util;

import net.minecraft.block.BlockState;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public final class ModUtils {
	public static boolean isMonster(LivingEntity living) {
		return living.getType().getSpawnGroup() == SpawnGroup.MONSTER;
	}

	public static boolean hasEnchantment(Enchantment enchantment, ItemStack itemStack) {
		return EnchantmentHelper.getLevel(enchantment, itemStack) > 0;
	}

	public static ItemStack searchInInventory(Inventory inventory, Predicate<ItemStack> predicate) {
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack itemStack = inventory.getStack(i);
			if (predicate.test(itemStack)) {
				return itemStack;
			}
		}

		return ItemStack.EMPTY;
	}

	public static Optional<BlockPos> getNearestPos(Collection<BlockPos> poses, Entity entity) {
		return poses.stream()
				.min(Comparator.comparingDouble(pos -> entity.squaredDistanceTo(Vec3d.of(pos))));
	}

	public static int getHeightFromGround(World world, Entity entity) {
		BlockPos.Mutable pos = entity.getBlockPos().down().mutableCopy();

		int i;
		for (i = 0; world.getBlockState(pos).isAir() && world.isInBuildLimit(pos); pos.move(Direction.DOWN, 1)) {
			i++;
		}

		return i;
	}

	public static boolean isSameObject(World world, BlockPos pos1, BlockPos pos2) {
		BlockState state1 = world.getBlockState(pos1);
		BlockState state2 = world.getBlockState(pos2);

		if (pos1.equals(pos2)) return true;
		if (!state1.isOf(state2.getBlock())) return false;

		if (state1.contains(Properties.BED_PART) && state2.contains(Properties.BED_PART)
				&& state1.contains(Properties.HORIZONTAL_FACING) && state2.contains(Properties.HORIZONTAL_FACING)) {
			Direction facing = state1.get(Properties.HORIZONTAL_FACING);
			Direction connectTo = state1.get(Properties.BED_PART) == BedPart.HEAD ? facing.getOpposite() : facing;
			BedPart connectWith = state1.get(Properties.BED_PART) == BedPart.HEAD ? BedPart.FOOT : BedPart.HEAD;

			return pos1.offset(connectTo).equals(pos2)
					&& state2.get(Properties.BED_PART) == connectWith
					&& state2.get(Properties.HORIZONTAL_FACING) == facing;
		} else if (state1.isIn(BlockTags.DOORS) && state2.isIn(BlockTags.DOORS)
				&& state1.contains(Properties.DOUBLE_BLOCK_HALF) && state2.contains(Properties.DOUBLE_BLOCK_HALF)) {
			Direction connectTo = state1.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? Direction.DOWN : Direction.UP;
			DoubleBlockHalf connectWith = state1.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER;

			return pos1.offset(connectTo).equals(pos2)
					&& state2.get(Properties.DOUBLE_BLOCK_HALF) == connectWith;
		}

		return false;
	}

	public static float lerpAngle(float angle0, float angle1, float magnitude) {
		float f = (magnitude - angle1) % ((float) Math.PI * 2);

		if (f < (float) -Math.PI) {
			f += (float) Math.PI * 2;
		}

		if (f >= (float) Math.PI) {
			f -= (float) Math.PI * 2;
		}

		return angle1 + angle0 * f;
	}

	public static boolean hasHarmfulEffect(LivingEntity living) {
		return living.getStatusEffects().stream()
				.anyMatch(effect -> effect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL);
	}

	private ModUtils() throws IllegalAccessException {throw new IllegalAccessException();}
}
