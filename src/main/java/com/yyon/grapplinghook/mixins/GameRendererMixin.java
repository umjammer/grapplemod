package com.yyon.grapplinghook.mixins;

import com.yyon.grapplinghook.client.ClientControllerManager;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.controllers.AirfrictionController;
import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.yyon.grapplinghook.client.ClientSetup.clientControllerManager;


/**
 * @forge.event CameraSetup
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    private float currentCameraTilt = 0;
    private float roll;

    @Inject(
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;update(Lnet/minecraft/world/BlockView;Lnet/minecraft/entity/Entity;ZZF)V", shift = At.Shift.AFTER
            ), method = "renderWorld(FJLnet/minecraft/client/util/math/MatrixStack;)V"
    )
    private void renderWorld(float tickDelta, long limitTime, MatrixStack matrices, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (!MinecraftClient.getInstance().isRunning() || player == null) {
            return;
        }

        int id = player.getId();
        int targetCameraTilt = 0;
        if (clientControllerManager.controllers.containsKey(id)) {
            GrappleController controller = clientControllerManager.controllers.get(id);
            if (controller instanceof AirfrictionController afcontroller) {
                if (afcontroller.wasWallrunning) {
                    Vec walldirection = afcontroller.getWallDirection();
                    if (walldirection != null) {
                        Vec lookdirection = Vec.lookVec(player);
                        int dir = lookdirection.cross(walldirection).y > 0 ? 1 : -1;
                        targetCameraTilt = dir;
                    }
                }
            }
        }

        if (currentCameraTilt != targetCameraTilt) {
            float cameraDiff = targetCameraTilt - currentCameraTilt;
            if (cameraDiff != 0) {
                float anim_s = GrappleConfig.getClientConf().camera.wallrun_camera_animation_s;
                float speed = (anim_s == 0) ? 9999 :  1.0f / (anim_s * 20.0f);
                if (speed > Math.abs(cameraDiff)) {
                    currentCameraTilt = targetCameraTilt;
                } else {
                    currentCameraTilt += speed * (cameraDiff > 0 ? 1 : -1);
                }
            }
        }

        if (currentCameraTilt != 0) {
            roll += currentCameraTilt * GrappleConfig.getClientConf().camera.wallrun_camera_tilt_degrees;
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(roll));
        }
    }
}