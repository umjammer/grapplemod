package com.yyon.grapplinghook.mixins;

import com.yyon.grapplinghook.client.ClientControllerManager;
import com.yyon.grapplinghook.controllers.AirfrictionController;
import com.yyon.grapplinghook.controllers.ForcefieldController;
import com.yyon.grapplinghook.controllers.GrappleController;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.yyon.grapplinghook.client.ClientSetup.clientControllerManager;


@Mixin(Keyboard.class)
public abstract class KeyboardMixin {

    /**
     * @forge.event KeyInputEvent
     */
    @Inject(at = @At("HEAD"), method = "onKey")
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (!MinecraftClient.getInstance().isRunning() || player == null) {
            return;
        }

        GrappleController controller = null;
        if (ClientControllerManager.controllers.containsKey(player.getId())) {
            controller = ClientControllerManager.controllers.get(player.getId());
        }

        if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
            if (controller != null) {
                if (controller instanceof AirfrictionController && ((AirfrictionController) controller).wasSliding) {
                    controller.slidingJump();
                }
            }
        }

        clientControllerManager.checkSlide(MinecraftClient.getInstance().player);
    }

    /**
     * @forge.input MovementInputUpdateEvent
     */
    @Inject(at = @At("HEAD"), method = "onKey")
    public void onInputUpdate(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (!MinecraftClient.getInstance().isRunning() || player == null) {
            return;
        }

        int id = player.getId();
        if (ClientControllerManager.controllers.containsKey(id)) {
            Input input = MinecraftClient.getInstance().player.input;
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
