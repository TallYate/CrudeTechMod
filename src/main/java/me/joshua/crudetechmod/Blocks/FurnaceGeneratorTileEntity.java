package me.joshua.crudetechmod.Blocks;

import javax.annotation.Nonnull;

import me.joshua.crudetechmod.CrudeTechMod;
import me.joshua.crudetechmod.Init.ModBlocks;
import me.joshua.crudetechmod.Init.ModTileEntityTypes;
import me.joshua.crudetechmod.gui.FurnaceGeneratorContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public class FurnaceGeneratorTileEntity extends LockableLootTileEntity implements ITickableTileEntity {
	public final int SIZE = 5;
	protected NonNullList<ItemStack> generatorContents = NonNullList.withSize(SIZE, ItemStack.EMPTY);
	protected int numPlayersUsing;
	protected IItemHandlerModifiable items = createHandler();
	protected LazyOptional<IItemHandlerModifiable> itemHolder = LazyOptional.of(() -> items);
	public double excessEnergy = 0;
	public double excessTick = 0;
	public int percBurn = 100;
	public int burnTime = 0;
	public int fullBurnTime = 0;
	public boolean requiresUpdate = true;
	public int energyMult = 12;
	public int percEnergy = 100;

	protected static BlockPos negative;
	protected static int x;
	protected static int y;
	protected static int z;
	protected EnergyStorage energy = new EnergyStorage(100000);
	protected LazyOptional<EnergyStorage> energyHolder = LazyOptional.of(() -> energy);

	public FurnaceGeneratorTileEntity(TileEntityType<?> typeIn) {

		super(typeIn);
	}

	public FurnaceGeneratorTileEntity() {
		this(ModTileEntityTypes.FURNACE_GENERATOR.get());
	}

	public void updateNegPos(BlockPos pos) {
		int x = 0;
		int y = 0;
		int z = 0;
		while (this.world.getBlockState(pos.add(x - 1, y, z)).getBlock() == ModBlocks.FURNACE_GENERATOR.get()) {
			x--;
		}
		while (this.world.getBlockState(pos.add(x, y - 1, z)).getBlock() == ModBlocks.FURNACE_GENERATOR.get()) {
			y--;
		}
		while (this.world.getBlockState(pos.add(x, y, z - 1)).getBlock() == ModBlocks.FURNACE_GENERATOR.get()) {
			z--;
		}

		BlockPos neg = pos.add(x, y, z);
		CrudeTechMod.log(Integer.toString(x) + Integer.toString(y) + Integer.toString(z));
		CrudeTechMod.log(neg.toString());

		int x2 = 0;
		int y2 = 0;
		int z2 = 0;
		while (this.world.getBlockState(pos.add(x2 + 1, y2, z2)).getBlock() == ModBlocks.FURNACE_GENERATOR.get()) {
			x2++;
		}
		while (this.world.getBlockState(pos.add(x2, y2 + 1, z2)).getBlock() == ModBlocks.FURNACE_GENERATOR.get()) {
			y2++;
		}
		while (this.world.getBlockState(pos.add(x2, y2, z2 + 1)).getBlock() == ModBlocks.FURNACE_GENERATOR.get()) {
			z2++;
		}

		CrudeTechMod.log(Integer.toString(x2) + Integer.toString(y2) + Integer.toString(z2));
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
					double burn = time / (this.percBurn / 100.0D);
					int burn2 = (int) burn;
					this.excessTick += burn - burn2;
					this.burnTime += burn2;
					this.fullBurnTime = (int) burn;
					if (this.excessTick >= 1) {
						this.burnTime += (int) this.excessEnergy;
						excessEnergy = excessEnergy % 1;
					}
				}
			}
		} else {
			this.burnTime--;
			float en = ((this.percEnergy + multiplier(this.getItems().get(1).getItem())) / 100.0F)
					* (float) this.energyMult;

			int n = (int) en;
			this.energy.receiveEnergy(n, false);
			this.excessEnergy += (en - n);
			if (this.excessEnergy >= 1) {
				this.energy.receiveEnergy((int) this.excessEnergy, false);
				excessEnergy = excessEnergy % 1;
			}
		}

		if (this.energy.getEnergyStored() > 0) {
			for (int i = 2; i < SIZE; i++) {
				ItemStack stack = items.getStackInSlot(i);
				if (stack != null) {
					stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler -> {
						int ext = energy.extractEnergy(energy.getMaxEnergyStored() / 100, false);
						int rec = handler.receiveEnergy(ext, false);
						energy.receiveEnergy(ext - rec, false);
					});
				}
			}
		}

		if (requiresUpdate) {
			updateTile();
			requiresUpdate = false;
		}
	}

	private static int multiplier(Item item) {
		int x = 0;
		if (item == Items.IRON_INGOT) {
			x = 1;
		} else if (item == Items.GOLD_INGOT) {
			x = 2;
		} else if (item == Items.DIAMOND) {
			x = 4;
		}
		return x;
	}

	public int getEnergy() {
		return this.energy.getEnergyStored();
	}

	public int getMaxEnergy() {
		return this.energy.getMaxEnergyStored();
	}

	public void setEnergy(int x) {
		this.energy.receiveEnergy(x - this.getEnergy(), false);
	}

	public int getBurnTime() {
		return (int) this.burnTime;
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
			return stack.getCapability(CapabilityEnergy.ENERGY).isPresent();
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
		compound.putInt("FullBurnTime", this.fullBurnTime);
		return compound;
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		this.generatorContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
		if (!this.checkLootAndRead(compound)) {
			ItemStackHelper.loadAllItems(compound, this.generatorContents);
		}
		this.setEnergy(compound.getInt("Energy"));
		this.burnTime = compound.getInt("BurnTime");
		this.fullBurnTime = compound.getInt("FullBurnTime");
	}

	private void playSound(SoundEvent sound) {
		double dx = (double) this.pos.getX() + 0.5D;
		double dy = (double) this.pos.getY() + 0.5D;
		double dz = (double) this.pos.getZ() + 0.5D;
		this.world.playSound((PlayerEntity) null, dx, dy, dz, sound, SoundCategory.BLOCKS, 0.5F,
				this.world.rand.nextFloat() * 0.1F + 0.5F);
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

	public static void swapChestContents(FurnaceGeneratorTileEntity te, FurnaceGeneratorTileEntity otherTe) {
		NonNullList<ItemStack> list = te.getItems();
		te.setItems(otherTe.getItems());
		otherTe.setItems(list);
	}

	@Override
	public void updateContainingBlockInfo() {
		CrudeTechMod.log("updateContainingBlockInfo");
		super.updateContainingBlockInfo();
		if (this.itemHolder != null) {
			CrudeTechMod.log("	itemHolder invalidating and nulling");
			this.itemHolder.invalidate();
			this.itemHolder = null;
		}
		if (this.energyHolder != null) {
			CrudeTechMod.log("	energyHolder invalidating and nulling");
			this.energyHolder.invalidate();
			this.energyHolder = null;
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nonnull Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return itemHolder.cast();
		} else if (cap == CapabilityEnergy.ENERGY) {
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
		if (itemHolder != null) {
			CrudeTechMod.log("	invalidating itemHolder");
			itemHolder.invalidate();
		}
		if (energyHolder != null) {
			CrudeTechMod.log("	invalidating energyHolder");
			energyHolder.invalidate();
		}
	}

	public void updateTile() {
		this.markDirty();
		if (this.world != null) {
			this.world.notifyBlockUpdate(pos, this.getBlockState(), this.getBlockState(), 3);
		}
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, -1, this.getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		handleUpdateTag(pkt.getNbtCompound());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.serializeNBT();
	}

	@Override
	public void handleUpdateTag(CompoundNBT tag) {
		this.read(tag);
	}

}