package me.joshua.crudetechmod.Packets;

import java.util.function.Supplier;

import me.joshua.crudetechmod.CrudeTechMod;
import me.joshua.crudetechmod.Blocks.FurnaceGeneratorTileEntity;
import me.joshua.crudetechmod.Energy.ModCapabilityEnergy;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.PacketTarget;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class GeneratorPacket {
	public final int energy;
	public final int burnTime;
	public final boolean fromServer;
	public final BlockPos pos;

	public GeneratorPacket(int energy, int burnTime, boolean fromServer, BlockPos pos) {
		this.energy = energy;
		this.burnTime = burnTime;
		this.fromServer = fromServer;
		this.pos = pos;
	}

	public static void encode(GeneratorPacket msg, PacketBuffer buf) {
		buf.writeInt(msg.energy);
		buf.writeInt(msg.burnTime);
		buf.writeBoolean(msg.fromServer);
		buf.writeBlockPos(msg.pos);
	}

	public static GeneratorPacket decode(PacketBuffer buf) {
		return new GeneratorPacket(buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readBlockPos());
	}

	public static void handle(GeneratorPacket msg, Supplier<NetworkEvent.Context> ctx) {
		if (ctx != null) {
			if (ctx.get() != null) {
				ctx.get().enqueueWork(() -> {
					if (ctx.get().getSender() != null) {
						if (ctx.get().getSender().world != null) {
							TileEntity te = ctx.get().getSender().world.getTileEntity(msg.pos);
							if (!msg.fromServer) {
								if (te instanceof FurnaceGeneratorTileEntity) {
									FurnaceGeneratorTileEntity gen = (FurnaceGeneratorTileEntity) te;
									int energy = gen.getEnergy();
									int burnTime = gen.getBurnTime();
									CrudeTechMod.log("Energy: " + energy + ", BurnTime: " + burnTime);
									PacketTarget target = PacketDistributor.PLAYER.with(() -> ctx.get().getSender());
									INSTANCE.send(target, new GeneratorPacket(energy, burnTime, true, msg.pos));
								}
							} else if (te instanceof FurnaceGeneratorTileEntity) {
								CrudeTechMod.log("energy: " + msg.energy + ", burnTime: " + msg.burnTime);
								FurnaceGeneratorTileEntity gen = (FurnaceGeneratorTileEntity) te;
								gen.readSpecial(msg.energy, msg.burnTime);
							}
						}
						else {
							CrudeTechMod.log("world is null");
						}
					}
					CrudeTechMod.log("sender is null, side is " + (msg.fromServer?"Server":"Client"));
				});
				ctx.get().setPacketHandled(true);
			} else {
				CrudeTechMod.log("ctx.get() is null");
			}
		} else {
			CrudeTechMod.log("ctx is null");
		}
	}

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(CrudeTechMod.MOD_ID, "generator_packet"), () -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	public static int i = 0;
}