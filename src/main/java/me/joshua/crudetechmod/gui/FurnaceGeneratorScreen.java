package me.joshua.crudetechmod.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import me.joshua.crudetechmod.CrudeTechMod;
import me.joshua.crudetechmod.ModGuiUtils;
import me.joshua.crudetechmod.Blocks.FurnaceGeneratorTileEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FurnaceGeneratorScreen extends ContainerScreen<FurnaceGeneratorContainer> {

	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(CrudeTechMod.MOD_ID,
			"textures/gui/container/furnace_generator.png");
	private static final ResourceLocation BAR_TEXTURES = new ResourceLocation("textures/gui/bars.png");

	public FurnaceGeneratorScreen(FurnaceGeneratorContainer screenContainer, PlayerInventory inv,
			ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.guiLeft = 0;
		this.guiTop = 0;
		this.xSize = 175;
		this.ySize = 149;
	}

	@Override
	public void render(final int mouseX, final int mouseY, final float partialTicks) {
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
		this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 60.0F, 47.0F, 4210752);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		int startX = x + 154;
		int startY = y + 24;

		if (mouseX >= startX && mouseX <= startX + 12 && mouseY >= startY && mouseY <= startY + 33) {
			List<String> list = new ArrayList<String>();
			ModGuiUtils.drawHoveringText(Arrays.asList(this.container.tileEntity.getEnergy() + "FE",
					this.container.tileEntity.getMaxEnergy() + "FE"), mouseX-x, mouseY-y, this.width, this.height, 0, this.font);
		}

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);

		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;

		this.blit(x, y, 0, 0, this.xSize, this.ySize);
		FurnaceGeneratorTileEntity gen = this.container.tileEntity;
		if (gen.fullBurnTime != 0 && gen.burnTime != 0) {
			int pix = (int) (((double) gen.burnTime / (double) gen.fullBurnTime) * 13);
			this.blit(x + 29, y + 43, 176, 0, 14, 14 - pix);
		} else {
			this.blit(x + 29, y + 43, 176, 0, 14, 14);
		}

		int cur = gen.getEnergy();
		int max = gen.getMaxEnergy();
		if (cur != 0 && max != 0) {
			int pix = (int) ((((double) cur / (double) max) * 30) + 0.5) + 3;
			this.blit(x + 154, y + 26, 176, 26, 12, 33 - pix);
		} else {
			this.blit(x + 154, y + 26, 176, 26, 12, 33);
		}

	}

}
