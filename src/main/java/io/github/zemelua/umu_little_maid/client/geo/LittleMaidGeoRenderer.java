package io.github.zemelua.umu_little_maid.client.geo;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.client.renderer.entity.feature.MaidArmorRenderer;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class LittleMaidGeoRenderer extends ExtendedGeoEntityRenderer<LittleMaidEntity> {
	public static BipedEntityModel<LivingEntity> ARMOR_MODEL = new BipedEntityModel<>(MaidArmorRenderer.createModelData().createModel());

	public LittleMaidGeoRenderer(EntityRendererFactory.Context renderManager) {
		super(renderManager, new LittleMaidGeoModel());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void renderEarly(LittleMaidEntity animatable, MatrixStack poseStack, float partialTick, VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
		this.modelProvider.getAnimationProcessor().getModelRendererList().stream()
				.filter(bone -> bone instanceof GeoBone)
				.filter(bone -> this.isArmorBone((GeoBone) bone))
				.forEach(bone -> ((GeoBone) bone).setHidden(true));

		super.renderEarly(animatable, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, partialTicks);
	}

	@Override
	@Nullable
	protected ItemStack getArmorForBone(String boneName, LittleMaidEntity currentEntity) {
		return switch (boneName) {
			case LittleMaidGeoModel.KEY_ARMOR_HEAD -> currentEntity.getEquippedStack(EquipmentSlot.HEAD);
			default -> null;
		};
	}

	@Nullable @Override
	protected EquipmentSlot getEquipmentSlotForArmorBone(String boneName, LittleMaidEntity currentEntity) {
		return switch (boneName) {
			case LittleMaidGeoModel.KEY_ARMOR_HEAD -> EquipmentSlot.HEAD;
			default -> null;
		};
	}

	@Nullable @Override
	protected ModelPart getArmorPartForBone(String name, BipedEntityModel<?> armorModel) {
		return switch (name) {
			case LittleMaidGeoModel.KEY_ARMOR_HEAD -> armorModel.head;
			default -> null;
		};
	}

	@Override
	protected Identifier getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @NotNull String type) {
		return UMULittleMaid.identifier("textures/item/armor/maid_diamond.png");
	}

	@Override
	protected boolean isArmorBone(GeoBone bone) {
		return bone.getName().startsWith("armor");
	}

	@Override
	@Nullable
	protected Identifier getTextureForBone(String boneName, LittleMaidEntity currentEntity) {

		return null;
	}



	@Override
	@Nullable
	protected ItemStack getHeldItemForBone(String boneName, LittleMaidEntity currentEntity) {
		return null;
	}

	@Override
	protected ModelTransformation.Mode getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
		return ModelTransformation.Mode.NONE;
	}

	@Override
	@Nullable
	protected BlockState getHeldBlockForBone(String boneName, LittleMaidEntity currentEntity) {
		return null;
	}

	@Override
	protected void preRenderItem(MatrixStack PoseStack, ItemStack item, String boneName, LittleMaidEntity currentEntity, IBone bone) {

	}

	@Override
	protected void preRenderBlock(MatrixStack PoseStack, BlockState block, String boneName, LittleMaidEntity currentEntity) {

	}

	@Override
	protected void postRenderItem(MatrixStack PoseStack, ItemStack item, String boneName, LittleMaidEntity currentEntity, IBone bone) {

	}

	@Override
	protected void postRenderBlock(MatrixStack PoseStack, BlockState block, String boneName, LittleMaidEntity currentEntity) {

	}
}
