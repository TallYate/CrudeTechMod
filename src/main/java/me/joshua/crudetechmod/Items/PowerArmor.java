package me.joshua.crudetechmod.Items;

import java.util.List;

import javax.annotation.Nullable;

import me.joshua.crudetechmod.CrudeTechMod;
import me.joshua.crudetechmod.Energy.ModCapabilityEnergy;
import me.joshua.crudetechmod.Energy.ModEnergyStorage;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

//For Testing Event - remove later
@Mod.EventBusSubscriber(modid = CrudeTechMod.MOD_ID, bus = Bus.FORGE)
public class PowerArmor extends ArmorItem {
	public PowerArmor(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
		super(materialIn, slot, builder);
	}

	private ModEnergyStorage newStorage() {
		return new ModEnergyStorage(10000, 1000, 1000);
	}

@Override
public ICapabilitySerializable<IntNBT> initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
	return new ICapabilitySerializable<IntNBT>() {
		protected ModEnergyStorage storage = newStorage();
		protected LazyOptional<ModEnergyStorage> storageHolder = LazyOptional.of(() -> storage);

		public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
			if (cap == ModCapabilityEnergy.ENERGY) {
				return storageHolder.cast();
			}
			return LazyOptional.empty();
		}
		@Override
		public IntNBT serializeNBT() {
			return IntNBT.valueOf(storage.getEnergyStored());
		}

		@Override
		public void deserializeNBT(IntNBT nbt) {
			storage.setEnergy(nbt.getInt());
		}
	};
}

	@Override
	public boolean shouldSyncTag() {
		return true;
	}

	@Override
	@Nullable
	public CompoundNBT getShareTag(ItemStack stack) {
		CompoundNBT tag = stack.getOrCreateTag();
		stack.getCapability(ModCapabilityEnergy.ENERGY, null).ifPresent(handler -> {
			tag.putInt("Energy", handler.getEnergyStored());
		});
		return tag;
	}

	@Override
	public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
		stack.getCapability(ModCapabilityEnergy.ENERGY, null).ifPresent(handler -> {
			handler.setEnergy(nbt.getInt("Energy"));
		});
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip,
			ITooltipFlag flagIn) {
		IEnergyStorage cap = stack.getCapability(ModCapabilityEnergy.ENERGY, null).orElseGet(null);
		if (cap != null) {
			String charge = Integer.toString(cap.getEnergyStored());
			String capacity = Integer.toString(cap.getMaxEnergyStored());
			tooltip.add(new TranslationTextComponent("Charge: " + charge + "/" + capacity));
		} else {
			tooltip.add(new TranslationTextComponent("missing capability"));
		}
	}
}