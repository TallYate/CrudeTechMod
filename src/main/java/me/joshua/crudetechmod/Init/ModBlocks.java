package me.joshua.crudetechmod.Init;

import me.joshua.crudetechmod.CrudeTechMod;
import me.joshua.crudetechmod.Blocks.FurnaceGeneratorBlock;
import me.joshua.crudetechmod.Blocks.MiniTNT;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS,
			CrudeTechMod.MOD_ID);
	
	public static final RegistryObject<Block> FURNACE_GENERATOR = BLOCKS.register("furnace_generator", () -> new FurnaceGeneratorBlock(Block.Properties.create(Material.ROCK, MaterialColor.GRAY).sound(SoundType.STONE).hardnessAndResistance(4.0F,  8.0F)));
	public static final RegistryObject<Block> MINI_TNT = BLOCKS.register("mini_tnt", () -> new MiniTNT(Block.Properties.create(Material.TNT).sound(SoundType.SAND).hardnessAndResistance(0.0F)));
}
