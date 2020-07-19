package me.joshua.crudetechmod;

import org.lwjgl.glfw.GLFW;

import me.joshua.crudetechmod.Init.ModItems;
import me.joshua.crudetechmod.Packets.ExplosiveJumpPacket;
import me.joshua.crudetechmod.Packets.Packets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = CrudeTechMod.MOD_ID, bus = Bus.FORGE, value = Dist.CLIENT)
public class KeyBinds {

	public static KeyBinding[] keyBindings;

	@SubscribeEvent
	public static void onInput(InputEvent.KeyInputEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		if(!minecraft.isGameFocused()) {
			return;
		}
		ClientPlayerEntity player = minecraft.player;
		if (player != null) {
			ItemStack feet = player.getItemStackFromSlot(EquipmentSlotType.FEET);

			if (event.getKey() == keyBindings[0].getKey().getKeyCode() && event.getAction() == 0
					&& feet.getItem() == ModItems.KABOOTS.get()) {
				feet.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
					int slot = -1;
					for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
						if (player.inventory.getStackInSlot(i).getItem() == Items.GUNPOWDER) {
							slot = i;
						}
					}
					if (slot > -1) {
						int max = handler.getMaxEnergyStored();
						int div = 100;
						int ext = handler.extractEnergy(max / div, false);
						if (ext < max / div && ext > 0) {
							ExplosiveJumpPacket.createBoom(player, false, false, 0.4F);
						} else {
							ExplosiveJumpPacket.createBoom(player, false, false, 1.5F);
						}
						Packets.INSTANCE.sendToServer(new ExplosiveJumpPacket(slot));
					}
				});
			}
		}
	}

	public static void registerKeyBindings() {
		int amount = 1;
		keyBindings = new KeyBinding[amount];
		keyBindings[0] = new KeyBinding("description", GLFW.GLFW_KEY_Y, "tech");
		for (int i = 0; i < amount; i++) {
			ClientRegistry.registerKeyBinding(keyBindings[i]);
		}
	}
}