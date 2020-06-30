package me.joshua.crudetechmod.gui;

import com.mojang.blaze3d.systems.RenderSystem;

import me.joshua.crudetechmod.CrudeTechMod;
import me.joshua.crudetechmod.Energy.ModCapabilityEnergy;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FurnaceGeneratorScreen extends ContainerScreen<FurnaceGeneratorContainer> {

	private final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(CrudeTechMod.MOD_ID,
			"textures/gui/container/furnace_generator.png");

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
		this.container.tileEntity.getCapability(ModCapabilityEnergy.ENERGY).ifPresent(handler -> {
			this.font.drawString("Energy: " + handler.getEnergyStored(), 0, 0, 1238767);
		});
		this.font.drawString("BurnTime: " + this.container.tileEntity.burnTime, 0, 8, 1238767);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		
		int x = (this.width - this.xSize) /2;
		int y = (this.height - this.ySize) /2;
		
		this.blit(x, y, 0, 0, this.xSize, this.ySize);
	}

}
