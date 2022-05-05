
package com.yyon.grapplinghook.mixins;

import com.yyon.grapplinghook.items.GrapplehookItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @forge.BreakEvent
 */
@Mixin(Block.class)
public abstract class ServerBlockBreakMixin {

    @Inject(at = @At(value = "HEAD"), method = "onBreak", cancellable = true)
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
        if (player != null) {
            ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
            if (stack != null) {
                Item item = stack.getItem();
                if (item instanceof GrapplehookItem) {
                    ci.cancel();
                }
            }
        }
    }
}
