package me.joshua.crudetechmod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import net.java.games.input.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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
		ModEntities.ENTITY_TYPES.register(modEventBus);
		
	}
	
	
	
	
	private void setup(final FMLCommonSetupEvent event) {
		Packets.INSTANCE.registerMessage(Packets.i++, key.class, key::encode, key::decode, key::handle);
		ModCapabilityEnergy.register();
		//CapabilityManager.INSTANCE.register(ModIEnergyStorage.class, ModCapabilityEnergy.ENERGY.getStorage(), ModEnergyStorage.class);
	}
	
	private void doClientStuff(final FMLClientSetupEvent event) {
		KeyBinds.registerKeyBindings();
	}
	
	public static void log(String info) {
		LOGGER.info(info);
	}
}
