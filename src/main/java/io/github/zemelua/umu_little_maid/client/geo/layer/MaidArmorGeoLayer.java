package io.github.zemelua.umu_little_maid.client.geo.layer;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.client.geo.model.MaidGeoModel;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.ItemArmorGeoLayer;

public class MaidArmorGeoLayer extends ItemArmorGeoLayer<LittleMaidEntity> {
	private static final BipedEntityModel<LivingEntity> MODEL = new BipedEntityModel<>(modelData().createModel());

	private static final String MODEL_KEY_HEAD = EntityModelPartNames.HEAD;
	private static final String MODEL_KEY_HAT = EntityModelPartNames.HAT;
	private static final String MODEL_KEY_BODY = EntityModelPartNames.BODY;
	private static final String MODEL_KEY_SKIRT = "skirt";
	private static final String MODEL_KEY_LEFT_ARM = EntityModelPartNames.LEFT_ARM;
	private static final String MODEL_KEY_RIGHT_ARM = EntityModelPartNames.RIGHT_ARM;
	private static final String MODEL_KEY_LEFT_LEG = EntityModelPartNames.LEFT_LEG;
	private static final String MODEL_KEY_RIGHT_LEG = EntityModelPartNames.RIGHT_LEG;

	public static final Identifier TEXTURE_LEATHER = UMULittleMaid.identifier("textures/item/armor/maid_leather.png");
	public static final Identifier TEXTURE_LEATHER_OVERLAY = UMULittleMaid.identifier("textures/item/armor/maid_leather_overlay.png");
	public static final Identifier TEXTURE_IRON = UMULittleMaid.identifier("textures/item/armor/maid_iron.png");
	public static final Identifier TEXTURE_GOLD = UMULittleMaid.identifier("textures/item/armor/maid_gold.png");
	public static final Identifier TEXTURE_DIAMOND = UMULittleMaid.identifier("textures/item/armor/maid_diamond.png");
	public static final Identifier TEXTURE_NETHERITE = UMULittleMaid.identifier("textures/item/armor/maid_netherite.png");
	public static final Identifier TEXTURE_CHAINMAIL = UMULittleMaid.identifier("textures/item/armor/maid_chainmail.png");
	public static final Identifier TEXTURE_TURTLE = UMULittleMaid.identifier("textures/item/armor/maid_turtle.png");

	private static TexturedModelData modelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();

		root.addChild(MODEL_KEY_HEAD, ModelPartBuilder.create()
						.uv(0, 0).cuboid(MODEL_KEY_HEAD, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.75F)),
				ModelTransform.pivot(0.0F, 8.0F, 0.0F));
		root.addChild(MODEL_KEY_HAT, ModelPartBuilder.create(), ModelTransform.NONE);
		root.addChild(MODEL_KEY_BODY, ModelPartBuilder.create()
						.uv(0, 16).cuboid(MODEL_KEY_BODY, -3.0F, 0.0F, -2.0F, 6.0F, 9.0F, 4.0F, new Dilation(0.75F)),
				ModelTransform.pivot(0.0F, 8.0F, 0.0F));
		root.addChild(MODEL_KEY_SKIRT, ModelPartBuilder.create()
						.uv(32, 0).cuboid(MODEL_KEY_SKIRT, -4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.75F)),
				ModelTransform.pivot(0.0F, 14.0F, 0.0F));
		root.addChild(MODEL_KEY_LEFT_ARM, ModelPartBuilder.create()
						.uv(28, 16).cuboid(MODEL_KEY_LEFT_ARM, -1.0F, -1.5F, -1.5F, 2.0F, 9.0F, 2.0F, new Dilation(0.75F)),
				ModelTransform.pivot(4.0F, 9.5F, 0.5F));
		root.addChild(MODEL_KEY_RIGHT_ARM, ModelPartBuilder.create()
						.uv(20, 16).cuboid(MODEL_KEY_RIGHT_ARM, -1.0F, -1.5F, -1.5F, 2.0F, 9.0F, 2.0F, new Dilation(0.75F)),
				ModelTransform.pivot(-4.0F, 9.5F, 0.5F));
		root.addChild(MODEL_KEY_LEFT_LEG, ModelPartBuilder.create()
						.uv(50, 16).cuboid(MODEL_KEY_LEFT_LEG, -1.5F, 0.0F, -2.0F, 3.0F, 7.0F, 4.0F, new Dilation(0.5F)),
				ModelTransform.pivot(1.5F, 17.0F, 0.0F));
		root.addChild(MODEL_KEY_RIGHT_LEG, ModelPartBuilder.create()
						.uv(36, 16).cuboid(MODEL_KEY_RIGHT_LEG, -1.5F, 0.0F, -2.0F, 3.0F, 7.0F, 4.0F, new Dilation(0.5F)),
				ModelTransform.pivot(-1.5F, 17.0F, 0.0F));

		return TexturedModelData.of(modelData, 64, 64);
	}

	public MaidArmorGeoLayer(GeoRenderer<LittleMaidEntity> geoRenderer) {
		super(geoRenderer);
	}

	@Nullable
	@Override
	protected ItemStack getArmorItemForBone(GeoBone bone, LittleMaidEntity maid) {
		return switch (bone.getName()) {
			case MaidGeoModel.KEY_ARMOR_HEAD -> maid.getEquippedStack(EquipmentSlot.HEAD);
			case MaidGeoModel.KEY_ARMOR_LEFT_LEG, MaidGeoModel.KEY_ARMOR_RIGHT_LEG -> maid.getEquippedStack(EquipmentSlot.FEET);
			default -> super.getArmorItemForBone(bone, maid);
		};
	}

	@NotNull
	@Override
	protected EquipmentSlot getEquipmentSlotForBone(GeoBone bone, ItemStack stack, LittleMaidEntity maid) {
		return switch (bone.getName()) {
			case MaidGeoModel.KEY_ARMOR_HEAD -> EquipmentSlot.HEAD;
			case MaidGeoModel.KEY_ARMOR_LEFT_LEG, MaidGeoModel.KEY_ARMOR_RIGHT_LEG -> EquipmentSlot.FEET;
			default -> super.getEquipmentSlotForBone(bone, stack, maid);
		};
	}

	@NotNull
	@Override
	protected ModelPart getModelPartForBone(GeoBone bone, EquipmentSlot slot, ItemStack stack, LittleMaidEntity maid, BipedEntityModel<?> baseModel) {
		return switch (bone.getName()) {
			case MaidGeoModel.KEY_ARMOR_HEAD -> baseModel.head;
			case MaidGeoModel.KEY_ARMOR_LEFT_LEG -> baseModel.leftLeg;
			case MaidGeoModel.KEY_ARMOR_RIGHT_LEG -> baseModel.rightLeg;
			default -> super.getModelPartForBone(bone, slot, stack, maid, baseModel);
		};
	}

	// バニラのアーマーのモデルとしてメイドさんのモデルを使用する
	@NotNull
	@Override
	protected BipedEntityModel<?> getModelForItem(GeoBone bone, EquipmentSlot slot, ItemStack stack, LittleMaidEntity maid) {
		return RenderProvider.of(stack).getHumanoidArmorModel(null, stack, null, MODEL);
	}

	// バニラのアーマーのテクスチャとしてメイドさん専用のものを使用する
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
