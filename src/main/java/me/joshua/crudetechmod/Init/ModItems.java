package me.joshua.crudetechmod.Init;

import me.joshua.crudetechmod.CrudeTechMod;
import me.joshua.crudetechmod.CrudeTechMod.CrudeTechTab;
import me.joshua.crudetechmod.Blocks.FuelItem;
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

	public static final RegistryObject<Item> KABOOTS = ITEMS.register("kaboots",
			() -> new PowerArmor(ArmorMaterial.CHAIN, EquipmentSlotType.FEET,
					new Item.Properties().group(CrudeTechTab.INSTANCE)));
	public static final RegistryObject<Item> WOOD_BIT = ITEMS.register("wood_bit",
			() -> new FuelItem(25, new Item.Properties().group(CrudeTechTab.INSTANCE)));

	public static final RegistryObject<Item> MINI_GUNDPOWDER = ITEMS.register("mini_gunpowder",
			() -> new Item(new Item.Properties().group(CrudeTechTab.INSTANCE)));
	public static final RegistryObject<Item> MINI_TNT = ITEMS.register("mini_tnt", () -> new Item(new Item.Properties().group(CrudeTechTab.INSTANCE)));

	public static final RegistryObject<Item> FURNACE_GENERATOR = ITEMS.register("furnace_generator",
			() -> new BlockItem(ModBlocks.FURNACE_GENERATOR.get(), new Item.Properties().group(CrudeTechTab.INSTANCE)));
}