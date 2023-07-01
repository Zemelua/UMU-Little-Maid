package io.github.zemelua.umu_little_maid.client.geo.layer;

import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.util.ModMathUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class LittleMaidHeldItemGeoLayer extends BlockAndItemGeoLayer<LittleMaidEntity> {
	private static final String KEY_ITEM_LEFT_HAND = "itemLeftHand";
	private static final String KEY_ITEM_RIGHT_HAND = "itemRightHand";
	private static final String KEY_DRIPLEAF_RIGHT_HAND = "dripleaf_right_hand";

	public LittleMaidHeldItemGeoLayer(GeoRenderer<LittleMaidEntity> renderer) {
		super(renderer);
	}

	@Nullable
	@Override
	protected ItemStack getStackForBone(GeoBone bone, LittleMaidEntity maid) {
		ItemStack mainStack = maid.getMainHandStack();
		ItemStack offStack = maid.getOffHandStack();

		return switch (bone.getName()) {
			case KEY_ITEM_LEFT_HAND -> maid.isLeftHanded() ? mainStack : offStack;
			case KEY_ITEM_RIGHT_HAND -> maid.isLeftHanded() ? offStack : mainStack;
			default -> null;
		};
	}

	@Nullable
	@Override
	protected BlockState getBlockForBone(GeoBone bone, LittleMaidEntity maid) {
		if (maid.isGliding()) {
			if (bone.getName().equals(KEY_DRIPLEAF_RIGHT_HAND)) {
				return Blocks.BIG_DRIPLEAF.getDefaultState();
			}
		}

		return null;
	}

	@Override
	protected ModelTransformationMode getTransformTypeForStack(GeoBone bone, ItemStack stack, LittleMaidEntity maid) {
		return switch (bone.getName()) {
			case KEY_ITEM_LEFT_HAND -> ModelTransformationMode.THIRD_PERSON_LEFT_HAND;
			case KEY_ITEM_RIGHT_HAND -> ModelTransformationMode.THIRD_PERSON_RIGHT_HAND;
			default -> ModelTransformationMode.NONE;
		};
	}

	@Override
	protected void renderStackForBone(MatrixStack matrices, GeoBone bone, ItemStack stack, LittleMaidEntity maid,
	                                  VertexConsumerProvider bufferSource, float partialTick, int packedLight, int packedOverlay) {
		ItemStack mainStack = maid.getMainHandStack();
		ItemStack offStack = maid.getOffHandStack();

		ModMathUtils.scaleMatrices(matrices, 0.7F);
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
		if (stack.equals(mainStack)) {
			if (stack.isOf(Items.BOW)) {
				matrices.translate(0.05D, 0.0D, -0.05D);
			} else if (stack.getItem() instanceof ShieldItem) {
				matrices.translate(0.0F, 0.125F, -0.25F);
			} else if (stack.isOf(Items.CROSSBOW)) {
				matrices.translate(0.0D, 0.04D, 0.0D);
			}
		}
		else if (stack.equals(offStack)) {
			if (stack.getItem() instanceof ShieldItem) {
				matrices.translate(0.0F, 0.125F, 0.25F);
				matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
			}
		}

		super.renderStackForBone(matrices, bone, stack, maid, bufferSource, partialTick, packedLight, packedOverlay);
	}

	@Override
	protected void renderBlockForBone(MatrixStack matrices, GeoBone bone, BlockState state, LittleMaidEntity maid,
	                                  VertexConsumerProvider bufferSource, float partialTick, int packedLight, int packedOverlay) {
		ModMathUtils.scaleMatrices(matrices, 2.0F);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
		matrices.translate(0.0D, 0.2D, -0.15D);

		super.renderBlockForBone(matrices, bone, state, maid, bufferSource, partialTick, packedLight, packedOverlay);
	}
}
