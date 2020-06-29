package me.joshua.crudetechmod.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.ForgeHooks;

public class ModFuelSlot extends Slot {
	public ModFuelSlot(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	public boolean isItemValid(ItemStack stack) {
		return ForgeHooks.getBurnTime(stack) > 0 || isBucket(stack);
	}

	public int getItemStackLimit(ItemStack stack) {
		return isBucket(stack) ? 1 : super.getItemStackLimit(stack);
	}

	public static boolean isBucket(ItemStack stack) {
		return stack.getItem() == Items.BUCKET;
	}
}