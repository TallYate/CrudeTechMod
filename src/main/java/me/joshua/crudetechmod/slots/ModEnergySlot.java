package me.joshua.crudetechmod.slots;

import me.joshua.crudetechmod.Energy.ModCapabilityEnergy;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ModEnergySlot extends ModSingularSlot {

	public ModEnergySlot(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	public boolean isItemValid(ItemStack stack) {
		return stack.getCapability(ModCapabilityEnergy.ENERGY).isPresent();
	}

}