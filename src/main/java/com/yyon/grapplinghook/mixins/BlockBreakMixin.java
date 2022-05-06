
package com.yyon.grapplinghook.mixins;

import com.yyon.grapplinghook.client.ClientControllerManager;
import com.yyon.grapplinghook.controllers.GrappleController;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.yyon.grapplinghook.client.ClientSetup.clientControllerManager;


/**
 * @forge.event BreakEvent
 */
@Mixin(Block.class)
public abstract class BlockBreakMixin {

    @Inject(at = @At(value = "HEAD"), method = "onBreak")
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo cbinfo) {
        if (clientControllerManager.controllerPos.containsKey(pos)) {
            GrappleController control = clientControllerManager.controllerPos.get(pos);

            control.unattach();

            clientControllerManager.controllerPos.remove(pos);
        }
    }
}
