package me.joshua.crudetechmod.Init;

import me.joshua.crudetechmod.CrudeTechMod;
import net.minecraft.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, CrudeTechMod.MOD_ID);
}
