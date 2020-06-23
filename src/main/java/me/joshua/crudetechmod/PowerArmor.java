package me.joshua.crudetechmod;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class PowerArmor extends ArmorItem{
	public PowerArmor(IArmorMaterial materialIn, EquipmentSlotType slot, Properties builder) {
		super(materialIn, slot, builder);
	}

	private ModEnergyStorage newStorage() {
		return new ModEnergyStorage(10000, 1000, 1000);
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		return new ICapabilityProvider() {
			protected ModEnergyStorage storage = newStorage();
			protected LazyOptional<ModEnergyStorage> storageHolder = LazyOptional.of(() -> storage);

			public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
				if (cap == ModCapabilityEnergy.ENERGY) {
					return storageHolder.cast();
				}
				return LazyOptional.empty();
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
			CrudeTechMod.log("getEnergy: " + handler.getEnergyStored());
		});
		CrudeTechMod.log("getShareTag end");
		return tag;
	}

	
	@Override
	public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
		stack.getCapability(ModCapabilityEnergy.ENERGY, null).ifPresent(handler -> {
			handler.setEnergy(nbt.getInt("Energy"));
			CrudeTechMod.log("readEnergy: " + handler.getEnergyStored());
		});
		CrudeTechMod.log("readShareTag end");
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