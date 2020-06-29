package me.joshua.crudetechmod.Init;

import me.joshua.crudetechmod.CrudeTechMod;
import me.joshua.crudetechmod.CrudeTechMod.CrudeTechTab;
import me.joshua.crudetechmod.Items.PowerArmor;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS,
			CrudeTechMod.MOD_ID);

	public static final RegistryObject<Item> KABOOTS = ITEMS.register("kaboots", () -> new PowerArmor(ArmorMaterial.CHAIN, EquipmentSlotType.FEET, new Item.Properties().group(CrudeTechTab.instance)));
	
	public static final RegistryObject<Item> FURNACE_GENERATOR = ITEMS.register("furnace_generator", () -> new BlockItem(ModBlocks.FURNACE_GENERATOR.get(), new Item.Properties().group(CrudeTechTab.instance)));
}