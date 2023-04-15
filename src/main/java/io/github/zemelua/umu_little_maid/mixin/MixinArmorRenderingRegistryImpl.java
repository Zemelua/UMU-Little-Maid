package io.github.zemelua.umu_little_maid.mixin;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.client.geo.LittleMaidGeoRenderer;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import software.bernie.geckolib3.ArmorRenderingRegistryImpl;

@Mixin(ArmorRenderingRegistryImpl.class)
public abstract class MixinArmorRenderingRegistryImpl {
	@Inject(method = "getArmorModel",
			at = @At("HEAD"),
			cancellable = true,
			remap = false)
	private static void returnMaidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, BipedEntityModel<LivingEntity> defaultModel, CallbackInfoReturnable<BipedEntityModel<LivingEntity>> callback) {
		if (entity instanceof LittleMaidEntity) {
			callback.setReturnValue(LittleMaidGeoRenderer.ARMOR_MODEL);
		}
	}

	@Inject(method = "getArmorTexture",
			at = @At("HEAD"),
			cancellable = true,
			remap = false)
	private static void returnMaidArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot, boolean secondLayer, String suffix, Identifier defaultTexture, CallbackInfoReturnable<Identifier> callback) {
		if (entity instanceof LittleMaidEntity) {
			callback.setReturnValue(UMULittleMaid.identifier("textures/item/armor/maid_diamond"));
		}
	}
}
