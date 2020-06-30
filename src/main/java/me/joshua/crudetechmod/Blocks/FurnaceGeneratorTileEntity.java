package me.joshua.crudetechmod.Blocks;

import javax.annotation.Nonnull;

import me.joshua.crudetechmod.CrudeTechMod;
import me.joshua.crudetechmod.Energy.ModCapabilityEnergy;
import me.joshua.crudetechmod.Energy.ModEnergyStorage;
import me.joshua.crudetechmod.Init.ModTileEntityTypes;
import me.joshua.crudetechmod.Packets.GeneratorPacket;
import me.joshua.crudetechmod.gui.FurnaceGeneratorContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public class FurnaceGeneratorTileEntity extends LockableLootTileEntity implements ITickableTileEntity {
	public final int SIZE = 4;
	private NonNullList<ItemStack> generatorContents = NonNullList.withSize(SIZE, ItemStack.EMPTY);
	protected int numPlayersUsing;
	private IItemHandlerModifiable items = createHandler();
	private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);
	public int burnTime = 0;

	private ModEnergyStorage energy = new ModEnergyStorage(100000, 1000);
	private LazyOptional<ModEnergyStorage> energyHolder = LazyOptional.of(() -> energy);

	public FurnaceGeneratorTileEntity(TileEntityType<?> typeIn) {
		super(typeIn);
	}

	public FurnaceGeneratorTileEntity() {
		this(ModTileEntityTypes.FURNACE_GENERATOR.get());
	}

	public void tick() {

		if (this.burnTime == 0) {
			ItemStack fuel = items.getStackInSlot(0);
			if (fuel.getItem() != Items.AIR) {
				int time = ForgeHooks.getBurnTime(fuel);
				if (time > 0) {
					if (fuel.hasContainerItem()) {
						items.setStackInSlot(0, fuel.getContainerItem());
					} else {
						fuel.setCount(fuel.getCount() - 1);
					}
					this.burnTime += time;
				}
			}
		} else {
			this.burnTime--;
			energy.receiveEnergy(10, false);
		}

		if (this.energy.getEnergyStored() > 0) {
			for (int i = 2; i < SIZE; i++) {
				ItemStack stack = items.getStackInSlot(i);
				if (stack != null) {
					stack.getCapability(ModCapabilityEnergy.ENERGY).ifPresent(handler -> {
						int ext = energy.extractEnergy(energy.getMaxEnergyStored() / 100, false);
						int rec = handler.receiveEnergy(ext, false);
						energy.receiveEnergy(ext - rec, false);
					});
				}
			}
		}
	}

	public int getEnergy() {
		return this.energy.getEnergyStored();
	}

	public void setEnergy(int x) {
		this.energy.setEnergy(x);
	}

	public int getBurnTime() {
		return this.burnTime;
	}

	public void setBurnTime(int x) {
		this.burnTime = x;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		switch (index) {
		case 0:
			CrudeTechMod.log(ForgeHooks.getBurnTime(stack) + "slot 0");
			return ForgeHooks.getBurnTime(stack) > 0;
		case 1:
			CrudeTechMod.log("true");
			return true;
		case 2:
			return stack.getCapability(ModCapabilityEnergy.ENERGY).isPresent();
		}
		return true;

	}

	@Override
	public int getSizeInventory() {
		return SIZE;
	}

	@Override
	public NonNullList<ItemStack> getItems() {
		return this.generatorContents;
	}

	@Override
	public void setItems(NonNullList<ItemStack> itemsIn) {
		this.generatorContents = itemsIn;
	}

	@Override
	public ITextComponent getDefaultName() {
		return new TranslationTextComponent("container.furnace_generator");
	}

	@Override
	public Container createMenu(int id, PlayerInventory player) {
		return new FurnaceGeneratorContainer(id, player, this);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		if (!this.checkLootAndWrite(compound)) {
			ItemStackHelper.saveAllItems(compound, this.generatorContents);
		}

		compound.putInt("Energy", this.energy.getEnergyStored());
		compound.putInt("BurnTime", this.burnTime);

		String side = "world is null ";
		if (world != null) {
			side = (this.world.isRemote ? "Client-Side " : "Server-Side ");
		}

		CrudeTechMod.log(side + "wrote Energy: " + Integer.toString(compound.getInt("Energy")));
		CrudeTechMod.log(side + "wrote BurnTime: " + Integer.toString(compound.getInt("BurnTime")));

		return compound;
	}

	public void readSpecial(int energy, int burnTime) {
		this.energy.setEnergy(energy);
		this.burnTime = burnTime;
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		this.generatorContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
		if (!this.checkLootAndRead(compound)) {
			ItemStackHelper.loadAllItems(compound, this.generatorContents);
		}

		if (this.world != null) {
			if (this.world.isRemote) {
				CrudeTechMod.log("sent packet");
				GeneratorPacket.INSTANCE.sendToServer(
						new GeneratorPacket(this.energy.getEnergyStored(), this.burnTime, false, this.pos));
			}
		} else {
			this.energy.setEnergy(compound.getInt("Energy"));
			this.burnTime = compound.getInt("BurnTime");
		}

		String side = "world is null ";
		if (world != null) {
			side = (this.world.isRemote ? "Client-Side " : "Server-Side ");
		}

		CrudeTechMod.log(side + "read Energy: " + Integer.toString(compound.getInt("Energy")));
		CrudeTechMod.log(side + "read BurnTime: " + Integer.toString(compound.getInt("BurnTime")));
	}

	private void playSound(SoundEvent sound) {
		double dx = (double) this.pos.getX() + 0.5D;
		double dy = (double) this.pos.getY() + 0.5D;
		double dz = (double) this.pos.getZ() + 0.5D;
		this.world.playSound((PlayerEntity) null, dx, dy, dz, sound, SoundCategory.BLOCKS, 0.5F,
				this.world.rand.nextFloat() * 0.1F + 0.5F);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("Energy", this.energy.getEnergyStored());
		SUpdateTileEntityPacket packet = new SUpdateTileEntityPacket(this.pos, 0, nbt);
		CrudeTechMod.log("getUpdatePacket");
		return packet;
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();
		this.energy.setEnergy(nbt.getInt("Energy"));
		CrudeTechMod.log("onDataPacket");
	}

	@Override
	public boolean receiveClientEvent(int id, int type) {
		if (id == 1) {
			this.numPlayersUsing = type;
			return true;
		} else {
			return super.receiveClientEvent(id, type);
		}
	}

	@Override
	public void openInventory(PlayerEntity player) {
		if (!player.isSpectator()) {
			if (this.numPlayersUsing < 0) {
				this.numPlayersUsing = 0;
			}

			++this.numPlayersUsing;
			this.onOpenOrClose();
		}
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		if (!player.isSpectator()) {
			--this.numPlayersUsing;
			this.onOpenOrClose();
		}
	}

	protected void onOpenOrClose() {
		Block block = this.getBlockState().getBlock();
		if (block instanceof FurnaceGeneratorBlock) {
			this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
			this.world.notifyNeighborsOfStateChange(this.pos, block);
		}
	}

	public static int getPlayersUsing(IBlockReader reader, BlockPos pos) {
		BlockState blockstate = reader.getBlockState(pos);
		if (blockstate.hasTileEntity()) {
			TileEntity tileentity = reader.getTileEntity(pos);
			if (tileentity instanceof FurnaceGeneratorTileEntity) {
				return ((FurnaceGeneratorTileEntity) tileentity).numPlayersUsing;
			}
		}
		return 0;
	}

	public static void swapContents(FurnaceGeneratorTileEntity te, FurnaceGeneratorTileEntity otherTe) {
		NonNullList<ItemStack> list = te.getItems();
		te.setItems(otherTe.getItems());
		otherTe.setItems(list);
	}

	@Override
	public void updateContainingBlockInfo() {
		CrudeTechMod.log("updateContainingBlockInfo");
		super.updateContainingBlockInfo();
		if (this.itemHandler != null) {
			this.itemHandler.invalidate();
			this.itemHandler = null;
		}
		if (this.energyHolder != null) {
			this.energyHolder.invalidate();
			this.energyHolder = null;
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nonnull Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return itemHandler.cast();
		} else if (cap == ModCapabilityEnergy.ENERGY) {
			return energyHolder.cast();
		} else {
			return super.getCapability(cap, side);
		}
	}

	private IItemHandlerModifiable createHandler() {
		return new InvWrapper(this);
	}

	@Override
	public void remove() {
		CrudeTechMod.log("removed furnace generator tileEntity");
		super.remove();
		if (itemHandler != null) {
			itemHandler.invalidate();
		}
		if (energyHolder != null) {
			energyHolder.invalidate();
		}
	}
}