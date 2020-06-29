package me.joshua.crudetechmod.gui;

import java.util.Objects;

import me.joshua.crudetechmod.Blocks.FurnaceGeneratorTileEntity;
import me.joshua.crudetechmod.Init.ModBlocks;
import me.joshua.crudetechmod.Init.ModContainerTypes;
import me.joshua.crudetechmod.slots.ModEnergySlot;
import me.joshua.crudetechmod.slots.ModFuelSlot;
import me.joshua.crudetechmod.slots.ModModifierSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;

public class FurnaceGeneratorContainer extends Container {
	public final FurnaceGeneratorTileEntity tileEntity;
	private final IWorldPosCallable canInteractWithCallable;

	public FurnaceGeneratorContainer(final int windowId, final PlayerInventory playerInventory,
			final FurnaceGeneratorTileEntity tileEntityIn) {
		super(ModContainerTypes.FURNACE_GENERATOR.get(), windowId);
		this.tileEntity = tileEntityIn;
		this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());

		// Main Inventory
		int startX = 8;
		int startY = 8;
		int slotSizePlus2 = 18;
		/*
		 * for(int row = 0; row<1; row++) { for(int column = 0; column < 2; column++) {
		 * this.addSlot(new Slot(tileEntity, row * 9 + column, startX + (column *
		 * slotSizePlus2), startY + (row * slotSizePlus2))); } }
		 */
		this.addSlot(new ModFuelSlot(tileEntityIn, 0, startX, startY + (2 * slotSizePlus2) - 3));
		this.addSlot(new ModModifierSlot(tileEntityIn, 1, startX + (8 * slotSizePlus2), startY - 1));
		for (int i = 2; i < this.tileEntity.SIZE; i++) {
			this.addSlot(
					new ModEnergySlot(tileEntityIn, i, startX + (8 * slotSizePlus2), startY + ((i - 1) * slotSizePlus2)));
		}

		// Main Player Inventory
		int startPlayerInvY = startY * 3 + 43;
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(playerInventory, 9 + (row * 9) + column, startX + (column * slotSizePlus2),
						startPlayerInvY + (row * slotSizePlus2)));
			}
		}

		// Hotbar
		int hotbarY = startPlayerInvY + 58;
		for (int column = 0; column < 9; column++) {
			this.addSlot(new Slot(playerInventory, column, startX + (column * slotSizePlus2), hotbarY));
		}
	}

	private static FurnaceGeneratorTileEntity getTileEntity(final PlayerInventory playerInventory,
			final PacketBuffer data) {
		Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof FurnaceGeneratorTileEntity) {
			return (FurnaceGeneratorTileEntity) tileAtPos;
		}
		throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
	}

	public FurnaceGeneratorContainer(final int windowId, final PlayerInventory playerInventory,
			final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractWithCallable, playerIn, ModBlocks.FURNACE_GENERATOR.get());
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index < 5) {
				if (!this.mergeItemStack(itemstack1, 5, this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 5, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}
}
