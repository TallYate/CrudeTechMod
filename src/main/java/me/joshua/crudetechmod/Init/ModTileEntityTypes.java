package me.joshua.crudetechmod.Init;

import me.joshua.crudetechmod.CrudeTechMod;
import me.joshua.crudetechmod.Blocks.FurnaceGeneratorTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, CrudeTechMod.MOD_ID);
	
	public static final RegistryObject<TileEntityType<FurnaceGeneratorTileEntity>> FURNACE_GENERATOR = TILE_ENTITY_TYPES.register("furnace_generator", () -> TileEntityType.Builder.create(FurnaceGeneratorTileEntity::new, ModBlocks.FURNACE_GENERATOR.get()).build(null));
}
