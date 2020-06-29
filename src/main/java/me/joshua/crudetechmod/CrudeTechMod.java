package me.joshua.crudetechmod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.joshua.crudetechmod.Energy.ModCapabilityEnergy;
import me.joshua.crudetechmod.Init.ModBlocks;
import me.joshua.crudetechmod.Init.ModContainerTypes;
import me.joshua.crudetechmod.Init.ModEntities;
import me.joshua.crudetechmod.Init.ModItems;
import me.joshua.crudetechmod.Init.ModTileEntityTypes;
import me.joshua.crudetechmod.Packets.Packets;
import me.joshua.crudetechmod.Packets.key;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("crudetech")
public class CrudeTechMod {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "crudetech";
	public static CrudeTechMod instance;

	public CrudeTechMod() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
		modEventBus.addListener(this::doClientStuff);
		MinecraftForge.EVENT_BUS.register(this);

		ModItems.ITEMS.register(modEventBus);
		ModBlocks.BLOCKS.register(modEventBus);
		ModEntities.ENTITY_TYPES.register(modEventBus);
		ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
		ModContainerTypes.CONTAINER_TYPES.register(modEventBus);

	}

	private void setup(final FMLCommonSetupEvent event) {
		Packets.INSTANCE.registerMessage(Packets.i++, key.class, key::encode, key::decode, key::handle);
		ModCapabilityEnergy.register();
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		KeyBinds.registerKeyBindings();
	}

	public static void log(String info) {
		LOGGER.info(info);
	}

	public static class CrudeTechTab extends ItemGroup {
		public static final CrudeTechTab instance = new CrudeTechTab(ItemGroup.GROUPS.length, "TAB");

		private CrudeTechTab(int index, String label) {
			super(index, label);
		}

		@Override
		public ItemStack createIcon() {
			return new ItemStack(ModItems.KABOOTS.get());
		}
	}

}
