package io.github.zemelua.umu_little_maid.client.geo.layer;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

public class LittleMaidArmorGeoLayer extends ItemArmorGeoLayer<LittleMaidEntity> {
	static final String KEY_ARMOR_HEAD = "armorHead";
	static final String KEY_ARMOR_LEFT_LEG = "armorLeftLeg";
	static final String KEY_ARMOR_RIGHT_LEG = "armorRightLeg";
	public static final Identifier TEXTURE_LEATHER = UMULittleMaid.identifier("textures/item/armor/maid_leather.png");
	public static final Identifier TEXTURE_LEATHER_OVERLAY = UMULittleMaid.identifier("textures/item/armor/maid_leather_overlay.png");
	public static final Identifier TEXTURE_IRON = UMULittleMaid.identifier("textures/item/armor/maid_iron.png");
	public static final Identifier TEXTURE_GOLD = UMULittleMaid.identifier("textures/item/armor/maid_gold.png");
	public static final Identifier TEXTURE_DIAMOND = UMULittleMaid.identifier("textures/item/armor/maid_diamond.png");
	public static final Identifier TEXTURE_NETHERITE = UMULittleMaid.identifier("textures/item/armor/maid_netherite.png");
	public static final Identifier TEXTURE_CHAINMAIL = UMULittleMaid.identifier("textures/item/armor/maid_chainmail.png");
	public static final Identifier TEXTURE_TURTLE = UMULittleMaid.identifier("textures/item/armor/maid_turtle.png");

	public LittleMaidArmorGeoLayer(GeoRenderer<LittleMaidEntity> geoRenderer) {
		super(geoRenderer);
	}

	@Nullable
	@Override
	protected ItemStack getArmorItemForBone(GeoBone bone, LittleMaidEntity maid) {
		return switch (bone.getName()) {
			case KEY_ARMOR_HEAD -> maid.getEquippedStack(EquipmentSlot.HEAD);
			case KEY_ARMOR_LEFT_LEG, KEY_ARMOR_RIGHT_LEG -> maid.getEquippedStack(EquipmentSlot.FEET);
			default -> super.getArmorItemForBone(bone, maid);
		};
	}

	@NotNull
	@Override
	protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, LittleMaidEntity maid) {
		return switch (bone.getName()) {
			case KEY_ARMOR_HEAD -> EquipmentSlot.HEAD;
			case KEY_ARMOR_LEFT_LEG, KEY_ARMOR_RIGHT_LEG -> EquipmentSlot.FEET;
			default -> super.getEquipmentSlotForBone(bone, stack, maid);
		};
	}

	@NotNull
	@Override
	protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, LittleMaidEntity maid, BipedEntityModel<?> baseModel) {
		return switch (bone.getName()) {
			case KEY_ARMOR_HEAD -> baseModel.head;
			case KEY_ARMOR_LEFT_LEG -> baseModel.leftLeg;
			case KEY_ARMOR_RIGHT_LEG -> baseModel.rightLeg;
			default -> super.getModelPartForBone(bone, slot, stack, maid, baseModel);
		};
	}

	// バニラのアーマーのテクスチャとしてメイドさん専用のものを使用するため。
	@Override
	public Identifier getVanillaArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, String type) {
		ArmorItem armor = (ArmorItem) stack.getItem();
		ArmorMaterial material = armor.getMaterial();

		if (material.equals(ArmorMaterials.LEATHER)) {
			if (type == null)                               return TEXTURE_LEATHER;
			else if (type.equals("overlay"))                return TEXTURE_LEATHER_OVERLAY;
		} else if (material.equals(ArmorMaterials.IRON))    return TEXTURE_IRON;
		else if (material.equals(ArmorMaterials.GOLD))      return TEXTURE_GOLD;
		else if (material.equals(ArmorMaterials.DIAMOND))   return TEXTURE_DIAMOND;
		else if (material.equals(ArmorMaterials.NETHERITE)) return TEXTURE_NETHERITE;
		else if (material.equals(ArmorMaterials.CHAIN))     return TEXTURE_CHAINMAIL;
		else if (material.equals(ArmorMaterials.TURTLE))    return TEXTURE_TURTLE;

		return super.getVanillaArmorResource(entity, stack, slot, type);
	}
}
