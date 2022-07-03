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
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.screen.PlayerScreenHandler;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@Environment(EnvType.CLIENT)
public class UMULittleMaidClient implements ClientModInitializer {
	public static final Marker MARKER = MarkerManager.getMarker("UMU_LITTLE_MAID_CLIENT");

	public static final EntityModelLayer LAYER_LITTLE_MAID = new EntityModelLayer(UMULittleMaid.identifier("little_maid"), "main");

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
}
