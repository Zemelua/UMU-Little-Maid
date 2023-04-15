package io.github.zemelua.umu_little_maid.client.renderer.entity.feature;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.client.model.entity.LittleMaidEntityModel;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import static io.github.zemelua.umu_little_maid.client.model.entity.LittleMaidEntityModel.*;

public class MaidArmorRenderer extends FeatureRenderer<LittleMaidEntity, LittleMaidEntityModel> {
	public static final BipedEntityModel<LivingEntity> MODEL = new BipedEntityModel<>(MaidArmorRenderer.createModelData().createModel());
	public static final Identifier TEXTURE_LEATHER = UMULittleMaid.identifier("textures/item/armor/maid_leather.png");
	public static final Identifier TEXTURE_LEATHER_OVERLAY = UMULittleMaid.identifier("textures/item/armor/maid_leather_overlay.png");
	public static final Identifier TEXTURE_IRON = UMULittleMaid.identifier("textures/item/armor/maid_iron.png");
	public static final Identifier TEXTURE_GOLD = UMULittleMaid.identifier("textures/item/armor/maid_gold.png");
	public static final Identifier TEXTURE_DIAMOND = UMULittleMaid.identifier("textures/item/armor/maid_diamond.png");
	public static final Identifier TEXTURE_NETHERITE = UMULittleMaid.identifier("textures/item/armor/maid_netherite.png");
	public static final Identifier TEXTURE_CHAINMAIL = UMULittleMaid.identifier("textures/item/armor/maid_chainmail.png");
	public static final Identifier TEXTURE_TURTLE = UMULittleMaid.identifier("textures/item/armor/maid_turtle.png");


	private final LittleMaidEntityModel model;

	public MaidArmorRenderer(FeatureRendererContext<LittleMaidEntity, LittleMaidEntityModel> context) {
		super(context);

		this.model = new LittleMaidEntityModel(createModelData().createModel());
	}

	public static TexturedModelData createModelData() {
		ModelData modelData = new ModelData();
		ModelPartData root = modelData.getRoot();

		root.addChild(KEY_HEAD, ModelPartBuilder.create()
						.uv(0, 0).cuboid(KEY_HEAD, -4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.75F)),
				ModelTransform.pivot(0.0F, 8.0F, 0.0F));
		root.addChild(KEY_HAT, ModelPartBuilder.create(), ModelTransform.NONE);
		root.addChild(KEY_BODY, ModelPartBuilder.create()
						.uv(0, 16).cuboid(KEY_BODY, -3.0F, 0.0F, -2.0F, 6.0F, 9.0F, 4.0F, new Dilation(0.75F)),
				ModelTransform.pivot(0.0F, 8.0F, 0.0F));
		root.addChild(KEY_SKIRT, ModelPartBuilder.create()
						.uv(32, 0).cuboid(KEY_SKIRT, -4.0F, 0.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.75F)),
				ModelTransform.pivot(0.0F, 14.0F, 0.0F));
		root.addChild(KEY_LEFT_ARM, ModelPartBuilder.create()
						.uv(28, 16).cuboid(KEY_LEFT_ARM, -1.0F, -1.5F, -1.5F, 2.0F, 9.0F, 2.0F, new Dilation(0.75F)),
				ModelTransform.pivot(4.0F, 9.5F, 0.5F));
		root.addChild(KEY_RIGHT_ARM, ModelPartBuilder.create()
						.uv(20, 16).cuboid(KEY_RIGHT_ARM, -1.0F, -1.5F, -1.5F, 2.0F, 9.0F, 2.0F, new Dilation(0.75F)),
				ModelTransform.pivot(-4.0F, 9.5F, 0.5F));
		root.addChild(KEY_LEFT_LEG, ModelPartBuilder.create()
						.uv(50, 16).cuboid(KEY_LEFT_LEG, -1.5F, 0.0F, -2.0F, 3.0F, 7.0F, 4.0F, new Dilation(0.5F)),
				ModelTransform.pivot(1.5F, 17.0F, 0.0F));
		root.addChild(KEY_RIGHT_LEG, ModelPartBuilder.create()
						.uv(36, 16).cuboid(KEY_RIGHT_LEG, -1.5F, 0.0F, -2.0F, 3.0F, 7.0F, 4.0F, new Dilation(0.5F)),
				ModelTransform.pivot(-1.5F, 17.0F, 0.0F));

		root.addChild(KEY_BONE_USING_DRIPLEAF, ModelPartBuilder.create(), ModelTransform.NONE);
		root.addChild(KEY_BONE_CHANGING_COSTUME, ModelPartBuilder.create(), ModelTransform.NONE);

		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LittleMaidEntity maid, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		this.renderArmor(matrices, vertexConsumers, maid, EquipmentSlot.HEAD, light);
		this.renderArmor(matrices, vertexConsumers, maid, EquipmentSlot.FEET, light);
	}

	private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LittleMaidEntity maid, EquipmentSlot slot, int light) {
		ItemStack armor = maid.getEquippedStack(slot);
		if (!(armor.getItem() instanceof ArmorItem armorItem)) return;

		if (armorItem.getSlotType() != slot) {
			return;
		}
		this.getContextModel().copyStateTo(this.model);
		LittleMaidEntityModel.setVisible(this.model, slot);
		boolean hasGlint = armor.hasGlint();
		if (armorItem instanceof DyeableArmorItem) {
			int i = ((DyeableArmorItem)armorItem).getColor(armor);
			float f = (float)(i >> 16 & 0xFF) / 255.0f;
			float g = (float)(i >> 8 & 0xFF) / 255.0f;
			float h = (float)(i & 0xFF) / 255.0f;
			this.renderArmorParts(matrices, vertexConsumers, light, armorItem, hasGlint, model, f, g, h, null);
			this.renderArmorParts(matrices, vertexConsumers, light, armorItem, hasGlint, model, 1.0f, 1.0f, 1.0f, "overlay");
		} else {
			this.renderArmorParts(matrices, vertexConsumers, light, armorItem, hasGlint, model, 1.0f, 1.0f, 1.0f, null);
		}
	}

	private void renderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item,
	                              boolean usesSecondLayer, LittleMaidEntityModel model,
	                              float red, float green, float blue, @Nullable String overlay) {
		VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(item, overlay)), false, usesSecondLayer);
		(model).render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, red, green, blue, 1.0f);
	}

	private Identifier getArmorTexture(ArmorItem item, @Nullable String overlay) {
		String string = "textures/item/armor/maid_" + item.getMaterial().getName() + (overlay == null ? "" : "_" + overlay) + ".png";

		return UMULittleMaid.identifier(string);
	}
}
