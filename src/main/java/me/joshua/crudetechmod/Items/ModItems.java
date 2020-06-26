package me.joshua.crudetechmod.Items;

import me.joshua.crudetechmod.CrudeTechMod;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS,
			CrudeTechMod.MOD_ID);

	public static final RegistryObject<Item> KABOOTS = ITEMS.register("kaboots",
			() -> new PowerArmor(ArmorMaterial.CHAIN, EquipmentSlotType.FEET, new Item.Properties()) {
				
			});
}