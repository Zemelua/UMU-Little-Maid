package io.github.zemelua.umu_little_maid;

import io.github.zemelua.umu_little_maid.data.tag.ModTags;
import io.github.zemelua.umu_little_maid.entity.ModDataHandlers;
import io.github.zemelua.umu_little_maid.entity.ModEntities;
import io.github.zemelua.umu_little_maid.entity.brain.ModMemories;
import io.github.zemelua.umu_little_maid.entity.maid.feeling.MaidFeeling;
import io.github.zemelua.umu_little_maid.inventory.ModInventories;
import io.github.zemelua.umu_little_maid.item.ModItems;
import io.github.zemelua.umu_little_maid.network.NetworkHandler;
import io.github.zemelua.umu_little_maid.register.ModRegistries;
import io.github.zemelua.umu_little_maid.sound.ModSounds;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class UMULittleMaid implements ModInitializer {
	public static final String MOD_ID = "umu_little_maid";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Marker MARKER = MarkerManager.getMarker("UMU_LITTLE_MAID");

	@Override
	public void onInitialize() {
		UMULittleMaid.LOGGER.info(UMULittleMaid.MARKER, "Start initializing mod!");

		ModRegistries.initialize();
		ModItems.initialize();
		ModEntities.initialize();
		MaidFeeling.init();
		ModMemories.init();
		ModDataHandlers.init();
		ModInventories.initialize();
		ModSounds.initialize();
		ModTags.initialize();

		NetworkHandler.init();

		UseBlockCallback.EVENT.register(Callbacks::onUseBlock);
		UseEntityCallback.EVENT.register(Callbacks::onUseEntity);
//		AttackBlockCallback.EVENT.register(Callbacks::onAttackBlock);
//		AttackEntityCallback.EVENT.register(Callbacks::onAttackEntity);

		UMULittleMaid.LOGGER.info(UMULittleMaid.MARKER, "Succeeded initializing mod!");
	}

	public static Identifier identifier(String path) {
		return new Identifier(UMULittleMaid.MOD_ID, path);
	}
}
