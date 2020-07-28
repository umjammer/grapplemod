
package com.yyon.grapplinghook.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.items.GrappleBow;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


@Mixin(Block.class)
public abstract class BlockBreakMixin {

    @Inject(at = @At(value = "HEAD"), method = "onBreak")
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo cbinfo) {
        if (world.isClient) {
            if (pos != null) {
                if (GrappleMod.controllerPos.containsKey(pos)) {
                    GrappleController control = GrappleMod.controllerPos.get(pos);
                    control.unattach();
                    GrappleMod.controllerPos.remove(pos);
                }
            }
        } else {
            if (player != null) {
                ItemStack stack = player.getMainHandStack();
                if (stack != null) {
                    Item item = stack.getItem();
                    if (item instanceof GrappleBow) {
                        cbinfo.cancel();
                        return;
                    }
                }
            }
//            this.blockbreak(event);
        }
    }
}
