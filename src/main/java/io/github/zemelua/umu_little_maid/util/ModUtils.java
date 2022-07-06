package io.github.zemelua.umu_little_maid.util;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

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

	public static ItemStack searchInInventory(Inventory inventory, Predicate<ItemStack> predicate) {
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack itemStack = inventory.getStack(i);
			if (predicate.test(itemStack)) {
				return itemStack;
			}
		}

		return ItemStack.EMPTY;
	}
}
