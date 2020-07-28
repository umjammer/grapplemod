
package com.yyon.grapplinghook.enchantments;

import com.yyon.grapplinghook.GrappleMod;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;


public class DoublejumpEnchantment extends Enchantment {

    public DoublejumpEnchantment() {
        super(Rarity.UNCOMMON,
              GrappleMod.GRAPPLEENCHANTS_FEET,
              new EquipmentSlot[] {
                  EquipmentSlot.FEET
              });
//        this.setName("doublejumpenchantment");
    }
}
