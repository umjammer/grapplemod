package com.yyon.grapplinghook.mixins;

import com.yyon.grapplinghook.items.GrapplehookItem;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.yyon.grapplinghook.grapplemod.LOGGER;

/**
 * @forge.event GrapplehookItem.onDroppedByPlayer
 */
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    @Inject(at = @At(value = "TAIL"), method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;")
    void dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        Item item = stack.getItem();
LOGGER.info("PlayerEntityMixin::dropItem: " + item);
        if (item instanceof GrapplehookItem grapplehookItem) {
            PlayerEntity player = ((PlayerEntity) (Object) this);
            grapplehookItem.onDroppedByPlayer(stack, player);
        }
    }
}
