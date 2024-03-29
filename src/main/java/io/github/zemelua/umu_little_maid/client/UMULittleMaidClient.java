package io.github.zemelua.umu_little_maid.client;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.client.geo.LittleMaidGeoRenderer;
import io.github.zemelua.umu_little_maid.client.model.entity.LittleMaidEntityModel;
import io.github.zemelua.umu_little_maid.client.network.ClientNetworkHandler;
import io.github.zemelua.umu_little_maid.client.particle.ShockParticleFactory;
import io.github.zemelua.umu_little_maid.client.particle.ShockwaveParticleFactory;
import io.github.zemelua.umu_little_maid.client.particle.TwinkleParticleFactory;
import io.github.zemelua.umu_little_maid.client.particle.ZZZParticleFactory;
import io.github.zemelua.umu_little_maid.client.screen.LittleMaidScreen;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.inventory.ModInventories;
import io.github.zemelua.umu_little_maid.particle.ModParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;
import net.minecraft.client.render.entity.animation.Transformation.Interpolations;
import net.minecraft.client.render.entity.animation.Transformation.Targets;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.screen.PlayerScreenHandler;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import static net.minecraft.client.option.KeyBinding.*;
import static net.minecraft.client.render.entity.animation.AnimationHelper.*;
import static org.lwjgl.glfw.GLFW.*;

@Environment(EnvType.CLIENT)
public class UMULittleMaidClient implements ClientModInitializer {
	public static final Marker MARKER = MarkerManager.getMarker("UMU_LITTLE_MAID_CLIENT");

	public static final KeyBinding KEY_HEADPAT = new KeyBinding("key.umu_little_maid.headpat", GLFW_KEY_P, GAMEPLAY_CATEGORY);

	public static final EntityModelLayer LAYER_LITTLE_MAID = new EntityModelLayer(UMULittleMaid.identifier("little_maid"), "main");

	public static final Animation ANIMATION_MAID_EAT;
	public static final Animation ANIMATION_MAID_HEAL;
	public static final Animation ANIMATION_MAID_USE_DRIPLEAF_LEFT;
	public static final Animation ANIMATION_MAID_USE_DRIPLEAF_RIGHT;
	public static final Animation ANIMATION_MAID_CHANGE_COSTUME;
	public static final Animation ANIMATION_MAID_HEADPATTED;

	@Override
	public void onInitializeClient() {
		UMULittleMaid.LOGGER.info(UMULittleMaidClient.MARKER, "Start initializing mod client!");

		KeyBindingHelper.registerKeyBinding(KEY_HEADPAT);

		ClientNetworkHandler.initializeClient();

		EntityRendererRegistry.register(ModEntities.LITTLE_MAID, LittleMaidGeoRenderer::new);
		HandledScreens.register(ModInventories.LITTLE_MAID, LittleMaidScreen::new);
		EntityModelLayerRegistry.registerModelLayer(UMULittleMaidClient.LAYER_LITTLE_MAID, LittleMaidEntityModel::getTexturedModelData);

		ParticleFactoryRegistry.getInstance().register(ModParticles.TWINKLE, TwinkleParticleFactory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.SHOCK, ShockParticleFactory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.SHOCKWAVE, ShockwaveParticleFactory::new);
		ParticleFactoryRegistry.getInstance().register(ModParticles.ZZZ, ZZZParticleFactory::new);

		ClientTickEvents.START_CLIENT_TICK.register(ClientCallbacks::onStartTick);
		ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(ClientCallbacks::onRegisterSpritesWithBlockAtlas);
		WorldRenderEvents.BLOCK_OUTLINE.register(ClientCallbacks::onRenderBlockOutline);
		WorldRenderEvents.BEFORE_DEBUG_RENDER.register(ClientCallbacks::beforeRenderDebug);
		HudRenderCallback.EVENT.register(ClientCallbacks::onRenderHUD);

		UMULittleMaid.LOGGER.info(UMULittleMaidClient.MARKER, "Succeeded initializing mod client!");
	}

	static {
		ANIMATION_MAID_EAT = Animation.Builder.create(0.5F).looping()
				.addBoneAnimation(EntityModelPartNames.HEAD, new Transformation(Targets.ROTATE,
						new Keyframe(0.0F, createRotationalVector(10.0F, 0.0F, 0.0F), Interpolations.CUBIC),
						new Keyframe(0.2F, createRotationalVector(30.0F, 0.0F, 0.0F), Interpolations.CUBIC),
						new Keyframe(0.3F, createRotationalVector(30.0F, 0.0F, 0.0F), Interpolations.CUBIC),
						new Keyframe(0.5F, createRotationalVector(10.0F, 0.0F, 0.0F), Interpolations.CUBIC)))
				.addBoneAnimation("left_arm", new Transformation(Targets.ROTATE,
						new Keyframe(0.0F, createRotationalVector(-90.5F, 22.9F, 0.0F), Interpolations.LINEAR)))
				.addBoneAnimation(EntityModelPartNames.RIGHT_ARM, new Transformation(Targets.ROTATE,
						new Keyframe(0.0F, createRotationalVector(-90.5F, -22.9F, 0.0F), Interpolations.LINEAR)))
				.build();
		ANIMATION_MAID_HEAL = Animation.Builder.create(1.0F).looping()
				.addBoneAnimation(EntityModelPartNames.LEFT_ARM, new Transformation(Targets.ROTATE,
						new Keyframe(0.0F, createRotationalVector(-90.0F, 5.7F, -45.0F), Interpolations.LINEAR)))
				.addBoneAnimation(EntityModelPartNames.RIGHT_ARM, new Transformation(Targets.ROTATE,
						new Keyframe(0.0F, createRotationalVector(-90.0F, -5.7F, 45.0F), Interpolations.LINEAR)))
				.build();
		ANIMATION_MAID_USE_DRIPLEAF_LEFT = Animation.Builder.create(2.0F).looping()
				.addBoneAnimation(EntityModelPartNames.LEFT_ARM, new Transformation(Targets.ROTATE,
						new Keyframe(0.0F, createRotationalVector(0.0F, 0.0F, -150.0F), Interpolations.LINEAR)))
				.addBoneAnimation("using_dripleaf_bone", new Transformation(Targets.ROTATE,
						new Keyframe(0.0F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
						new Keyframe(0.25F, createRotationalVector(0.0F, 0.0F, 15.0F), Interpolations.LINEAR),
						new Keyframe(0.5F, createRotationalVector(0.0F, 0.0F, 20.0F), Interpolations.LINEAR),
						new Keyframe(0.75F, createRotationalVector(0.0F, 0.0F, 15.0F), Interpolations.LINEAR),
						new Keyframe(1.0F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
						new Keyframe(1.25F, createRotationalVector(0.0F, 0.0F, -15.0F), Interpolations.LINEAR),
						new Keyframe(1.5F, createRotationalVector(0.0F, 0.0F, -20.0F), Interpolations.LINEAR),
						new Keyframe(1.75F, createRotationalVector(0.0F, 0.0F, -15.0F), Interpolations.LINEAR),
						new Keyframe(2.0F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)))
				.build();
		ANIMATION_MAID_USE_DRIPLEAF_RIGHT = Animation.Builder.create(2.0F).looping()
				.addBoneAnimation(EntityModelPartNames.RIGHT_ARM, new Transformation(Targets.ROTATE,
						new Keyframe(0.0F, createRotationalVector(0.0F, 0.0F, 150.0F), Interpolations.LINEAR)))
				.addBoneAnimation("using_dripleaf_bone", new Transformation(Targets.ROTATE,
						new Keyframe(0.0F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
						new Keyframe(0.25F, createRotationalVector(0.0F, 0.0F, -15.0F), Interpolations.LINEAR),
						new Keyframe(0.5F, createRotationalVector(0.0F, 0.0F, -20.0F), Interpolations.LINEAR),
						new Keyframe(0.75F, createRotationalVector(0.0F, 0.0F, -15.0F), Interpolations.LINEAR),
						new Keyframe(1.0F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.LINEAR),
						new Keyframe(1.25F, createRotationalVector(0.0F, 0.0F, 15.0F), Interpolations.LINEAR),
						new Keyframe(1.5F, createRotationalVector(0.0F, 0.0F, 20.0F), Interpolations.LINEAR),
						new Keyframe(1.75F, createRotationalVector(0.0F, 0.0F, 15.0F), Interpolations.LINEAR),
						new Keyframe(2.0F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.LINEAR)))
				.build();
		ANIMATION_MAID_CHANGE_COSTUME = Animation.Builder.create(0.5F)
				.addBoneAnimation(EntityModelPartNames.LEFT_ARM, new Transformation(Targets.ROTATE,
						new Keyframe(0.0F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.CUBIC),
						new Keyframe(0.04F, createRotationalVector(0.0F, 0.0F, -32.5F), Interpolations.CUBIC),
						new Keyframe(0.25F, createRotationalVector(0.0F, 0.0F, -65.0F), Interpolations.CUBIC),
						new Keyframe(0.46F, createRotationalVector(0.0F, 0.0F, -32.5F), Interpolations.CUBIC),
						new Keyframe(0.5F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.CUBIC)))
				.addBoneAnimation(EntityModelPartNames.RIGHT_ARM, new Transformation(Targets.ROTATE,
						new Keyframe(0.0F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.CUBIC),
						new Keyframe(0.04F, createRotationalVector(0.0F, 0.0F, 32.5F), Interpolations.CUBIC),
						new Keyframe(0.25F, createRotationalVector(0.0F, 0.0F, 65.0F), Interpolations.CUBIC),
						new Keyframe(0.46F, createRotationalVector(0.0F, 0.0F, 32.5F), Interpolations.CUBIC),
						new Keyframe(0.5F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.CUBIC)))
				.build();
//		ANIMATION_MAID_HEADPATTED = Animation.Builder.create(0.7F).looping()
//				.addBoneAnimation(LittleMaidEntityModel.KEY_HEAD, new Transformation(Targets.ROTATE,
//						new Keyframe(0.0F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.CUBIC),
//						new Keyframe(0.175F, createRotationalVector(0.0F, 27.0F, 0.0F), Interpolations.CUBIC),
//						new Keyframe(0.35F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.CUBIC),
//						new Keyframe(0.525F, createRotationalVector(0.0F, -27.0F, 0.0F), Interpolations.CUBIC),
//						new Keyframe(0.7F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.CUBIC)))
//				.build();
		ANIMATION_MAID_HEADPATTED = Animation.Builder.create(1.0F).looping()
				.addBoneAnimation(LittleMaidEntityModel.KEY_HEAD, new Transformation(Targets.ROTATE,
						new Keyframe(0.0F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.CUBIC),
						new Keyframe(0.25F, createRotationalVector(0.0F, 5.0F, 0.0F), Interpolations.CUBIC),
						new Keyframe(0.5F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.CUBIC),
						new Keyframe(0.75F, createRotationalVector(0.0F, -5.0F, 0.0F), Interpolations.CUBIC),
						new Keyframe(1.0F, createRotationalVector(0.0F, 0.0F, 0.0F), Interpolations.CUBIC)))
				.build();
	}
}
