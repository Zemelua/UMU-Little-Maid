package io.github.zemelua.umu_little_maid.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemStack;

public final class ModUtils {
	private ModUtils() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	public static boolean isMonster(Entity entity) {
		return entity.getType().getSpawnGroup() == SpawnGroup.MONSTER;
	}

	public static boolean hasEnchantment(Enchantment enchantment, ItemStack itemStack) {
		return EnchantmentHelper.getLevel(enchantment, itemStack) > 0;
	}
}
