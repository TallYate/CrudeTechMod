package me.joshua.crudetechmod.Init;

import me.joshua.crudetechmod.CrudeTechMod;
import me.joshua.crudetechmod.Entities.CustomTNTEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES,
			CrudeTechMod.MOD_ID);

	public static final RegistryObject<EntityType<CustomTNTEntity>> CUSTOM_EXPLOSIVE = ENTITY_TYPES.register(
			"custom_explosive",
			() -> EntityType.Builder.<CustomTNTEntity>create(CustomTNTEntity::new, EntityClassification.MISC)
					.size(0.2F, 0.2F)
					.build(new ResourceLocation(CrudeTechMod.MOD_ID, "custom_explosive").toString()));
}
