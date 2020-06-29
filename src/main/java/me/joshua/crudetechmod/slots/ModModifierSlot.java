package me.joshua.crudetechmod.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ModModifierSlot extends ModSingularSlot {

	public ModModifierSlot(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	public boolean isItemValid(ItemStack stack) {
		Item item = stack.getItem();
		if (item == Items.IRON_INGOT || item == Items.GOLD_INGOT || item == Items.DIAMOND) {
			return true;
		}
		return false;
	}

}