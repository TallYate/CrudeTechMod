package me.joshua.crudetechmod;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;

public class ModGuiUtils {
	private static ItemStack cachedTooltipStack = ItemStack.EMPTY;
	public static final int DEFAULT_BACKGROUND_COLOR = 0xF0100010;
	public static final int DEFAULT_BORDER_COLOR_START = 0x505000FF;
	public static final int DEFAULT_BORDER_COLOR_END = (DEFAULT_BORDER_COLOR_START & 0xFEFEFE) >> 1
			| DEFAULT_BORDER_COLOR_START & 0xFF000000;

	public static void drawHoveringText(List<String> textLines, int mouseX, int mouseY, int screenWidth,
			int screenHeight, int maxTextWidth, FontRenderer font) {
		drawHoveringText(textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, DEFAULT_BACKGROUND_COLOR,
				DEFAULT_BORDER_COLOR_START, DEFAULT_BORDER_COLOR_END, font);
	}

	public static void drawHoveringText(List<String> textLines, int mouseX, int mouseY, int screenWidth,
			int screenHeight, int maxTextWidth, int backgroundColor, int borderColorStart, int borderColorEnd,
			FontRenderer font) {
		drawHoveringText(cachedTooltipStack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth,
				backgroundColor, borderColorStart, borderColorEnd, font);
	}

	public static void drawHoveringText(@Nonnull final ItemStack stack, List<String> textLines, int mouseX, int mouseY,
			int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font) {
		drawHoveringText(stack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth,
				DEFAULT_BACKGROUND_COLOR, DEFAULT_BORDER_COLOR_START, DEFAULT_BORDER_COLOR_END, font);
	}

	/**
	 * Use this version if calling from somewhere where ItemStack context is
	 * available.
	 *
	 * @see #drawHoveringText(List, int, int, int, int, int, int, int, int,
	 *      FontRenderer)
	 */
	public static void drawHoveringText(@Nonnull final ItemStack stack, List<String> textLines, int mouseX, int mouseY,
			int screenWidth, int screenHeight, int maxTextWidth, int backgroundColor, int borderColorStart,
			int borderColorEnd, FontRenderer font) {
		if (!textLines.isEmpty()) {
			RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, textLines, mouseX, mouseY, screenWidth,
					screenHeight, maxTextWidth, font);
			if (MinecraftForge.EVENT_BUS.post(event))
				return;
			mouseX = event.getX();
			mouseY = event.getY();
			screenWidth = event.getScreenWidth();
			screenHeight = event.getScreenHeight();
			maxTextWidth = event.getMaxWidth();
			font = event.getFontRenderer();

			RenderSystem.disableRescaleNormal();
			RenderSystem.disableDepthTest();
			int tooltipTextWidth = 0;

			for (String textLine : textLines) {
				int textLineWidth = font.getStringWidth(textLine);
				if (textLineWidth > tooltipTextWidth)
					tooltipTextWidth = textLineWidth;
			}

			boolean needsWrap = false;

			int titleLinesCount = 1;
			int tooltipX = mouseX + 12;
			if (tooltipX + tooltipTextWidth + 4 > screenWidth) {
				tooltipX = mouseX - 16 - tooltipTextWidth;
				if (tooltipX < 4) // if the tooltip doesn't fit on the screen
				{
					if (mouseX > screenWidth / 2)
						tooltipTextWidth = mouseX - 12 - 8;
					else
						tooltipTextWidth = screenWidth - 16 - mouseX;
					needsWrap = true;
				}
			}

			if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth) {
				tooltipTextWidth = maxTextWidth;
				needsWrap = true;
			}

			if (needsWrap) {
				int wrappedTooltipWidth = 0;
				List<String> wrappedTextLines = new ArrayList<String>();
				for (int i = 0; i < textLines.size(); i++) {
					String textLine = textLines.get(i);
					List<String> wrappedLine = font.listFormattedStringToWidth(textLine, tooltipTextWidth);
					if (i == 0)
						titleLinesCount = wrappedLine.size();

					for (String line : wrappedLine) {
						int lineWidth = font.getStringWidth(line);
						if (lineWidth > wrappedTooltipWidth)
							wrappedTooltipWidth = lineWidth;
						wrappedTextLines.add(line);
					}
				}
				tooltipTextWidth = wrappedTooltipWidth;
				textLines = wrappedTextLines;

				if (mouseX > screenWidth / 2)
					tooltipX = mouseX - 16 - tooltipTextWidth;
				else
					tooltipX = mouseX + 12;
			}

			int tooltipY = mouseY - 12;
			int tooltipHeight = 8;

			if (textLines.size() > 1) {
				tooltipHeight += (textLines.size() - 1) * 10;
				if (textLines.size() > titleLinesCount)
					tooltipHeight += 2; // gap between title lines and next lines
			}

			if (tooltipY < 4)
				tooltipY = 4;
			else if (tooltipY + tooltipHeight + 4 > screenHeight)
				tooltipY = screenHeight - tooltipHeight - 4;

			final int zLevel = 300;
			RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, textLines, tooltipX, tooltipY,
					font, backgroundColor, borderColorStart, borderColorEnd);
			MinecraftForge.EVENT_BUS.post(colorEvent);
			backgroundColor = colorEvent.getBackground();
			borderColorStart = colorEvent.getBorderStart();
			borderColorEnd = colorEvent.getBorderEnd();

			drawGradientRect(zLevel, tooltipX - 3, tooltipY - 4, tooltipX + tooltipTextWidth + 3, tooltipY - 3,
					backgroundColor, backgroundColor);
			drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 3, tooltipX + tooltipTextWidth + 3,
					tooltipY + tooltipHeight + 4, backgroundColor, backgroundColor);
			drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3,
					tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
			drawGradientRect(zLevel, tooltipX - 4, tooltipY - 3, tooltipX - 3, tooltipY + tooltipHeight + 3,
					backgroundColor, backgroundColor);
			drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 3, tooltipY - 3, tooltipX + tooltipTextWidth + 4,
					tooltipY + tooltipHeight + 3, backgroundColor, backgroundColor);
			drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3 + 1, tooltipX - 3 + 1, tooltipY + tooltipHeight + 3 - 1,
					borderColorStart, borderColorEnd);
			drawGradientRect(zLevel, tooltipX + tooltipTextWidth + 2, tooltipY - 3 + 1, tooltipX + tooltipTextWidth + 3,
					tooltipY + tooltipHeight + 3 - 1, borderColorStart, borderColorEnd);
			drawGradientRect(zLevel, tooltipX - 3, tooltipY - 3, tooltipX + tooltipTextWidth + 3, tooltipY - 3 + 1,
					borderColorStart, borderColorStart);
			drawGradientRect(zLevel, tooltipX - 3, tooltipY + tooltipHeight + 2, tooltipX + tooltipTextWidth + 3,
					tooltipY + tooltipHeight + 3, borderColorEnd, borderColorEnd);

			MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostBackground(stack, textLines, tooltipX, tooltipY,
					font, tooltipTextWidth, tooltipHeight));

			IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
			MatrixStack textStack = new MatrixStack();
			textStack.translate(0.0D, 0.0D, (double) zLevel);
			Matrix4f textLocation = textStack.getLast().getMatrix();

			int tooltipTop = tooltipY;
			for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
				String line = textLines.get(lineNumber);
				if (line != null) {
					font.renderString(line, (float) tooltipX, (float) tooltipY, -1, true, textLocation, renderType,
							false, 0, 15728880);
					if (lineNumber == 1) {
						String flat = "";
						for(int a=1; a<line.length(); a++) {
							flat+='_';
						}
						font.renderString(flat, (float) tooltipX+2, (float) tooltipY - 10, -1, true, textLocation,
								renderType, false, 0, 15728880);
						flat+='_';
						font.renderString(flat, (float) tooltipX, (float) tooltipY - 10, -1, true, textLocation,
								renderType, false, 0, 15728880);
					}
				}

				if (lineNumber + 1 == titleLinesCount)
					tooltipY += 2;

				tooltipY += 10;
			}

			renderType.finish();

			MinecraftForge.EVENT_BUS.post(new RenderTooltipEvent.PostText(stack, textLines, tooltipX, tooltipTop, font,
					tooltipTextWidth, tooltipHeight));

			RenderSystem.enableDepthTest();
			RenderSystem.enableRescaleNormal();
		}
	}

	public static void drawGradientRect(int zLevel, int left, int top, int right, int bottom, int startColor,
			int endColor) {
		float startAlpha = (float) (startColor >> 24 & 255) / 255.0F;
		float startRed = (float) (startColor >> 16 & 255) / 255.0F;
		float startGreen = (float) (startColor >> 8 & 255) / 255.0F;
		float startBlue = (float) (startColor & 255) / 255.0F;
		float endAlpha = (float) (endColor >> 24 & 255) / 255.0F;
		float endRed = (float) (endColor >> 16 & 255) / 255.0F;
		float endGreen = (float) (endColor >> 8 & 255) / 255.0F;
		float endBlue = (float) (endColor & 255) / 255.0F;

		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.defaultBlendFunc();
		RenderSystem.shadeModel(GL11.GL_SMOOTH);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(right, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
		buffer.pos(left, top, zLevel).color(startRed, startGreen, startBlue, startAlpha).endVertex();
		buffer.pos(left, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
		buffer.pos(right, bottom, zLevel).color(endRed, endGreen, endBlue, endAlpha).endVertex();
		tessellator.draw();

		RenderSystem.shadeModel(GL11.GL_FLAT);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
		RenderSystem.enableTexture();
	}
}
