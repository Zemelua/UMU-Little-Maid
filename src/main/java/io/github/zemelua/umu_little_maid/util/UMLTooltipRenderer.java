package io.github.zemelua.umu_little_maid.util;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.zemelua.umu_little_maid.client.renderer.InstructionRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public final class UMLTooltipRenderer {
	public static void draw(DrawContext context, TextRenderer textRenderer, int x, int y, Identifier icon, Text title, Text content, int width) {
		final int bgColor = 0xF0100010;
		final int frColor0 = 0x50FF81D6;
		final int frColor1 = 0x509C1B73;

		final int lineHeight = textRenderer.fontHeight + 1;
		final int lineWidth = width - 8;
		List<OrderedText> contentLines = textRenderer.wrapLines(content, lineWidth);
		int lineNum = contentLines.size();
		final int linesHeight = lineHeight * lineNum;
		int centerX = Math.round(x + 1 + ((lineWidth + 4) / 2.0F));

		context.getMatrices().push();
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();

		int headdressW = Math.round(64 * 1.8F);
		int headdressH = Math.round(16 * 1.8F);
		int headdressX = centerX - Math.round(headdressW / 2.0F);

		context.getMatrices().translate(0.0D, 0.0D, 1.0D);
		context.drawTexture(InstructionRenderer.HEADDRESS, headdressX, y, headdressW, headdressH, 0, 0, 64, 16, 64, 16);

		int titleAndIconWidth = 16 + 2 + textRenderer.getWidth(title);
		int titleAndIconCenterY = y + Math.round(headdressH / 2.0F);
		int iconX = centerX - Math.round(titleAndIconWidth / 2.0F);
		int iconY = titleAndIconCenterY - 8;
		int titleX = iconX + 18;
		int titleY = titleAndIconCenterY - Math.round(textRenderer.fontHeight / 2.0F);

		context.getMatrices().translate(0.0D, 0.0D, 1.0D);
		context.getMatrices().push();

		context.drawTexture(icon, iconX, iconY, 0, 0, 16, 16, 16, 16);

		context.getMatrices().translate(titleX, titleY, 0.0D);
		context.drawText(textRenderer, title, 0, 0, 0x000000, false);
		context.getMatrices().pop();

		int x0 = x + 1;  // 色付きの枠の左上のx位置
		int y0 = y + 3 + headdressH;  // 色付きの枠の左上のy位置
		int w0 = lineWidth + 3 + 3;
		int h0 = linesHeight + 3 + 3;

		TooltipBackgroundRenderer.renderHorizontalLine(context, x0, y0 - 1, w0, 1, bgColor);
		TooltipBackgroundRenderer.renderHorizontalLine(context, x0, y0 + h0, w0, 1, bgColor);
		TooltipBackgroundRenderer.renderRectangle(context, x0, y0, w0, h0, 1, bgColor);
		TooltipBackgroundRenderer.renderVerticalLine(context, x0 - 1, y0, h0, 1, bgColor);
		TooltipBackgroundRenderer.renderVerticalLine(context, x0 + w0, y0, h0, 1, bgColor);
		TooltipBackgroundRenderer.renderBorder(context, x0, y0 + 1, w0, h0, 1, frColor0, frColor1);

		RenderSystem.disableBlend();

		// テキストの描画
		int textX = x0 + 4;
		int textY = y0 + 4;

		context.getMatrices().translate(0.0, 0.0, 400.0);
		for (OrderedText line : contentLines) {
			context.drawText(textRenderer, line, textX, textY, 0xFFFFFF, true);
			textY += lineHeight;
		}

		context.getMatrices().pop();
	}
}
