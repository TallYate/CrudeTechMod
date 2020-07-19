package me.joshua.crudetechmod.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;

public class ModEnergySlot extends SingularSlot {

	public ModEnergySlot(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}

	public boolean isItemValid(ItemStack stack) {
		return stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
	}

}