package io.github.zemelua.umu_little_maid.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public final class ModUtils {
	private ModUtils() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	public static boolean hasEnchantment(Enchantment enchantment, ItemStack itemStack) {
		return EnchantmentHelper.getLevel(enchantment, itemStack) > 0;
	}
}
