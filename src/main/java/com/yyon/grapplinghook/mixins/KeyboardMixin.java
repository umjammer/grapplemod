package com.yyon.grapplinghook.mixins;

import com.yyon.grapplinghook.client.ClientControllerManager;
import com.yyon.grapplinghook.controllers.AirfrictionController;
import com.yyon.grapplinghook.controllers.GrappleController;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
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
    @Inject(at = @At("TAIL"), method = "onKey")
    public void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (!MinecraftClient.getInstance().isRunning() || player == null) {
            return;
        }

        GrappleController controller = null;
        if (clientControllerManager.controllers.containsKey(player.getId())) {
            controller = clientControllerManager.controllers.get(player.getId());
        }

        if (MinecraftClient.getInstance().options.jumpKey.isPressed()) {
            if (controller != null) {
                if (controller instanceof AirfrictionController && ((AirfrictionController) controller).wasSliding) {
                    controller.slidingJump();
                }
            }
        }

        clientControllerManager.checkSlide(player);
    }
}
