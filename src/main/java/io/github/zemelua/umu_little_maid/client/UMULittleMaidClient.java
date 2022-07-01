package io.github.zemelua.umu_little_maid.client;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.client.model.entity.LittleMaidEntityModel;
import io.github.zemelua.umu_little_maid.client.renderer.entity.LittleMaidEntityRenderer;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
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

		EntityModelLayerRegistry.registerModelLayer(UMULittleMaidClient.LAYER_LITTLE_MAID, LittleMaidEntityModel::getTexturedModelData);

		UMULittleMaid.LOGGER.info(UMULittleMaidClient.MARKER, "Succeeded initializing mod client!");
	}
}
