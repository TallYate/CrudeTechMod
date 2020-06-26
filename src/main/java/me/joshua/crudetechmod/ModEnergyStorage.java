package me.joshua.crudetechmod;

import net.minecraft.nbt.IntNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class ModEnergyStorage implements ModIEnergyStorage, INBTSerializable<IntNBT> {

	protected int energy;
	protected int capacity;
	protected int maxReceive;
	protected int maxExtract;

	public ModEnergyStorage(int capacity) {
		this(capacity, capacity, capacity, 0);
	}

	public ModEnergyStorage(int capacity, int maxTransfer) {
		this(capacity, maxTransfer, maxTransfer, 0);
	}

	public ModEnergyStorage(int capacity, int maxReceive, int maxExtract) {
		this(capacity, maxReceive, maxExtract, 0);
	}

	public ModEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
		this.energy = Math.max(0, Math.min(capacity, energy));
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		if (!canReceive())
			return 0;

		int energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
		if (!simulate)
			energy += energyReceived;
		return energyReceived;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		if (!canExtract())
			return 0;

		int energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
		if (!simulate)
			energy -= energyExtracted;
		return energyExtracted;
	}

	@Override
	public int getEnergyStored() {
		return energy;
	}

	@Override
	public int getMaxEnergyStored() {
		return capacity;
	}

	@Override
	public boolean canExtract() {
		return this.maxExtract > 0;
	}

	@Override
	public boolean canReceive() {
		return this.maxReceive > 0;
	}

	@Override
	public void setEnergy(int energy) {
		this.energy = energy;
	}

	@Override
	public IntNBT serializeNBT() {
		return IntNBT.valueOf(this.energy);
	}

	@Override
	public void deserializeNBT(IntNBT nbt) {
		this.energy = ((IntNBT)nbt).getInt();
	}
}
