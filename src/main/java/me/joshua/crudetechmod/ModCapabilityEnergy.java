package me.joshua.crudetechmod;

import java.util.Random;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;

public class ModCapabilityEnergy implements INBTSerializable
{
    @CapabilityInject(ModIEnergyStorage.class)
    public static Capability<ModIEnergyStorage> ENERGY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(ModIEnergyStorage.class, new IStorage<ModIEnergyStorage>()
        {
            @Override
            public INBT writeNBT(Capability<ModIEnergyStorage> capability, ModIEnergyStorage instance, Direction side)
            {
            	
            	CrudeTechMod.log("writeNBT " + IntNBT.valueOf(instance.getEnergyStored()));
                return IntNBT.valueOf(instance.getEnergyStored());
            }

            @Override
            public void readNBT(Capability<ModIEnergyStorage> capability, ModIEnergyStorage instance, Direction side, INBT nbt)
            {
                if (!(instance instanceof ModEnergyStorage))
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                ((ModEnergyStorage)instance).energy = ((IntNBT)nbt).getInt();
                CrudeTechMod.log("readNBT " + ((ModEnergyStorage)instance).energy);
            }
        },
        () -> new ModEnergyStorage(1000));
    }

	@Override
	public INBT serializeNBT() {
		
		return null;
	}

	@Override
	public void deserializeNBT(INBT nbt) {
		
	}

}