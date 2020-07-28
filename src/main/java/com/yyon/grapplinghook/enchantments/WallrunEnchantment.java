
package com.yyon.grapplinghook.enchantments;

import com.yyon.grapplinghook.GrappleMod;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;


public class WallrunEnchantment extends Enchantment {
    public WallrunEnchantment() {
        super(Rarity.UNCOMMON,
              GrappleMod.GRAPPLEENCHANTS_FEET,
              new EquipmentSlot[] {
                  EquipmentSlot.FEET
              });
//        this.setName("wallrunenchantment");
    }
}
