package me.joshua.crudetechmod;

import org.lwjgl.glfw.GLFW;

import me.joshua.crudetechmod.Energy.ModCapabilityEnergy;
import me.joshua.crudetechmod.Init.ModItems;
import me.joshua.crudetechmod.Packets.Packets;
import me.joshua.crudetechmod.Packets.key;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
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
		if (player != null) {
			ItemStack feet = player.getItemStackFromSlot(EquipmentSlotType.FEET);
			if (event.getKey() == keyBindings[0].getKey().getKeyCode() && !press[0]
					&& feet.getItem() == ModItems.KABOOTS.get()) {
				feet.getCapability(ModCapabilityEnergy.ENERGY).ifPresent(handler -> {
					int max = handler.getMaxEnergyStored();
					int div = 100;
					int ext = handler.extractEnergy(max / div, false);
					if (ext < max / div && ext > 0) {
						key.createBoom(player, false, false, 0.4F);
					} else {
						key.createBoom(player, false, false, 1.5F);
					}
					Packets.INSTANCE.sendToServer(new key(0));
					press[0] = true;
				});
			}
		}
	}

	public static void registerKeyBindings() {
		int amount = 1;
		keyBindings = new KeyBinding[amount];
		press = new Boolean[amount];
		keyBindings[0] = new KeyBinding("description", GLFW.GLFW_KEY_Y, "tech");
		for (int i = 0; i < amount; i++) {
			ClientRegistry.registerKeyBinding(keyBindings[i]);
			press[i] = false;
		}
	}
}