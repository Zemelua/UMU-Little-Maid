package io.github.zemelua.umu_little_maid.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

public final class ModUtils {
	@SuppressWarnings("unused") public static final double RADIAN_D = 180.0D / Math.PI;
	public static final float RADIAN_F = 180.0F / (float) Math.PI;

	private ModUtils() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	public static boolean isMonster(Entity entity) {
		return entity.getType().getSpawnGroup() == SpawnGroup.MONSTER;
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

		int height;
		for (height = 0; world.getBlockState(pos).isAir(); pos.move(Direction.DOWN, 1)) {
			height++;
		}

		return height;
	}

	public static boolean hasHarmfulEffect(LivingEntity entity) {
		return entity.getStatusEffects().stream()
				.anyMatch(effect -> effect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL);
	}

	public static float lerpAngle(float angleOne, float angleTwo, float magnitude) {
		float f = (magnitude - angleTwo) % ((float)Math.PI * 2);
		if (f < (float)(-Math.PI)) {
			f += (float)Math.PI * 2;
		}
		if (f >= (float)Math.PI) {
			f -= (float)Math.PI * 2;
		}
		return angleTwo + angleOne * f;
	}
}
