package io.github.zemelua.umu_little_maid.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.zemelua.umu_little_maid.client.renderer.InstructionRenderer;
import io.github.zemelua.umu_little_maid.mixin.DrawableHelperAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class ModGUIUtils extends DrawableHelper {
	public static void drawULMTooltip(MinecraftClient client, MatrixStack matrices, TextRenderer textRenderer, int x, int y, @Nullable Sprite texture, Text title, Text content, int width) {
		final int bgColor = 0xF0100010;
		final int frColor0 = 0x50FF81D6;
		final int frColor1 = 0x509C1B73;

		final int lineHeight = textRenderer.fontHeight + 1;
		final int lineWidth = width - 8;
		List<OrderedText> contentLines = textRenderer.wrapLines(content, lineWidth);
		int lineNum = contentLines.size();
		final int linesHeight = lineHeight * lineNum;
		int centerX = Math.round(x + 1 + ((lineWidth + 4) / 2.0F));

		matrices.push();
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableTexture();
		RenderSystem.setShaderTexture(0, PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

		Sprite headdress = client.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).apply(InstructionRenderer.HEADDRESS);
		int headdressW = Math.round(headdress.getWidth() * 1.8F);
		int headdressH = Math.round(headdress.getHeight() * 1.8F);
		int headdressX = centerX - Math.round(headdressW / 2.0F);

		matrices.translate(0.0D, 0.0D, 1.0D);
		DrawableHelper.drawSprite(matrices, headdressX, y, 0, headdressW, headdressH, headdress);

		int titleAndIconWidth = Math.round((16 + 2 + textRenderer.getWidth(title)));
		int titleAndIconCenterY = y + Math.round(headdressH / 2.0F);
		int iconX = centerX - Math.round(titleAndIconWidth / 2.0F);
		int iconY = titleAndIconCenterY - 8;
		int titleX = iconX + 18;
		int titleY = titleAndIconCenterY - Math.round(textRenderer.fontHeight / 2.0F);

		matrices.translate(0.0D, 0.0D, 1.0D);
		matrices.push();
		ItemStack itemStack = new ItemStack(Items.PINK_BED);
		client.getItemRenderer().renderGuiItemIcon(itemStack, iconX, iconY);

		matrices.translate(titleX, titleY, 0.0D);
		textRenderer.draw(matrices, title, 0, 0, 0x555555);
		matrices.pop();

		RenderSystem.disableTexture();

		int x0 = x + 1;  // 色付きの枠の左上のx位置
		int y0 = y + 3 + headdressH;  // 色付きの枠の左上のy位置
		int x1 = x0 + lineWidth + 4;
		int y1 = y0 + linesHeight + 4;

		// 色付きの枠にかかる中央の長方形の描画
		DrawableHelperAccessor.callFillGradient(matrices, x0, y0, x1, y1, bgColor, bgColor, 400);

		// 色付きの枠の外側の枠の描画
		DrawableHelperAccessor.callFillGradient(matrices, x0,     y0 - 1, x1,     y0,     bgColor, bgColor, 400);  // 上
		DrawableHelperAccessor.callFillGradient(matrices, x0 - 1, y0,     x0,     y1,     bgColor, bgColor, 400);  // 左
		DrawableHelperAccessor.callFillGradient(matrices, x1    , y0,     x1 + 1, y1,     bgColor, bgColor, 400);  // 右
		DrawableHelperAccessor.callFillGradient(matrices, x0,     y1,     x1,     y1 + 1, bgColor, bgColor, 400);  // 下

		// 色付きの枠の描画
		DrawableHelperAccessor.callFillGradient(matrices, x0,     y0,     x1,     y0 + 1, frColor0, frColor0, 400);  // 上
		DrawableHelperAccessor.callFillGradient(matrices, x0,     y0 + 1, x0 + 1, y1 - 1, frColor0, frColor1, 400);  // 左
		DrawableHelperAccessor.callFillGradient(matrices, x1 - 1, y0 + 1, x1,     y1 - 1, frColor0, frColor1, 400);  // 右
		DrawableHelperAccessor.callFillGradient(matrices, x0,     y1 - 1, x1,     y1,     frColor1, frColor1, 400);  // 下

		RenderSystem.disableBlend();
		RenderSystem.enableTexture();

		// テキストの描画
		int textX = x0 + 3;
		int textY = y0 + 3;

		matrices.translate(0.0, 0.0, 400.0);
		for (OrderedText line : contentLines) {
			textRenderer.drawWithShadow(matrices, line, textX, textY, 0xFFFFFF);
			textY += lineHeight;
		}

		matrices.pop();
	}
}
