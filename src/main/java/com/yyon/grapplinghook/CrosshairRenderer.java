
package com.yyon.grapplinghook;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.yyon.grapplinghook.items.GrappleBow;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;


public class CrosshairRenderer {

    public MinecraftClient mc;

    float zLevel = -90.0F;

    public CrosshairRenderer() {
        this.mc = MinecraftClient.getInstance();
    }

//    @SubscribeEvent
    public void onRenderGameOverlayPost(MatrixStack matrixStack, float tickDelta) {
        GameOptions gamesettings = this.mc.options;
        if (gamesettings.perspective > 0) {
            return;
        }
        if (this.mc.player.isSpectator() && this.mc.targetedEntity == null) {
            return;
        }
        if (gamesettings.debugEnabled && !gamesettings.hudHidden && !this.mc.player.getReducedDebugInfo() &&
            !gamesettings.reducedDebugInfo) {
            return;
        }

        renderCrossHairs(matrixStack, tickDelta, gamesettings.fov);
        renderExperience(matrixStack, tickDelta);
    }

    private void renderCrossHairs(MatrixStack matrixStack, float tickDelta, double fovd) {
        ClientPlayerEntity player = this.mc.player;
        ItemStack bow = null;
        if ((player.getStackInHand(Hand.MAIN_HAND) != null &&
             player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof GrappleBow)) {
            bow = player.getStackInHand(Hand.MAIN_HAND);
        } else if ((player.getStackInHand(Hand.OFF_HAND) != null &&
                    player.getStackInHand(Hand.OFF_HAND).getItem() instanceof GrappleBow)) {
            bow = player.getStackInHand(Hand.OFF_HAND);
        }

        if (bow != null) {
            GrappleCustomization custom = ((GrappleBow) GrappleMod.grapplebowitem).getCustomization(bow);
            double angle = Math.toRadians(custom.angle);
            // ((grappleBow) grapplemod.grapplebowitem).getAngle(player, bow));
            double verticalangle = Math.toRadians(custom.verticalThrowAngle);
            if (player.isSneaking()) {
                angle = Math.toRadians(custom.sneakingAngle);
                verticalangle = Math.toRadians(custom.sneakingVerticalThrowAngle);
            }

            if (!custom.doublehook) {
                angle = 0;
            }

            int w = mc.getWindow().getScaledWidth();
            int h = mc.getWindow().getScaledHeight();

            double fov = Math.toRadians(fovd);
            fov *= player.getFovModifier();
            double l = ((double) h / 2) / Math.tan(fov / 2);

            if (!((verticalangle == 0) && (!custom.doublehook || angle == 0))) {
                // float partialticks = event.getPartialTicks();

//                mc.entityRenderer.setupOverlayRendering(); // TODO
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                // mc.getTextureManager().bindTexture(Gui.ICONS);
                RenderSystem.enableBlend();

                int offset = (int) (Math.tan(angle) * l);
                int verticaloffset = (int) (-Math.tan(verticalangle) * l);

                RenderSystem.blendFuncSeparate(SrcFactor.ONE_MINUS_DST_COLOR,
                                               DstFactor.ONE_MINUS_SRC_COLOR,
                                               SrcFactor.ONE,
                                               DstFactor.ZERO);
                RenderSystem.enableAlphaTest();
                this.drawTexturedModalRect(w / 2 - 7 + offset, h / 2 - 7 + verticaloffset, 0, 0, 16, 16);
                if (angle != 0) {
                    this.drawTexturedModalRect(w / 2 - 7 - offset, h / 2 - 7 + verticaloffset, 0, 0, 16, 16);
                }
            }

            if (custom.rocket && custom.rocket_vertical_angle != 0) {
                int verticaloffset = (int) (-Math.tan(Math.toRadians(custom.rocket_vertical_angle)) * l);
                RenderSystem.blendFuncSeparate(SrcFactor.ONE_MINUS_DST_COLOR,
                                               DstFactor.ONE_MINUS_SRC_COLOR,
                                               SrcFactor.ONE,
                                               DstFactor.ZERO);
                RenderSystem.enableAlphaTest();
                this.drawTexturedModalRect(w / 2 - 7, h / 2 - 7 + verticaloffset, 0, 0, 16, 16);
            }
        }
    }

    private void renderExperience(MatrixStack matrixStack, float tickDelta) {
        double rocketFuel = GrappleMod.proxy.rocketFuel;

        if (rocketFuel < 1) {

            int w = mc.getWindow().getScaledWidth();
            int h = mc.getWindow().getScaledHeight();

            int totalbarlength = w / 8;

            RenderSystem.pushMatrix();
//            mc.entityRenderer.setupOverlayRendering();
//            RenderSystem.color(0.5F, 1.0F, 1.0F, 0.5F);
//            mc.getTextureManager().bindTexture(Gui.ICONS);
//            RenderSystem.enableBlend();
//            RenderSystem.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//            RenderSystem.enableAlpha();

            RenderSystem.disableLighting();
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
//            RenderSystem.enableBlend();
//            RenderSystem.enableAlpha();

//            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

            this.drawRect(w / 2 - totalbarlength / 2, h * 3 / 4, totalbarlength, 2, 50, 100);
            this.drawRect(w / 2 - totalbarlength / 2, h * 3 / 4, (int) (totalbarlength * rocketFuel), 2, 200, 255);

//            RenderSystem.disableBlend();
//            RenderSystem.disableAlpha();
            RenderSystem.enableTexture();
//            RenderSystem.enableLighting();
//            RenderSystem.enableDepth();

            RenderSystem.popMatrix();
//            RenderSystem.disableAlpha();
//            RenderSystem.disableBlend();
        }
    }

    private void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, VertexFormats.POSITION_TEXTURE);
        vertexbuffer.vertex(x + 0, y + height, this.zLevel).texture((textureX + 0) * f, (textureY + height) * f1).next();
        vertexbuffer.vertex(x + width, y + height, this.zLevel)
                .texture((textureX + width) * f, (textureY + height) * f1)
                .next();
        vertexbuffer.vertex(x + width, y + 0, this.zLevel).texture((textureX + width) * f, (textureY + 0) * f1).next();
        vertexbuffer.vertex(x + 0, y + 0, this.zLevel).texture((textureX + 0) * f, (textureY + 0) * f1).next();
        tessellator.draw();
    }

    private void drawRect(int x, int y, int width, int height, int g, int a) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, VertexFormats.POSITION_COLOR);
        vertexbuffer.vertex(x + 0, y + height, this.zLevel).color(g, g, g, a).next();
        vertexbuffer.vertex(x + width, y + height, this.zLevel).color(g, g, g, a).next();
        vertexbuffer.vertex(x + width, y + 0, this.zLevel).color(g, g, g, a).next();
        vertexbuffer.vertex(x + 0, y + 0, this.zLevel).color(g, g, g, a).next();
        tessellator.draw();
    }
}
