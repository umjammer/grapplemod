package com.yyon.grapplinghook.mixins;

import com.yyon.grapplinghook.client.ClientControllerManager;
import com.yyon.grapplinghook.controllers.AirfrictionController;
import com.yyon.grapplinghook.controllers.ForcefieldController;
import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.items.GrapplehookItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.yyon.grapplinghook.grapplemod.LOGGER;


/**
 * @forge.input MovementInputUpdateEvent
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(Z)V", shift = At.Shift.AFTER
            ), method = "tickMovement"
    )
    void tickMovement(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        if (!MinecraftClient.getInstance().isRunning() || player == null) {
            return;
        }

        int id = player.getId();
        if (ClientControllerManager.controllers.containsKey(id)) {
            Input input = player.input;
            GrappleController control = ClientControllerManager.controllers.get(id);
            control.receivePlayerMovementMessage(input.movementSideways, input.movementForward, input.jumping, input.sneaking);

            boolean overrideMovement = true;
            if (MinecraftClient.getInstance().player.isOnGround()) {
                if (!(control instanceof AirfrictionController) && !(control instanceof ForcefieldController)) {
                    overrideMovement = false;
                }
            }

            if (overrideMovement) {
                input.jumping = false;
                input.pressingBack = false;
                input.pressingForward = false;
                input.pressingLeft = false;
                input.pressingRight = false;
                input.movementForward = 0;
                input.movementSideways = 0;
//				input.sneak = false; // fix alternate throw angles
            }
        }
    }
}
