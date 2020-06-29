package me.joshua.crudetechmod.Packets;

import java.util.function.Supplier;

import me.joshua.crudetechmod.CrudeTechMod;
import me.joshua.crudetechmod.Energy.ModCapabilityEnergy;
import me.joshua.crudetechmod.Init.ModItems;
import me.joshua.crudetechmod.World.Boom;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraftforge.fml.network.NetworkEvent;

public class key {
	private final int key;

	public key(int keyIn) {
		this.key = keyIn;
	}

	public static void encode(key msg, PacketBuffer buf) {
		buf.writeInt(msg.key);
	}

	public static key decode(PacketBuffer buf) {
		return new key(buf.readInt());
	}

	public static void handle(key msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity sender = ctx.get().getSender();
			ItemStack feet = sender.getItemStackFromSlot(EquipmentSlotType.FEET);
			if (msg.key == 0 && feet.getItem() == ModItems.KABOOTS.get()) {
				feet.getCapability(ModCapabilityEnergy.ENERGY).ifPresent(handler -> {
					int max = handler.getMaxEnergyStored();
					int div = 100;
					int ext = handler.extractEnergy(max/div, false);
					
					if(ext==max/div) {								//	full block
						CrudeTechMod.log("full block");
						createBoom(sender, false, false, 1.5F);
						if (handler.getEnergyStored()==0) {
							sender.sendMessage(new TranslationTextComponent("Your boots ran out of power!"));
						}
						return;
					}
					else if (ext<max/div&&ext>0) {					//	half block
						createBoom(sender, false, true, 0.4F);
					}
					else {										//	no block
						createBoom(sender, true);
						return;
					}
					
					sender.sendMessage(new TranslationTextComponent("Your boots ran out of power!"));
				});
			}
		});
		ctx.get().setPacketHandled(true);
	}
	
	public static void createBoom(PlayerEntity sender, boolean server) {
		createBoom(sender, server, true, 1.5F);
	}

	public static void createBoom(PlayerEntity sender, boolean server, boolean damage, float size) {
		Vec3d pos = sender.getPositionVec();
		Vec3d look = sender.getLookVec();
		double x = pos.getX() - (look.getX());
		double y = pos.getY();
		if (!sender.world.getBlockState(sender.getPosition().down()).isSolid()) {
			y -= 0.5;
		}
		double z = pos.getZ() - (look.getZ());
		
		Boom explosion = new Boom(sender.world, null, x, y, z, size, false, Explosion.Mode.BREAK);
		explosion.setDamageSource(DamageSource.causeExplosionDamage(sender));
		explosion.doExplosionA(server, damage);
		explosion.doExplosionB(true);
	}
}