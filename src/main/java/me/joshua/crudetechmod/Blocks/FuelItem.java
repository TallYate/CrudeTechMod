package me.joshua.crudetechmod.Blocks;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FuelItem extends Item{
	private final int burnTime;
	public FuelItem(int burnTimeIn, Properties properties) {
		super(properties);
		this.burnTime=burnTimeIn;
	}
	
	@Override
	public int getBurnTime(ItemStack itemStack) {
		return this.burnTime;
	}

}
