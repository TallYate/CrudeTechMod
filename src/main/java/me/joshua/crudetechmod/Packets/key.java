package me.joshua.crudetechmod.Packets;

import java.util.function.Supplier;

import me.joshua.crudetechmod.Items.ModItems;
import me.joshua.crudetechmod.World.Boom;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
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
			ItemStack feet = ctx.get().getSender().getItemStackFromSlot(EquipmentSlotType.FEET);
			if (msg.key == 0 && feet.getItem() == ModItems.KABOOTS.get()) {

				createBoom(sender, true);
				feet.damageItem(1, sender, player -> {
				});
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public static void createBoom(PlayerEntity sender, Boolean server) {
		Vec3d pos = sender.getPositionVec();
		Vec3d look = sender.getLookVec();
		double x = pos.getX() - (look.getX());
		double y = pos.getY();
		if (!sender.world.getBlockState(sender.getPosition().down()).isSolid()) {
			y -= 0.5;
		}
		double z = pos.getZ() - (look.getZ());
		float size = 1.5F;
		Boom explosion = new Boom(sender.world, null, x, y, z, size, false, Explosion.Mode.BREAK);
		explosion.setDamageSource(DamageSource.causeExplosionDamage(sender));
		explosion.doExplosionA(server);
		explosion.doExplosionB(true);
	}
}