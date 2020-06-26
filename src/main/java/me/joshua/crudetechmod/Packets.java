package me.joshua.crudetechmod;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;


public class Packets {
		private static final String PROTOCOL_VERSION = "1";
		public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		    new ResourceLocation(CrudeTechMod.MOD_ID, "main"),
		    () -> PROTOCOL_VERSION,
		    PROTOCOL_VERSION::equals,
		    PROTOCOL_VERSION::equals
		);
		public static int i=0;
}
