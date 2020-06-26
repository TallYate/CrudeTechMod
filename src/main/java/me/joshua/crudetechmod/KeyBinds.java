package me.joshua.crudetechmod;

import org.lwjgl.glfw.GLFW;

import me.joshua.crudetechmod.Items.ModItems;
import me.joshua.crudetechmod.Packets.Packets;
import me.joshua.crudetechmod.Packets.key;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = CrudeTechMod.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class KeyBinds {

	public static KeyBinding[] keyBindings;
	public static Boolean[] press;

	@SubscribeEvent
	public static void onTick(ClientTickEvent event) {
		if (press[0] && !keyBindings[0].isKeyDown()) {
			press[0] = false;
		}
	}

	@SubscribeEvent
	public static void onInput(InputEvent.KeyInputEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		ClientPlayerEntity player = minecraft.player;
		if (event.getKey() == keyBindings[0].getKey().getKeyCode() && !press[0]
				&& player.getItemStackFromSlot(EquipmentSlotType.FEET)
						.getItem() == ModItems.KABOOTS.get()) {
			key.createBoom(player, false);
			Packets.INSTANCE.sendToServer(new key(0));
			press[0] = true;
		}
	}

	public static void registerKeyBindings() {
		int amount = 1;
		keyBindings = new KeyBinding[amount];
		press = new Boolean[amount];
		keyBindings[0] = new KeyBinding("description", GLFW.GLFW_KEY_Y, "tech");
		for (int i = 0; i < amount; i++) {
			ClientRegistry.registerKeyBinding(keyBindings[i]);
			press[i]=false;
		}
	}
}