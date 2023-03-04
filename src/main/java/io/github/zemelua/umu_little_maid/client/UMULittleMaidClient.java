package io.github.zemelua.umu_little_maid.client;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.client.model.entity.LittleMaidEntityModel;
import io.github.zemelua.umu_little_maid.client.network.ClientNetworkHandler;
import io.github.zemelua.umu_little_maid.client.renderer.InstructionRenderer;
import io.github.zemelua.umu_little_maid.client.renderer.entity.LittleMaidEntityRenderer;
import io.github.zemelua.umu_little_maid.client.screen.LittleMaidScreen;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.inventory.ModInventories;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
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

	public static final Animation ANIMATION_MAID_EAT;
	public static final Animation ANIMATION_MAID_HEAL;
	public static final Animation ANIMATION_MAID_USE_DRIPLEAF_LEFT;
	public static final Animation ANIMATION_MAID_USE_DRIPLEAF_RIGHT;
	public static final Animation ANIMATION_MAID_CHANGE_COSTUME;

	@Override
	public void onInitializeClient() {
		UMULittleMaid.LOGGER.info(UMULittleMaidClient.MARKER, "Start initializing mod client!");

		ClientNetworkHandler.initializeClient();

		EntityRendererRegistry.register(ModEntities.LITTLE_MAID, LittleMaidEntityRenderer::new);
		HandledScreens.register(ModInventories.LITTLE_MAID, LittleMaidScreen::new);

		EntityModelLayerRegistry.registerModelLayer(UMULittleMaidClient.LAYER_LITTLE_MAID, LittleMaidEntityModel::getTexturedModelData);

		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
			registry.register(LittleMaidScreen.EMPTY_HELD_SLOT_TEXTURE);
			registry.register(LittleMaidScreen.EMPTY_HELMET_SLOT_TEXTURE);
			registry.register(LittleMaidScreen.EMPTY_BOOTS_SLOT_TEXTURE);
			registry.register(InstructionRenderer.OVERLAY_AVAILABLE_TEXTURE);
			registry.register(InstructionRenderer.OVERLAY_AVAILABLE_TEXTURE_DOWN);
			registry.register(InstructionRenderer.OVERLAY_AVAILABLE_TEXTURE_UP);
			registry.register(InstructionRenderer.OVERLAY_AVAILABLE_TEXTURE_LEFT);
			registry.register(InstructionRenderer.OVERLAY_AVAILABLE_TEXTURE_RIGHT);
			registry.register(InstructionRenderer.OVERLAY_UNAVAILABLE_TEXTURE);
			registry.register(InstructionRenderer.OVERLAY_UNAVAILABLE_TEXTURE_DOWN);
			registry.register(InstructionRenderer.OVERLAY_UNAVAILABLE_TEXTURE_UP);
			registry.register(InstructionRenderer.OVERLAY_UNAVAILABLE_TEXTURE_LEFT);
			registry.register(InstructionRenderer.OVERLAY_UNAVAILABLE_TEXTURE_RIGHT);
		});

		WorldRenderEvents.BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> {
			InstructionRenderer.renderTargetOverlay(worldRenderContext, blockOutlineContext);

			return false;
		});

		WorldRenderEvents.LAST.register(InstructionRenderer::renderSitesOverlay);

		UMULittleMaid.LOGGER.info(UMULittleMaidClient.MARKER, "Succeeded initializing mod client!");
	}

	static {
		ANIMATION_MAID_EAT = Animation.Builder.create(0.5F).looping()
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
		ANIMATION_MAID_HEAL = Animation.Builder.create(1.0F).looping()
				.addBoneAnimation(EntityModelPartNames.LEFT_ARM, new Transformation(Transformation.Targets.ROTATE,
						new Keyframe(0.0F, AnimationHelper.method_41829(-90.0F, 5.7F, -45.0F), Transformation.Interpolations.field_37884)))
				.addBoneAnimation(EntityModelPartNames.RIGHT_ARM, new Transformation(Transformation.Targets.ROTATE,
						new Keyframe(0.0F, AnimationHelper.method_41829(-90.0F, -5.7F, 45.0F), Transformation.Interpolations.field_37884)))
				.build();
		ANIMATION_MAID_USE_DRIPLEAF_LEFT = Animation.Builder.create(2.0F).looping()
				.addBoneAnimation(EntityModelPartNames.LEFT_ARM, new Transformation(Transformation.Targets.ROTATE,
						new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 0.0F, -150.0F), Transformation.Interpolations.field_37884)))
				.addBoneAnimation("using_dripleaf_bone", new Transformation(Transformation.Targets.ROTATE,
						new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_37884),
						new Keyframe(0.25F, AnimationHelper.method_41829(0.0F, 0.0F, 15.0F), Transformation.Interpolations.field_37884),
						new Keyframe(0.5F, AnimationHelper.method_41829(0.0F, 0.0F, 20.0F), Transformation.Interpolations.field_37884),
						new Keyframe(0.75F, AnimationHelper.method_41829(0.0F, 0.0F, 15.0F), Transformation.Interpolations.field_37884),
						new Keyframe(1.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_37884),
						new Keyframe(1.25F, AnimationHelper.method_41829(0.0F, 0.0F, -15.0F), Transformation.Interpolations.field_37884),
						new Keyframe(1.5F, AnimationHelper.method_41829(0.0F, 0.0F, -20.0F), Transformation.Interpolations.field_37884),
						new Keyframe(1.75F, AnimationHelper.method_41829(0.0F, 0.0F, -15.0F), Transformation.Interpolations.field_37884),
						new Keyframe(2.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_37884)))
				.build();
		ANIMATION_MAID_USE_DRIPLEAF_RIGHT = Animation.Builder.create(2.0F).looping()
				.addBoneAnimation(EntityModelPartNames.RIGHT_ARM, new Transformation(Transformation.Targets.ROTATE,
						new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 0.0F, 150.0F), Transformation.Interpolations.field_37884)))
				.addBoneAnimation("using_dripleaf_bone", new Transformation(Transformation.Targets.ROTATE,
						new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_37884),
						new Keyframe(0.25F, AnimationHelper.method_41829(0.0F, 0.0F, -15.0F), Transformation.Interpolations.field_37884),
						new Keyframe(0.5F, AnimationHelper.method_41829(0.0F, 0.0F, -20.0F), Transformation.Interpolations.field_37884),
						new Keyframe(0.75F, AnimationHelper.method_41829(0.0F, 0.0F, -15.0F), Transformation.Interpolations.field_37884),
						new Keyframe(1.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_37884),
						new Keyframe(1.25F, AnimationHelper.method_41829(0.0F, 0.0F, 15.0F), Transformation.Interpolations.field_37884),
						new Keyframe(1.5F, AnimationHelper.method_41829(0.0F, 0.0F, 20.0F), Transformation.Interpolations.field_37884),
						new Keyframe(1.75F, AnimationHelper.method_41829(0.0F, 0.0F, 15.0F), Transformation.Interpolations.field_37884),
						new Keyframe(2.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_37884)))
				.build();
		ANIMATION_MAID_CHANGE_COSTUME = Animation.Builder.create(0.5F)
				.addBoneAnimation(EntityModelPartNames.LEFT_ARM, new Transformation(Transformation.Targets.ROTATE,
						new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_37885),
						new Keyframe(0.04F, AnimationHelper.method_41829(0.0F, 0.0F, -32.5F), Transformation.Interpolations.field_37885),
						new Keyframe(0.25F, AnimationHelper.method_41829(0.0F, 0.0F, -65.0F), Transformation.Interpolations.field_37885),
						new Keyframe(0.46F, AnimationHelper.method_41829(0.0F, 0.0F, -32.5F), Transformation.Interpolations.field_37885),
						new Keyframe(0.5F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_37885)))
				.addBoneAnimation(EntityModelPartNames.RIGHT_ARM, new Transformation(Transformation.Targets.ROTATE,
						new Keyframe(0.0F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_37885),
						new Keyframe(0.04F, AnimationHelper.method_41829(0.0F, 0.0F, 32.5F), Transformation.Interpolations.field_37885),
						new Keyframe(0.25F, AnimationHelper.method_41829(0.0F, 0.0F, 65.0F), Transformation.Interpolations.field_37885),
						new Keyframe(0.46F, AnimationHelper.method_41829(0.0F, 0.0F, 32.5F), Transformation.Interpolations.field_37885),
						new Keyframe(0.5F, AnimationHelper.method_41829(0.0F, 0.0F, 0.0F), Transformation.Interpolations.field_37885)))
				.build();
	}
}
