
package com.yyon.grapplinghook.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;


public class GrappleEnchantsFeet extends EnchantmentTargetMixin {
    @Override
    public boolean isAcceptableItem(Item item) {
        return item instanceof ArmorItem && ((ArmorItem) item).getSlotType() == EquipmentSlot.FEET;
    }
}

@Mixin(EnchantmentTarget.class)
abstract class EnchantmentTargetMixin {
    @Shadow
    abstract boolean isAcceptableItem(Item item);
}
