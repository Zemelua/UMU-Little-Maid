package io.github.zemelua.umu_little_maid.client.geo.renderer;

import io.github.zemelua.umu_little_maid.client.geo.layer.MaidArmorGeoLayer;
import io.github.zemelua.umu_little_maid.client.geo.layer.MaidHeldItemGeoLayer;
import io.github.zemelua.umu_little_maid.client.geo.model.MaidGeoModel;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.molang.MolangParser;
import software.bernie.geckolib.renderer.DynamicGeoEntityRenderer;

public class MaidGeoRenderer extends DynamicGeoEntityRenderer<LittleMaidEntity> {
	public MaidGeoRenderer(EntityRendererFactory.Context renderManager) {
		super(renderManager, new MaidGeoModel());

		this.addRenderLayer(new MaidArmorGeoLayer(this));
		this.addRenderLayer(new MaidHeldItemGeoLayer(this));
	}

	@Override
	public void preRender(MatrixStack poseStack, LittleMaidEntity maid, BakedGeoModel model,
	                      @Nullable VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer,
	                      boolean isReRender, float partialTick, int packedLight, int packedOverlay,
	                      float red, float green, float blue, float alpha) {
//		// アーマーボーンのテクスチャの一部がアーマーのテクスチャに置き換わる謎のバグがあるため
//		this.modelProvider.getAnimationProcessor().getModelRendererList().stream()
//				.filter(bone -> bone instanceof GeoBone)
//				.filter(bone -> this.isArmorBone((GeoBone) bone))
//				.forEach(bone -> ((GeoBone) bone).setHidden(true));

		CoreGeoBone head = this.model.getBone(MaidGeoModel.KEY_HEAD).get();
		MolangParser.INSTANCE.setValue("query.maid.head_pitch", head::getRotX);
		MolangParser.INSTANCE.setValue("query.maid.head_yaw", head::getRotY);

//		IBone leftArm = this.modelProvider.getBone(LittleMaidGeoModel.KEY_LEFT_ARM);
//		IBone rightArm = this.modelProvider.getBone(LittleMaidGeoModel.KEY_RIGHT_ARM);
//		IMaidArmsPose armsPose = getArmsPose(maid);
//		armsPose.setArmsPose(maid, leftArm, rightArm, this.modelProvider);

		super.preRender(poseStack, maid, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

//	@Override
//	protected boolean isArmorBone(GeoBone bone) {
//		return bone.getName().startsWith("armor");
//	}
}
