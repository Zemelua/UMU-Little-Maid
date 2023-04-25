package io.github.zemelua.umu_little_maid.client.geo;

import io.github.zemelua.umu_little_maid.client.renderer.entity.feature.MaidArmorRenderer;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.ModMathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.ExtendedGeoEntityRenderer;
import software.bernie.geckolib3.resource.GeckoLibCache;

public class LittleMaidGeoRenderer extends ExtendedGeoEntityRenderer<LittleMaidEntity> {

	public LittleMaidGeoRenderer(EntityRendererFactory.Context renderManager) {
		super(renderManager, new LittleMaidGeoModel());
	}

	@Override
	@SuppressWarnings("unchecked")
	public void renderEarly(LittleMaidEntity maid, MatrixStack poseStack, float partialTick, VertexConsumerProvider bufferSource, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float partialTicks) {
		// アーマーボーンのテクスチャの一部がアーマーのテクスチャに置き換わる謎のバグがあるため
		this.modelProvider.getAnimationProcessor().getModelRendererList().stream()
				.filter(bone -> bone instanceof GeoBone)
				.filter(bone -> this.isArmorBone((GeoBone) bone))
				.forEach(bone -> ((GeoBone) bone).setHidden(true));

		IBone head = this.modelProvider.getBone(LittleMaidGeoModel.KEY_HEAD);
		GeckoLibCache.getInstance().parser.setValue("query.maid.head_pitch", head::getRotationX);
		GeckoLibCache.getInstance().parser.setValue("query.maid.head_yaw", head::getRotationY);

//		IBone leftArm = this.modelProvider.getBone(LittleMaidGeoModel.KEY_LEFT_ARM);
//		IBone rightArm = this.modelProvider.getBone(LittleMaidGeoModel.KEY_RIGHT_ARM);
//		IMaidArmsPose armsPose = getArmsPose(maid);
//		armsPose.setArmsPose(maid, leftArm, rightArm, this.modelProvider);

		super.renderEarly(maid, poseStack, partialTick, bufferSource, buffer, packedLight, packedOverlay, red, green, blue, partialTicks);
	}

	@Override
	@Nullable
	protected Identifier getTextureForBone(String boneName, LittleMaidEntity currentEntity) {
		return null;
	}

	@Override
	@Nullable
	protected ItemStack getArmorForBone(String boneName, LittleMaidEntity currentEntity) {
		return switch (boneName) {
			case LittleMaidGeoModel.KEY_ARMOR_HEAD -> currentEntity.getEquippedStack(EquipmentSlot.HEAD);
			case LittleMaidGeoModel.KEY_ARMOR_LEFT_LEG,
					LittleMaidGeoModel.KEY_ARMOR_RIGHT_LEG -> currentEntity.getEquippedStack(EquipmentSlot.FEET);
			default -> null;
		};
	}

	@Override
	@Nullable
	protected EquipmentSlot getEquipmentSlotForArmorBone(String boneName, LittleMaidEntity currentEntity) {
		return switch (boneName) {
			case LittleMaidGeoModel.KEY_ARMOR_HEAD -> EquipmentSlot.HEAD;
			case LittleMaidGeoModel.KEY_ARMOR_LEFT_LEG,
					LittleMaidGeoModel.KEY_ARMOR_RIGHT_LEG -> EquipmentSlot.FEET;
			default -> null;
		};
	}

	@Override
	@Nullable
	protected ModelPart getArmorPartForBone(String boneName, BipedEntityModel<?> armorModel) {
		return switch (boneName) {
			case LittleMaidGeoModel.KEY_ARMOR_HEAD -> armorModel.head;
			case LittleMaidGeoModel.KEY_ARMOR_LEFT_LEG -> armorModel.leftLeg;
			case LittleMaidGeoModel.KEY_ARMOR_RIGHT_LEG -> armorModel.rightLeg;
			default -> null;
		};
	}

	@Override
	@SuppressWarnings("ConstantConditions")
	protected Identifier getArmorResource(Entity entity, ItemStack stack, EquipmentSlot slot, @NotNull String type) {
		ArmorItem armor = (ArmorItem) stack.getItem();
		ArmorMaterial material = armor.getMaterial();

		if (material.equals(ArmorMaterials.LEATHER)) {
			if (type == null)                return MaidArmorRenderer.TEXTURE_LEATHER;
			else if (type.equals("overlay")) return MaidArmorRenderer.TEXTURE_LEATHER_OVERLAY;
		} else if (material.equals(ArmorMaterials.IRON))    return MaidArmorRenderer.TEXTURE_IRON;
		else if (material.equals(ArmorMaterials.GOLD))      return MaidArmorRenderer.TEXTURE_GOLD;
		else if (material.equals(ArmorMaterials.DIAMOND))   return MaidArmorRenderer.TEXTURE_DIAMOND;
		else if (material.equals(ArmorMaterials.NETHERITE)) return MaidArmorRenderer.TEXTURE_NETHERITE;
		else if (material.equals(ArmorMaterials.CHAIN))     return MaidArmorRenderer.TEXTURE_CHAINMAIL;
		else if (material.equals(ArmorMaterials.TURTLE))    return MaidArmorRenderer.TEXTURE_TURTLE;

		return super.getArmorResource(entity, stack, slot, type);
	}

	@Override
	protected boolean isArmorBone(GeoBone bone) {
		return bone.getName().startsWith("armor");
	}

	@Override
	@Nullable
	protected ItemStack getHeldItemForBone(String boneName, LittleMaidEntity currentEntity) {
		ItemStack mainStack = currentEntity.getMainHandStack();
		ItemStack offStack = currentEntity.getOffHandStack();

		return switch (boneName) {
			case LittleMaidGeoModel.KEY_ITEM_LEFT_HAND -> currentEntity.isLeftHanded() ? mainStack : offStack;
			case LittleMaidGeoModel.KEY_ITEM_RIGHT_HAND -> currentEntity.isLeftHanded() ? offStack : mainStack;
			default -> null;
		};
	}

	@Override
	@Nullable
	protected BlockState getHeldBlockForBone(String boneName, LittleMaidEntity maid) {
		if (maid.isUsingDripleaf()) {
			if (boneName.equals(LittleMaidGeoModel.KEY_DRIPLEAF_RIGHT_HAND)) {
				return Blocks.BIG_DRIPLEAF.getDefaultState();
			}
		}

		return null;
	}

	@Override
	protected ModelTransformation.Mode getCameraTransformForItemAtBone(ItemStack boneItem, String boneName) {
		return switch (boneName) {
			case LittleMaidGeoModel.KEY_ITEM_LEFT_HAND -> ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND;
			case LittleMaidGeoModel.KEY_ITEM_RIGHT_HAND -> ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND;
			default -> ModelTransformation.Mode.NONE;
		};
	}

	@Override
	protected void preRenderItem(MatrixStack matrices, ItemStack item, String boneName, LittleMaidEntity currentEntity, IBone bone) {
		ItemStack mainStack = currentEntity.getMainHandStack();
		ItemStack offStack = currentEntity.getOffHandStack();

		ModMathUtils.scaleMatrices(matrices, 0.7F);
		matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
		if (item.equals(mainStack)) {
			if (item.isOf(Items.BOW)) {
				matrices.translate(0.05D, 0.0D, -0.05D);
			} else if (item.getItem() instanceof ShieldItem) {
				matrices.translate(0.0F, 0.125F, -0.25F);
			} else if (item.isOf(Items.CROSSBOW)) {
				matrices.translate(0.0D, 0.04D, 0.0D);
			}
		}
		else if (item.equals(offStack)) {
			if (item.getItem() instanceof ShieldItem) {
				matrices.translate(0.0F, 0.125F, 0.25F);
				matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
			}
		}
	}

	@Override
	protected void preRenderBlock(MatrixStack matrices, BlockState block, String boneName, LittleMaidEntity currentEntity) {
		ModMathUtils.scaleMatrices(matrices, 2.0F);
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
		matrices.translate(0.0D, 0.2D, -0.15D);
	}

	@Override
	protected void postRenderItem(MatrixStack PoseStack, ItemStack item, String boneName, LittleMaidEntity currentEntity, IBone bone) {}

	@Override
	protected void postRenderBlock(MatrixStack PoseStack, BlockState block, String boneName, LittleMaidEntity currentEntity) {}
}
