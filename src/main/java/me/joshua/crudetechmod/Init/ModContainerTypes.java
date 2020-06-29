package me.joshua.crudetechmod.Init;

import me.joshua.crudetechmod.CrudeTechMod;
import me.joshua.crudetechmod.gui.FurnaceGeneratorContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypes {
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, CrudeTechMod.MOD_ID);
	
	public static final RegistryObject<ContainerType<FurnaceGeneratorContainer>> FURNACE_GENERATOR = CONTAINER_TYPES.register("furnace_generator", () -> IForgeContainerType.create(FurnaceGeneratorContainer::new));
}
