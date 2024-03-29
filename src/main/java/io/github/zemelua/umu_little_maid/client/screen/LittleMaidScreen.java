package io.github.zemelua.umu_little_maid.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.zemelua.umu_little_maid.UMULittleMaid;
import io.github.zemelua.umu_little_maid.entity.maid.LittleMaidEntity;
import io.github.zemelua.umu_little_maid.inventory.LittleMaidScreenHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class LittleMaidScreen extends HandledScreen<LittleMaidScreenHandler> {
	private static final Identifier MAID_SCREEN_TEXTURE = UMULittleMaid.identifier("textures/gui/little_maid.png");
	public static final Identifier EMPTY_HELD_SLOT_TEXTURE = UMULittleMaid.identifier("item/empty_held_item_slot");
	public static final Identifier EMPTY_HELMET_SLOT_TEXTURE = UMULittleMaid.identifier("item/empty_armor_slot_helmet");
	public static final Identifier EMPTY_BOOTS_SLOT_TEXTURE = UMULittleMaid.identifier("item/empty_armor_slot_boots");

	public LittleMaidScreen(LittleMaidScreenHandler handler, PlayerInventory inventory, Text title) {
		super(handler, inventory, title);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, delta);
		this.drawMouseoverTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawBackground(MatrixStack matrixStack, float delta, int mouseX, int mouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, LittleMaidScreen.MAID_SCREEN_TEXTURE);

		int centerX = (this.width - this.backgroundWidth) / 2;
		int centerY = (this.height - this.backgroundHeight) / 2;
		this.drawTexture(matrixStack, centerX, centerY, 0, 0, this.backgroundWidth, this.backgroundHeight);

		LittleMaidEntity maid = this.getScreenHandler().getMaid();
		Inventory maidInventory = maid.getInventory();
		int inventorySize = maidInventory.size();
		for (int i = 0; i < 3 && (i + 1) * 5 <= inventorySize; i++) {
			for (int j = 0; j < 5 && i * 5 + j + 1 <= inventorySize; j++) {
				this.drawTexture(matrixStack, centerX + 79 + j * 18, centerY + 17 + i * 18, 0, 166, 18, 18);
			}
		}

		InventoryScreen.drawEntity(centerX + 51, centerY + 65, 25, centerX + 51 - mouseX, centerY + 25 - mouseY, maid);
	}
}
