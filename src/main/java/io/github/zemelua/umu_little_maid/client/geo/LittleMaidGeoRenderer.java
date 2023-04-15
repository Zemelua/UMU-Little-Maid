package io.github.zemelua.umu_little_maid.client.geo;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;

public class LittleMaidGeoRenderer extends ExtendedGeoEntityRenderer<LittleMaidEntity> {
	public LittleMaidGeoRenderer(EntityRendererFactory.Context renderManager) {
		super(renderManager, new LittleMaidGeoModel());
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
	protected boolean isArmorBone(GeoBone bone) {
		return bone.getName().startsWith("armor");
	}

	@Override
	@Nullable
	protected Identifier getTextureForBone(String boneName, LittleMaidEntity currentEntity) {
//		if (boneName.startsWith("armor")) {
//			return UMULittleMaid.identifier("textures/item/armor/maid_diamond.png");
//		}

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
