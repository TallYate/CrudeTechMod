package me.joshua.crudetechmod.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SingularSlot extends Slot {
	public SingularSlot(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 1;
	}
	
	@Override
	public int getSlotStackLimit() {
		return 1;
	}
}