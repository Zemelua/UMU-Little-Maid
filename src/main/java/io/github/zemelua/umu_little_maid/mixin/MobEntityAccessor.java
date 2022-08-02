package io.github.zemelua.umu_little_maid.mixin;

import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MobEntity.class)
public interface MobEntityAccessor {
	@Invoker void callDisablePlayerShield(PlayerEntity player, ItemStack mobStack, ItemStack playerStack);
}
