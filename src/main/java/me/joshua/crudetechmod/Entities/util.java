package me.joshua.crudetechmod.Entities;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;

public class util{


	public static void damageEntity(PlayerEntity player, DamageSource damageSrc, float damageAmount) {
		if (!player.isInvulnerableTo(damageSrc)) {
			damageAmount = net.minecraftforge.common.ForgeHooks.onLivingHurt(player, damageSrc, damageAmount);
			if (damageAmount <= 0)
				return;
			damageAmount = applyArmorCalculations(player, damageSrc, damageAmount);
			damageAmount = applyPotionDamageCalculations(player, damageSrc, damageAmount);
			float f2 = Math.max(damageAmount - player.getAbsorptionAmount(), 0.0F);
			player.setAbsorptionAmount(player.getAbsorptionAmount() - (damageAmount - f2));
			f2 = net.minecraftforge.common.ForgeHooks.onLivingDamage(player, damageSrc, f2);
			float f = damageAmount - f2;
			if (f > 0.0F && f < 3.4028235E37F) {
				player.addStat(Stats.DAMAGE_ABSORBED, Math.round(f * 10.0F));
			}

			if (f2 != 0.0F) {
				player.addExhaustion(damageSrc.getHungerDamage());
				float f1 = player.getHealth();
				player.setHealth(player.getHealth() - f2);
				player.getCombatTracker().trackDamage(damageSrc, f1, f2);
				if (f2 < 3.4028235E37F) {
					player.addStat(Stats.DAMAGE_TAKEN, Math.round(f2 * 10.0F));
				}

			}
		}
	}
	
	public static float simulatedDamage(PlayerEntity player, DamageSource damageSrc, float damageAmount) {
		damageAmount = applyArmorCalculations(player, damageSrc, damageAmount);
		damageAmount = applyPotionDamageCalculations(player, damageSrc, damageAmount);
		return damageAmount;
	}
	
	 protected static float applyArmorCalculations(PlayerEntity player, DamageSource source, float damage) {
	      if (!source.isUnblockable()) {
	         damageArmor(player, damage);
	         damage = CombatRules.getDamageAfterAbsorb(damage, (float)player.getTotalArmorValue(), (float)player.getAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getValue());
	      }

	      return damage;
	   }
	 
	 public static void damageArmor(PlayerEntity player, float damage) {
	      player.inventory.damageArmor(damage);
	   }

	   /**
	    * Reduces damage, depending on potions
	    */
	   public static float applyPotionDamageCalculations(PlayerEntity player, DamageSource source, float damage) {
	      if (source.isDamageAbsolute()) {
	         return damage;
	      } else {
	         if (player.isPotionActive(Effects.RESISTANCE) && source != DamageSource.OUT_OF_WORLD) {
	            int i = (player.getActivePotionEffect(Effects.RESISTANCE).getAmplifier() + 1) * 5;
	            int j = 25 - i;
	            float f = damage * (float)j;
	            float f1 = damage;
	            damage = Math.max(f / 25.0F, 0.0F);
	            float f2 = f1 - damage;
	            if (f2 > 0.0F && f2 < 3.4028235E37F) {
	               if (player instanceof ServerPlayerEntity) {
	                  ((ServerPlayerEntity)player).addStat(Stats.DAMAGE_RESISTED, Math.round(f2 * 10.0F));
	               } else if (source.getTrueSource() instanceof ServerPlayerEntity) {
	                  ((ServerPlayerEntity)source.getTrueSource()).addStat(Stats.DAMAGE_DEALT_RESISTED, Math.round(f2 * 10.0F));
	               }
	            }
	         }

	         if (damage <= 0.0F) {
	            return 0.0F;
	         } else {
	            int k = EnchantmentHelper.getEnchantmentModifierDamage(player.getArmorInventoryList(), source);
	            if (k > 0) {
	               damage = CombatRules.getDamageAfterMagicAbsorb(damage, (float)k);
	            }

	            return damage;
	         }
	      }
	   }

}
