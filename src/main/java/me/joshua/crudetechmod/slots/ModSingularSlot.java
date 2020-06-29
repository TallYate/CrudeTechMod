package me.joshua.crudetechmod.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ModSingularSlot extends Slot {
	public ModSingularSlot(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}

}