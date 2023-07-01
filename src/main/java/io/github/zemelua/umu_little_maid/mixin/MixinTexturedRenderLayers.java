package io.github.zemelua.umu_little_maid.mixin;

import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.util.SpriteIdentifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(TexturedRenderLayers.class)
public class MixinTexturedRenderLayers {
	@Inject(method = "addDefaultTextures",
			at = @At("HEAD"))
	private static void addModSprites(Consumer<SpriteIdentifier> adder, CallbackInfo callback) {
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.TEXTURE_INSTRUCTION_AVAILABLE));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.TEXTURE_INSTRUCTION_AVAILABLE_DOWN));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.TEXTURE_INSTRUCTION_AVAILABLE_UP));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.TEXTURE_INSTRUCTION_AVAILABLE_LEFT));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.TEXTURE_INSTRUCTION_AVAILABLE_RIGHT));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_DELETABLE_TEXTURE));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_DELETABLE_TEXTURE_DOWN));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_DELETABLE_TEXTURE_UP));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_DELETABLE_TEXTURE_LEFT));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_DELETABLE_TEXTURE_RIGHT));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_UNAVAILABLE_TEXTURE));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_UNAVAILABLE_TEXTURE_DOWN));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_UNAVAILABLE_TEXTURE_UP));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_UNAVAILABLE_TEXTURE_LEFT));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_UNAVAILABLE_TEXTURE_RIGHT));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_HOME_TEXTURE));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_HOME_TEXTURE_DOWN));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_HOME_TEXTURE_UP));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_HOME_TEXTURE_LEFT));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_HOME_TEXTURE_RIGHT));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_ANCHOR_TEXTURE));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_ANCHOR_TEXTURE_DOWN));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_ANCHOR_TEXTURE_UP));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_ANCHOR_TEXTURE_LEFT));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_ANCHOR_TEXTURE_RIGHT));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_DELIVERY_BOX_TEXTURE));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_DELIVERY_BOX_TEXTURE_DOWN));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_DELIVERY_BOX_TEXTURE_UP));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_DELIVERY_BOX_TEXTURE_LEFT));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, OverlayRenderer.OVERLAY_DELIVERY_BOX_TEXTURE_RIGHT));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, InstructionRenderer.CROSSHAIR));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, InstructionRenderer.HEADDRESS));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, InstructionRenderer.ICON_HOME));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, InstructionRenderer.ICON_ANCHOR));
//		adder.accept(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, InstructionRenderer.ICON_DELIVERY_BOX));
	}
}
