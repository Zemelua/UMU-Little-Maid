package io.github.zemelua.umu_little_maid.client.renderer.gui.overlay;

import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.client.renderer.ModRenderLayers;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

public final class OverlayRenderer {
	public static final Identifier TEXTURE_INSTRUCTION_AVAILABLE = UMULittleMaid.identifier("gui/overlay/instruction_available");
	public static final Identifier TEXTURE_INSTRUCTION_AVAILABLE_DOWN = UMULittleMaid.identifier("gui/overlay/instruction_available_down");
	public static final Identifier TEXTURE_INSTRUCTION_AVAILABLE_UP = UMULittleMaid.identifier("gui/overlay/instruction_available_up");
	public static final Identifier TEXTURE_INSTRUCTION_AVAILABLE_LEFT = UMULittleMaid.identifier("gui/overlay/instruction_available_left");
	public static final Identifier TEXTURE_INSTRUCTION_AVAILABLE_RIGHT = UMULittleMaid.identifier("gui/overlay/instruction_available_right");
	public static final Identifier OVERLAY_DELETABLE_TEXTURE = UMULittleMaid.identifier("gui/overlay/instruction_deletable");
	public static final Identifier OVERLAY_DELETABLE_TEXTURE_DOWN = UMULittleMaid.identifier("gui/overlay/instruction_deletable_down");
	public static final Identifier OVERLAY_DELETABLE_TEXTURE_UP = UMULittleMaid.identifier("gui/overlay/instruction_deletable_up");
	public static final Identifier OVERLAY_DELETABLE_TEXTURE_LEFT = UMULittleMaid.identifier("gui/overlay/instruction_deletable_left");
	public static final Identifier OVERLAY_DELETABLE_TEXTURE_RIGHT = UMULittleMaid.identifier("gui/overlay/instruction_deletable_right");
	public static final Identifier OVERLAY_UNAVAILABLE_TEXTURE = UMULittleMaid.identifier("gui/overlay/instruction_unavailable");
	public static final Identifier OVERLAY_UNAVAILABLE_TEXTURE_DOWN = UMULittleMaid.identifier("gui/overlay/instruction_unavailable_down");
	public static final Identifier OVERLAY_UNAVAILABLE_TEXTURE_UP = UMULittleMaid.identifier("gui/overlay/instruction_unavailable_up");
	public static final Identifier OVERLAY_UNAVAILABLE_TEXTURE_LEFT = UMULittleMaid.identifier("gui/overlay/instruction_unavailable_left");
	public static final Identifier OVERLAY_UNAVAILABLE_TEXTURE_RIGHT = UMULittleMaid.identifier("gui/overlay/instruction_unavailable_right");
	public static final Identifier OVERLAY_HOME_TEXTURE = UMULittleMaid.identifier("gui/overlay/instruction_home");
	public static final Identifier OVERLAY_HOME_TEXTURE_DOWN = UMULittleMaid.identifier("gui/overlay/instruction_home_down");
	public static final Identifier OVERLAY_HOME_TEXTURE_UP = UMULittleMaid.identifier("gui/overlay/instruction_home_up");
	public static final Identifier OVERLAY_HOME_TEXTURE_LEFT = UMULittleMaid.identifier("gui/overlay/instruction_home_left");
	public static final Identifier OVERLAY_HOME_TEXTURE_RIGHT = UMULittleMaid.identifier("gui/overlay/instruction_home_right");
	public static final Identifier OVERLAY_ANCHOR_TEXTURE = UMULittleMaid.identifier("gui/overlay/instruction_anchor");
	public static final Identifier OVERLAY_ANCHOR_TEXTURE_DOWN = UMULittleMaid.identifier("gui/overlay/instruction_anchor_down");
	public static final Identifier OVERLAY_ANCHOR_TEXTURE_UP = UMULittleMaid.identifier("gui/overlay/instruction_anchor_up");
	public static final Identifier OVERLAY_ANCHOR_TEXTURE_LEFT = UMULittleMaid.identifier("gui/overlay/instruction_anchor_left");
	public static final Identifier OVERLAY_ANCHOR_TEXTURE_RIGHT = UMULittleMaid.identifier("gui/overlay/instruction_anchor_right");
	public static final Identifier OVERLAY_DELIVERY_BOX_TEXTURE = UMULittleMaid.identifier("gui/overlay/instruction_delivery_box");
	public static final Identifier OVERLAY_DELIVERY_BOX_TEXTURE_DOWN = UMULittleMaid.identifier("gui/overlay/instruction_delivery_box_down");
	public static final Identifier OVERLAY_DELIVERY_BOX_TEXTURE_UP = UMULittleMaid.identifier("gui/overlay/instruction_delivery_box_up");
	public static final Identifier OVERLAY_DELIVERY_BOX_TEXTURE_LEFT = UMULittleMaid.identifier("gui/overlay/instruction_delivery_box_left");
	public static final Identifier OVERLAY_DELIVERY_BOX_TEXTURE_RIGHT = UMULittleMaid.identifier("gui/overlay/instruction_delivery_box_right");
	public static final Overlay AVAILABLE = new Overlay(
			ModRenderLayers.INSTRUCTION_OVERLAY_TARGET,
			PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
			TEXTURE_INSTRUCTION_AVAILABLE,
			TEXTURE_INSTRUCTION_AVAILABLE_DOWN,
			TEXTURE_INSTRUCTION_AVAILABLE_UP,
			TEXTURE_INSTRUCTION_AVAILABLE_LEFT,
			TEXTURE_INSTRUCTION_AVAILABLE_RIGHT
	);
	public static final Overlay OVERLAY_DELETABLE = new Overlay(
			ModRenderLayers.INSTRUCTION_OVERLAY_TARGET,
			PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
			OVERLAY_DELETABLE_TEXTURE,
			OVERLAY_DELETABLE_TEXTURE_DOWN,
			OVERLAY_DELETABLE_TEXTURE_UP,
			OVERLAY_DELETABLE_TEXTURE_LEFT,
			OVERLAY_DELETABLE_TEXTURE_RIGHT
	);
	public static final Overlay OVERLAY_UNAVAILABLE = new Overlay(
			ModRenderLayers.INSTRUCTION_OVERLAY_TARGET,
			PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
			OVERLAY_UNAVAILABLE_TEXTURE,
			OVERLAY_UNAVAILABLE_TEXTURE_DOWN,
			OVERLAY_UNAVAILABLE_TEXTURE_UP,
			OVERLAY_UNAVAILABLE_TEXTURE_LEFT,
			OVERLAY_UNAVAILABLE_TEXTURE_RIGHT
	);
	public static final Overlay OVERLAY_HOME = new Overlay(
			ModRenderLayers.INSTRUCTION_OVERLAY_SITE,
			PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
			OVERLAY_HOME_TEXTURE,
			OVERLAY_HOME_TEXTURE_DOWN,
			OVERLAY_HOME_TEXTURE_UP,
			OVERLAY_HOME_TEXTURE_LEFT,
			OVERLAY_HOME_TEXTURE_RIGHT
	);
	public static final Overlay OVERLAY_ANCHOR;
	public static final Overlay OVERLAY_DELIVERY_BOX = new Overlay(
			ModRenderLayers.INSTRUCTION_OVERLAY_SITE,
			PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
			OVERLAY_DELIVERY_BOX_TEXTURE,
			OVERLAY_DELIVERY_BOX_TEXTURE_DOWN,
			OVERLAY_DELIVERY_BOX_TEXTURE_UP,
			OVERLAY_DELIVERY_BOX_TEXTURE_LEFT,
			OVERLAY_DELIVERY_BOX_TEXTURE_RIGHT
	);

	static {
		OVERLAY_ANCHOR = new Overlay(
				ModRenderLayers.INSTRUCTION_OVERLAY_SITE,
				PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
				OVERLAY_ANCHOR_TEXTURE,
				OVERLAY_ANCHOR_TEXTURE_DOWN,
				OVERLAY_ANCHOR_TEXTURE_UP,
				OVERLAY_ANCHOR_TEXTURE_LEFT,
				OVERLAY_ANCHOR_TEXTURE_RIGHT
		);
	}
}
