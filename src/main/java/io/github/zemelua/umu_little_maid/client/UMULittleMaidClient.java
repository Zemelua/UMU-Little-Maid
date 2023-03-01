package io.github.zemelua.umu_little_maid.client;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.client.model.entity.LittleMaidEntityModel;
import io.github.zemelua.umu_little_maid.client.network.ClientNetworkHandler;
import io.github.zemelua.umu_little_maid.client.renderer.ModRenderLayers;
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
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
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
		});

		WorldRenderEvents.BLOCK_OUTLINE.register((worldRenderContext, blockOutlineContext) -> {
			Sprite icon = MinecraftClient.getInstance().getItemRenderer().getModels().getModel(new ItemStack(Blocks.WHITE_STAINED_GLASS)).getParticleSprite();

			MatrixStack matrixStack = worldRenderContext.matrixStack();
			Vec3d cameraPos = worldRenderContext.camera().getPos();
			BlockPos pos = blockOutlineContext.blockPos();

			matrixStack.push();
			matrixStack.translate(pos.getX() - cameraPos.getX(), pos.getY() - cameraPos.getY(), pos.getZ() - cameraPos.getZ());
			// PingRenderHelper.drawBlockOverlay(box, box, box, matrixStack, icon, 1000, 175);

			MatrixStack.Entry matrixEntry = matrixStack.peek();
			Matrix4f posMatrix = matrixEntry.getPositionMatrix();
//			// RenderType pingOverlay = PingRenderType.getPingOverlay();
//			MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
//			VertexConsumer vertexBuilder = buffer.getBuffer(pingOverlay);

			int r = 255;
			int g = 0;
			int b = 0;

			VertexConsumer vertexConsumer = worldRenderContext.consumers().getBuffer(ModRenderLayers.INSTRUCTION_TARGET);

			vertexConsumer.vertex(posMatrix, 0.0F, 1.0F, 0.0F)
					.texture(icon.getMinU(), icon.getMinV())
					.color(r, g, b, 255)
					.next();
			vertexConsumer.vertex(posMatrix, 1.0F, 1.0F, 0.0F)
					.texture(icon.getMaxU(), icon.getMinV())
					.color(r, g, b, 255)
					.next();
			vertexConsumer.vertex(posMatrix, 1.0F, 1.0F, 1.0F)
					.texture(icon.getMaxU(), icon.getMaxV())
					.color(r, g, b, 255)
					.next();
			vertexConsumer.vertex(posMatrix, 0.0F, 1.0F, 1.0F)
					.texture(icon.getMinU(), icon.getMaxV())
					.color(r, g, b, 255)
					.next();

//			vertexConsumer.vertex(posMatrix, -0.5F, 0.5F, -0.5F)
//					.color(r, g, b, 255)
//					.texture(icon.getMinU(), icon.getMinV())
//					.next();
//			vertexConsumer.vertex(posMatrix, 0.5F, 0.5F, -0.5F)
//					.color(r, g, b, 255)
//					.texture(icon.getMaxU(), icon.getMinV())
//					.next();
//			vertexConsumer.vertex(posMatrix, 0.5F, 0.5F, 0.5F)
//					.color(r, g, b, 255)
//					.texture(icon.getMaxU(), icon.getMaxV())
//					.next();
//			vertexConsumer.vertex(posMatrix, -0.5F, 0.5F, 0.5F)
//					.color(r, g, b, 255)
//					.texture(icon.getMinU(), icon.getMaxV())
//					.next();



			// worldRenderContext.consumers().

			// TOP
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), (height / 2), -(length / 2), icon.getU0(), icon.getV0(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), (height / 2), -(length / 2), icon.getU1(), icon.getV0(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), (height / 2), (length / 2), icon.getU1(), icon.getV1(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), (height / 2), (length / 2), icon.getU0(), icon.getV1(), r, g, b, alpha);
//
//			// BOTTOM
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), -(height / 2), (length / 2), icon.getU0(), icon.getV1(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), -(height / 2), (length / 2), icon.getU1(), icon.getV1(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), -(height / 2), -(length / 2), icon.getU1(), icon.getV0(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), -(height / 2), -(length / 2), icon.getU0(), icon.getV0(), r, g, b, alpha);
//
//			// NORTH
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), (height / 2), (length / 2), icon.getU0(), icon.getV1(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), (height / 2), (length / 2), icon.getU1(), icon.getV1(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), -(height / 2), (length / 2), icon.getU1(), icon.getV0(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), -(height / 2), (length / 2), icon.getU0(), icon.getV0(), r, g, b, alpha);
//
//			// SOUTH
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), -(height / 2), -(length / 2), icon.getU0(), icon.getV0(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), -(height / 2), -(length / 2), icon.getU1(), icon.getV0(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), (height / 2), -(length / 2), icon.getU1(), icon.getV1(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), (height / 2), -(length / 2), icon.getU0(), icon.getV1(), r, g, b, alpha);
//
//			// EAST
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), (height / 2), -(length / 2), icon.getU0(), icon.getV1(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), (height / 2), (length / 2), icon.getU1(), icon.getV1(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), -(height / 2), (length / 2), icon.getU1(), icon.getV0(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, -(width / 2), -(height / 2), -(length / 2), icon.getU0(), icon.getV0(), r, g, b, alpha);
//
//			// WEST
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), -(height / 2), -(length / 2), icon.getU0(), icon.getV0(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), -(height / 2), (length / 2), icon.getU1(), icon.getV0(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), (height / 2), (length / 2), icon.getU1(), icon.getV1(), r, g, b, alpha);
//			VertexHelper.renderPosTexColor(vertexBuilder, posMatrix, (width / 2), (height / 2), -(length / 2), icon.getU0(), icon.getV1(), r, g, b, alpha);
			// buffer.endBatch(pingOverlay);


			matrixStack.translate(0, 0, 0);
			matrixStack.pop();

			return false;
		});

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
