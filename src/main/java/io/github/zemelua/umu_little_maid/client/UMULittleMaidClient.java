package io.github.zemelua.umu_little_maid.client;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.client.model.entity.LittleMaidEntityModel;
import io.github.zemelua.umu_little_maid.client.renderer.entity.LittleMaidEntityRenderer;
import io.github.zemelua.umu_little_maid.client.screen.LittleMaidScreen;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.inventory.ModInventories;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.screen.PlayerScreenHandler;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@Environment(EnvType.CLIENT)
public class UMULittleMaidClient implements ClientModInitializer {
	public static final Marker MARKER = MarkerManager.getMarker("UMU_LITTLE_MAID_CLIENT");

	public static final EntityModelLayer LAYER_LITTLE_MAID = new EntityModelLayer(UMULittleMaid.identifier("little_maid"), "main");

	public static final Animation MAID_EAT_ANIMATION = Animation.Builder.create(0.5F).looping()
			.addBoneAnimation(EntityModelPartNames.HEAD, new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.method_41829(10.0F, 0.0F, 0.0F), Transformation.Interpolations.field_37885),
					new Keyframe(0.2F, AnimationHelper.method_41829(30.0F, 0.0F, 0.0F), Transformation.Interpolations.field_37885),
					new Keyframe(0.3F, AnimationHelper.method_41829(30.0F, 0.0F, 0.0F), Transformation.Interpolations.field_37885),
					new Keyframe(0.5F, AnimationHelper.method_41829(10.0F, 0.0F, 0.0F), Transformation.Interpolations.field_37885)))
			.addBoneAnimation("left_arm", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.method_41829(-90.5F, 22.9F, 0.0F), Transformation.Interpolations.field_37884)))
			.addBoneAnimation(EntityModelPartNames.RIGHT_ARM, new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0F, AnimationHelper.method_41829(-90.5F, -22.9F, 0.0F), Transformation.Interpolations.field_37884)))
			.build();

	public static final Animation FROG_WALK_ANIMATION = Animation.Builder.create(1.25f).looping()
			.addBoneAnimation("left_arm", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0f, AnimationHelper.method_41829(0.0f, -5.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.2917f, AnimationHelper.method_41829(7.5f, -2.67f, -7.5f), Transformation.Interpolations.field_37884),
					new Keyframe(0.625f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.7917f, AnimationHelper.method_41829(22.5f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(1.125f, AnimationHelper.method_41829(-45.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(1.25f, AnimationHelper.method_41829(0.0f, -5.0f, 0.0f), Transformation.Interpolations.field_37884)))
			.addBoneAnimation("left_arm", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0f, AnimationHelper.method_41823(0.0f, 0.1f, -2.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.2917f, AnimationHelper.method_41823(-0.5f, -0.25f, -0.13f), Transformation.Interpolations.field_37884),
					new Keyframe(0.625f, AnimationHelper.method_41823(-0.5f, 0.1f, 2.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.9583f, AnimationHelper.method_41823(0.5f, 1.0f, -0.11f), Transformation.Interpolations.field_37884),
					new Keyframe(1.25f, AnimationHelper.method_41823(0.0f, 0.1f, -2.0f), Transformation.Interpolations.field_37884)))
			.addBoneAnimation("right_arm", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.125f, AnimationHelper.method_41829(22.5f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.4583f, AnimationHelper.method_41829(-45.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.625f, AnimationHelper.method_41829(0.0f, 5.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.9583f, AnimationHelper.method_41829(7.5f, 2.33f, 7.5f), Transformation.Interpolations.field_37884),
					new Keyframe(1.25f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884)))
			.addBoneAnimation("right_arm", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0f, AnimationHelper.method_41823(0.5f, 0.1f, 2.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.2917f, AnimationHelper.method_41823(-0.5f, 1.0f, 0.12f), Transformation.Interpolations.field_37884),
					new Keyframe(0.625f, AnimationHelper.method_41823(0.0f, 0.1f, -2.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.9583f, AnimationHelper.method_41823(0.5f, -0.25f, -0.13f), Transformation.Interpolations.field_37884),
					new Keyframe(1.25f, AnimationHelper.method_41823(0.5f, 0.1f, 2.0f), Transformation.Interpolations.field_37884)))
			.addBoneAnimation("left_leg", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.1667f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.2917f, AnimationHelper.method_41829(45.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.625f, AnimationHelper.method_41829(-45.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.7917f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(1.25f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884)))
			.addBoneAnimation("left_leg", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0f, AnimationHelper.method_41823(0.0f, 0.1f, 1.2f), Transformation.Interpolations.field_37884),
					new Keyframe(0.1667f, AnimationHelper.method_41823(0.0f, 0.1f, 2.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.4583f, AnimationHelper.method_41823(0.0f, 2.0f, 1.06f), Transformation.Interpolations.field_37884),
					new Keyframe(0.7917f, AnimationHelper.method_41823(0.0f, 0.1f, -1.0f), Transformation.Interpolations.field_37884),
					new Keyframe(1.25f, AnimationHelper.method_41823(0.0f, 0.1f, 1.2f), Transformation.Interpolations.field_37884)))
			.addBoneAnimation("right_leg", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0f, AnimationHelper.method_41829(-33.75f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.0417f, AnimationHelper.method_41829(-45.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.1667f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.7917f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.9583f, AnimationHelper.method_41829(45.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(1.25f, AnimationHelper.method_41829(-33.75f, 0.0f, 0.0f), Transformation.Interpolations.field_37884)))
			.addBoneAnimation("right_leg", new Transformation(Transformation.Targets.TRANSLATE,
					new Keyframe(0.0f, AnimationHelper.method_41823(0.0f, 1.14f, 0.11f), Transformation.Interpolations.field_37884),
					new Keyframe(0.1667f, AnimationHelper.method_41823(0.0f, 0.1f, -1.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.7917f, AnimationHelper.method_41823(0.0f, 0.1f, 2.0f), Transformation.Interpolations.field_37884),
					new Keyframe(1.125f, AnimationHelper.method_41823(0.0f, 2.0f, 0.95f), Transformation.Interpolations.field_37884),
					new Keyframe(1.25f, AnimationHelper.method_41823(0.0f, 1.14f, 0.11f), Transformation.Interpolations.field_37884)))
			.addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE,
					new Keyframe(0.0f, AnimationHelper.method_41829(0.0f, 5.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.2917f, AnimationHelper.method_41829(-7.5f, 0.33f, 7.5f), Transformation.Interpolations.field_37884),
					new Keyframe(0.625f, AnimationHelper.method_41829(0.0f, -5.0f, 0.0f), Transformation.Interpolations.field_37884),
					new Keyframe(0.9583f, AnimationHelper.method_41829(-7.5f, 0.33f, -7.5f), Transformation.Interpolations.field_37884),
					new Keyframe(1.25f, AnimationHelper.method_41829(0.0f, 5.0f, 0.0f), Transformation.Interpolations.field_37884)))
			.build();

	@Override
	public void onInitializeClient() {
		UMULittleMaid.LOGGER.info(UMULittleMaidClient.MARKER, "Start initializing mod client!");

		EntityRendererRegistry.register(ModEntities.LITTLE_MAID, LittleMaidEntityRenderer::new);
		HandledScreens.register(ModInventories.LITTLE_MAID, LittleMaidScreen::new);

		EntityModelLayerRegistry.registerModelLayer(UMULittleMaidClient.LAYER_LITTLE_MAID, LittleMaidEntityModel::getTexturedModelData);

		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
			registry.register(LittleMaidScreen.EMPTY_HELD_SLOT_TEXTURE);
			registry.register(LittleMaidScreen.EMPTY_ARMOR_SLOT_TEXTURES[0]);
			registry.register(LittleMaidScreen.EMPTY_ARMOR_SLOT_TEXTURES[3]);
		});

		UMULittleMaid.LOGGER.info(UMULittleMaidClient.MARKER, "Succeeded initializing mod client!");
	}

	static {
//		MAID_WALK_ANIMATION = Animation.Builder.create(1.25f)
//				.looping().addBoneAnimation("left_arm", new Transformation(Transformation.Targets.ROTATE,
//						new Keyframe(0.0f, AnimationHelper.method_41829(0.0f, -5.0f, 0.0f), Transformation.Interpolations.field_37884),
//						new Keyframe(0.2917f, AnimationHelper.method_41829(7.5f, -2.67f, -7.5f), Transformation.Interpolations.field_37884),
//						new Keyframe(0.625f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
//						new Keyframe(0.7917f, AnimationHelper.method_41829(22.5f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
//						new Keyframe(1.125f, AnimationHelper.method_41829(-45.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884),
//						new Keyframe(1.25f, AnimationHelper.method_41829(0.0f, -5.0f, 0.0f), Transformation.Interpolations.field_37884))).addBoneAnimation("left_arm", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.method_41823(0.0f, 0.1f, -2.0f), Transformation.Interpolations.field_37884), new Keyframe(0.2917f, AnimationHelper.method_41823(-0.5f, -0.25f, -0.13f), Transformation.Interpolations.field_37884), new Keyframe(0.625f, AnimationHelper.method_41823(-0.5f, 0.1f, 2.0f), Transformation.Interpolations.field_37884), new Keyframe(0.9583f, AnimationHelper.method_41823(0.5f, 1.0f, -0.11f), Transformation.Interpolations.field_37884), new Keyframe(1.25f, AnimationHelper.method_41823(0.0f, 0.1f, -2.0f), Transformation.Interpolations.field_37884))).addBoneAnimation("right_arm", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(0.125f, AnimationHelper.method_41829(22.5f, 0.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(0.4583f, AnimationHelper.method_41829(-45.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(0.625f, AnimationHelper.method_41829(0.0f, 5.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(0.9583f, AnimationHelper.method_41829(7.5f, 2.33f, 7.5f), Transformation.Interpolations.field_37884), new Keyframe(1.25f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884))).addBoneAnimation("right_arm", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.method_41823(0.5f, 0.1f, 2.0f), Transformation.Interpolations.field_37884), new Keyframe(0.2917f, AnimationHelper.method_41823(-0.5f, 1.0f, 0.12f), Transformation.Interpolations.field_37884), new Keyframe(0.625f, AnimationHelper.method_41823(0.0f, 0.1f, -2.0f), Transformation.Interpolations.field_37884), new Keyframe(0.9583f, AnimationHelper.method_41823(0.5f, -0.25f, -0.13f), Transformation.Interpolations.field_37884), new Keyframe(1.25f, AnimationHelper.method_41823(0.5f, 0.1f, 2.0f), Transformation.Interpolations.field_37884))).addBoneAnimation("left_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(0.1667f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(0.2917f, AnimationHelper.method_41829(45.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(0.625f, AnimationHelper.method_41829(-45.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(0.7917f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(1.25f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884))).addBoneAnimation("left_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.method_41823(0.0f, 0.1f, 1.2f), Transformation.Interpolations.field_37884), new Keyframe(0.1667f, AnimationHelper.method_41823(0.0f, 0.1f, 2.0f), Transformation.Interpolations.field_37884), new Keyframe(0.4583f, AnimationHelper.method_41823(0.0f, 2.0f, 1.06f), Transformation.Interpolations.field_37884), new Keyframe(0.7917f, AnimationHelper.method_41823(0.0f, 0.1f, -1.0f), Transformation.Interpolations.field_37884), new Keyframe(1.25f, AnimationHelper.method_41823(0.0f, 0.1f, 1.2f), Transformation.Interpolations.field_37884))).addBoneAnimation("right_leg", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.method_41829(-33.75f, 0.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(0.0417f, AnimationHelper.method_41829(-45.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(0.1667f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(0.7917f, AnimationHelper.method_41829(0.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(0.9583f, AnimationHelper.method_41829(45.0f, 0.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(1.25f, AnimationHelper.method_41829(-33.75f, 0.0f, 0.0f), Transformation.Interpolations.field_37884))).addBoneAnimation("right_leg", new Transformation(Transformation.Targets.TRANSLATE, new Keyframe(0.0f, AnimationHelper.method_41823(0.0f, 1.14f, 0.11f), Transformation.Interpolations.field_37884), new Keyframe(0.1667f, AnimationHelper.method_41823(0.0f, 0.1f, -1.0f), Transformation.Interpolations.field_37884), new Keyframe(0.7917f, AnimationHelper.method_41823(0.0f, 0.1f, 2.0f), Transformation.Interpolations.field_37884), new Keyframe(1.125f, AnimationHelper.method_41823(0.0f, 2.0f, 0.95f), Transformation.Interpolations.field_37884), new Keyframe(1.25f, AnimationHelper.method_41823(0.0f, 1.14f, 0.11f), Transformation.Interpolations.field_37884))).addBoneAnimation("body", new Transformation(Transformation.Targets.ROTATE, new Keyframe(0.0f, AnimationHelper.method_41829(0.0f, 5.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(0.2917f, AnimationHelper.method_41829(-7.5f, 0.33f, 7.5f), Transformation.Interpolations.field_37884), new Keyframe(0.625f, AnimationHelper.method_41829(0.0f, -5.0f, 0.0f), Transformation.Interpolations.field_37884), new Keyframe(0.9583f, AnimationHelper.method_41829(-7.5f, 0.33f, -7.5f), Transformation.Interpolations.field_37884), new Keyframe(1.25f, AnimationHelper.method_41829(0.0f, 5.0f, 0.0f), Transformation.Interpolations.field_37884))).build();
	}
}
