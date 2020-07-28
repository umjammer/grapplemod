
package com.yyon.grapplinghook.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;


public class EntityTypeGrappleArrow extends EntityTypeMixin {
    @Override
    public boolean isAcceptableItem(Item item) {
        return item instanceof ArmorItem && ((ArmorItem) item).getSlotType() == EquipmentSlot.FEET;
    }
}

@Mixin(EntityType.class)
abstract class EntityTypeMixin {
    @Shadow
    abstract boolean isAcceptableItem(Item item);
}
