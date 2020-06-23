package me.joshua.crudetechmod;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.energy.EnergyStorage;

public class ModCapabilityEnergy
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
                return IntNBT.valueOf(instance.getEnergyStored());
            }

            @Override
            public void readNBT(Capability<ModIEnergyStorage> capability, ModIEnergyStorage instance, Direction side, INBT nbt)
            {
                if (!(instance instanceof ModEnergyStorage))
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                ((ModEnergyStorage)instance).energy = ((IntNBT)nbt).getInt();
            }
        },
        () -> new ModEnergyStorage(1000));
    }
}