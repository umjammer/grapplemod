package com.yyon.grapplinghook.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.yyon.grapplinghook.items.GrapplehookItem;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import static com.yyon.grapplinghook.client.ClientSetup.clientControllerManager;
import static com.yyon.grapplinghook.common.CommonSetup.grapplingHookItem;


public class CrosshairRenderer extends DrawableHelper {

    private MinecraftClient mc = MinecraftClient.getInstance();
    private float zLevel = -90.0F;

    public void onHudRender(MatrixStack matrices, float tickDelta) {

        GameOptions gamesettings = this.mc.options;
        if (!gamesettings.getPerspective().isFirstPerson()) return;
        if (this.mc.player.isSpectator()) return;
        if (gamesettings.glDebugVerbosity > 0 && !gamesettings.hudHidden && !gamesettings.reducedDebugInfo) return;

        ClientPlayerEntity player = this.mc.player;
        ItemStack grapplehookItemStack = null;
        if ((player.getStackInHand(Hand.MAIN_HAND) != null && player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof GrapplehookItem)) {
            grapplehookItemStack = player.getStackInHand(Hand.MAIN_HAND);
        } else if ((player.getStackInHand(Hand.OFF_HAND) != null && player.getStackInHand(Hand.OFF_HAND).getItem() instanceof GrapplehookItem)) {
            grapplehookItemStack = player.getStackInHand(Hand.OFF_HAND);
        }

        if (grapplehookItemStack != null) {
            GrappleCustomization custom = grapplingHookItem.getCustomization(grapplehookItemStack);
            double angle = Math.toRadians(custom.angle);
            double verticalangle = Math.toRadians(custom.verticalthrowangle);
            if (player.isSneaking()) {
                angle = Math.toRadians(custom.sneakingangle);
                verticalangle = Math.toRadians(custom.sneakingverticalthrowangle);
            }

            if (!custom.doublehook) {
                angle = 0;
            }

            Window resolution = mc.getWindow();
            int w = resolution.getScaledWidth();
            int h = resolution.getScaledHeight();

            double fov = Math.toRadians(gamesettings.fov);
            fov *= player.getFovMultiplier();
            double l = ((double) h / 2) / Math.tan(fov / 2);

            if (!((verticalangle == 0) && (!custom.doublehook || angle == 0))) {
                int offset = (int) (Math.tan(angle) * l);
                int verticaloffset = (int) (-Math.tan(verticalangle) * l);

                drawCrosshair(matrices, w / 2 + offset, h / 2 + verticaloffset);
                if (angle != 0) {
                    drawCrosshair(matrices, w / 2 - offset, h / 2 + verticaloffset);
                }
            }

            if (custom.rocket && custom.rocket_vertical_angle != 0) {
                int verticaloffset = (int) (-Math.tan(Math.toRadians(custom.rocket_vertical_angle)) * l);
                drawCrosshair(matrices, w / 2, h / 2 + verticaloffset);
            }
        }

        double rocketFuel = clientControllerManager.rocketFuel;

        if (rocketFuel < 1) {
            Window resolution = mc.getWindow();
            int w = resolution.getScaledWidth();
            int h = resolution.getScaledHeight();

            int totalbarlength = w / 8;

            matrices.push();
            fill(matrices, w / 2 - totalbarlength / 2, h * 3 / 4, w / 2 - totalbarlength / 2 + totalbarlength, h * 3 / 4 + 2, 0x64808080);
            fill(matrices, w / 2 - totalbarlength / 2, h * 3 / 4, w / 2 - totalbarlength / 2 + (int) (totalbarlength * rocketFuel), h * 3 / 4 + 2, 0xc8ffffff);
            matrices.pop();
        }
    }

    private void drawCrosshair(MatrixStack mStack, int x, int y) {
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        drawTexture(mStack, (int) (x - (15.0F / 2)), (int) (y - (15.0F / 2)), 0, 0, 15, 15);
        RenderSystem.defaultBlendFunc();
    }
}
