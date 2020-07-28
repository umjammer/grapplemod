/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.yyon.grapplinghook.entities;

import com.mojang.blaze3d.systems.RenderSystem;
import com.yyon.grapplinghook.Vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;


@Environment(value = EnvType.CLIENT)
public class GrappleArrowRenderer extends EntityRenderer<GrappleArrow> {

    protected final Item item;
    private final ItemRenderer itemRenderer;
    private static final Identifier LEASH_KNOT_TEXTURES = new Identifier("grapplemod", "textures/entity/rope.png");
    Identifier BLOCK_TEXTURE;

    public GrappleArrowRenderer(EntityRenderDispatcher dispatcher, Item item, ItemRenderer itemRenderer) {
        super(dispatcher);
        this.item = item;
        this.itemRenderer = itemRenderer;
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method,
     * always casting down its argument and then handing it off to a worker
     * function which does the actual work. In all probabilty, the class Render
     * is generic (Render<T extends Entity>) and this method has signature
     * public void func_76986_a(T entity, double d, double d1, double d2, float
     * f, float f1). But JAD is pre 1.5 so doe
     */
    @Override
    public void render(GrappleArrow entity,
                       float entityYaw,
                       float partialTicks,
                       MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers,
                       int light) {

        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();

        GrappleArrow arrow = entity;
        if (arrow == null || !arrow.isAlive()) {
            return;
        }

        SegmentHandler segmentHandler = arrow.segmenthandler;

        LivingEntity e = (LivingEntity) arrow.shootingEntity;

        if (e == null || !e.isAlive()) {
            return;
        }

        int primaryhand = e.getActiveHand() == Hand.MAIN_HAND ? 1 : -1; // TODO

        Vec3d offset = new Vec3d(0, 0, 0);
        if (!arrow.attached) {
            if (arrow.righthand) {
                offset = new Vec3d(primaryhand * -0.36D, -0.175D, 0.45D); // hand relative to person
            } else {
                offset = new Vec3d(primaryhand * 0.36D, -0.175D, 0.45D); // hand relative to person
            }
            offset = new Vec(offset).rotatePitch(-(e.prevPitch + (e.pitch - e.prevPitch) * partialTicks) * 0.017453292F)
                    .toVec3d();
            offset = new Vec(offset).rotateYaw(-(e.prevYaw + (e.yaw - e.prevYaw) * partialTicks) * 0.017453292F).toVec3d();

            double dist = e.distanceTo(arrow);
            double mult = 1 - (dist / 10.0);
            if (mult <= 0) {
                offset = new Vec3d(0, 0, 0);
            } else {
                offset = new Vec3d(offset.x * mult, offset.y * mult, offset.z * mult);

                x += offset.x;
                y += offset.y;
                z += offset.z;
            }
        }

//        RenderSystem.pushMatrix();
//        RenderSystem.translate((float)x, (float)y, (float)z);
//        RenderSystem.enableRescaleNormal();
//        RenderSystem.scale(0.5F, 0.5F, 0.5F); this.bindEntityTexture(entity);
//        RenderSystem.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
//        RenderSystem.rotate((float)(this.renderManager.options.
//        thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
//        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
//        vertexbuffer.pos(-0.5D, -0.5D, 0.0D).tex(0.0625D, 0.1875D).normal(0.0F, 1.0F, 0.0F).next();
//        vertexbuffer.pos(0.5D, -0.5D, 0.0D).tex(0.125D, 0.1875D).normal(0.0F, 1.0F, 0.0F).next();
//        vertexbuffer.pos(0.5D, 0.5D, 0.0D).tex(0.125D, 0.125D).normal(0.0F, 1.0F, 0.0F).next();
//        vertexbuffer.pos(-0.5D, 0.5D, 0.0D).tex(0.0625D, 0.125D).normal(0.0F, 1.0F, 0.0F).next();
//        tessellator.draw();
//        RenderSystem.disableRescaleNormal();
//        RenderSystem.popMatrix();

        int k = e.getActiveHand() == Hand.MAIN_HAND ? 1 : -1; // TODO
        float f7 = e.getHandSwingProgress(partialTicks);
        float f8 = MathHelper.sin(MathHelper.sqrt(f7) * (float) Math.PI);
        float f9 = (e.prevYaw + (e.yaw - e.prevYaw) * partialTicks) * 0.017453292F;
        double d0 = MathHelper.sin(f9);
        double d1 = MathHelper.cos(f9);
        double d2 = k * 0.35D;
        double d4;
        double d5;
        double d6;
        double d7;

        if ((this.dispatcher.gameOptions == null || !this.dispatcher.camera.isThirdPerson()) &&
            e == MinecraftClient.getInstance().player) {

            Vec3d vec3d;
            if (arrow.righthand) {
                vec3d = new Vec3d(k * -0.36D, -0.175D, 0.45D); // hand relative to person
            } else {
                vec3d = new Vec3d(k * 0.36D, -0.175D, 0.45D); // hand relative to person
            }
            vec3d = new Vec(vec3d).rotatePitch(-(e.prevPitch + (e.pitch - e.prevPitch) * partialTicks) * 0.017453292F).toVec3d();
            vec3d = new Vec(vec3d).rotateYaw(-(e.prevYaw + (e.yaw - e.prevYaw) * partialTicks) * 0.017453292F).toVec3d();
            vec3d = new Vec(vec3d).rotateYaw(f8 * 0.5F).toVec3d();
            vec3d = new Vec(vec3d).rotatePitch(-f8 * 0.7F).toVec3d();
            d4 = e.prevX + (e.getPos().x - e.prevX) * partialTicks + vec3d.x;
            d5 = e.prevY + (e.getPos().y - e.prevY) * partialTicks + vec3d.y;
            d6 = e.prevZ + (e.getPos().z - e.prevZ) * partialTicks + vec3d.z;
            d7 = e.getEyeY();
        } else {
            d4 = e.prevX + (e.getPos().x - e.prevX) * partialTicks - d1 * d2 - d0 * 0.8D;
            d5 = e.prevY + e.getEyeY() + (e.getPos().y - e.prevY) * partialTicks - 0.45D;
            d6 = e.prevZ + (e.getPos().z - e.prevZ) * partialTicks - d0 * d2 + d1 * 0.8D;
            d7 = e.isSneaking() ? -0.1875D : 0.0D;
        }

        double d13 = entity.prevX + (entity.getPos().x - entity.prevX) * partialTicks;
        double d8 = entity.prevY + (entity.getPos().y - entity.prevY) * partialTicks;
        double d9 = entity.prevZ + (entity.getPos().z - entity.prevZ) * partialTicks;

        // hand position
        double d10 = ((float) (d4 - d13)) - offset.x;
        double d11 = ((float) (d5 - d8)) + d7 - offset.y;
        double d12 = ((float) (d6 - d9)) - offset.z;

//        double X;
//        double Y;
//        double Z;

        Vec thispos = new Vec(x, y, z);
        Vec handpos = new Vec(d10 + x, d11 + y, d12 + z);
        Vec somethingpos = new Vec(d13, d8, d9).sub(thispos);

//        RenderSystem.disableTexture();
        RenderSystem.disableLighting();
        RenderSystem.disableCull();
        MinecraftClient.getInstance().getTextureManager().bindTexture(this.getTexture(entity));

        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder vertexbuffer = tessellator.getBuffer();

        if (segmentHandler == null) {
            this.drawSegment(thispos, handpos, arrow.taut, tessellator, vertexbuffer);
        } else {
            for (int i = 0; i < segmentHandler.segments.size() - 1; i++) {
                Vec from = segmentHandler.segments.get(i).sub(somethingpos);
                Vec to = segmentHandler.segments.get(i + 1).sub(somethingpos);

                if (i == 0) {
                    from = thispos;
                }
                if (i + 2 == segmentHandler.segments.size()) {
                    to = handpos;
                }

                double taut = 1;
                if (i == segmentHandler.segments.size() - 2) {
//                    taut = arrow.taut;
                }

                this.drawSegment(from, to, taut, tessellator, vertexbuffer);
            }
        }

        RenderSystem.enableLighting();
//        RenderSystem.enableTexture();
        RenderSystem.enableCull();

        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) x, (float) y, (float) z);
        RenderSystem.enableRescaleNormal();

        RenderSystem.rotatef(-this.dispatcher.camera.getYaw(), 0.0F, 1.0F, 0.0F);
        RenderSystem.rotatef((this.dispatcher.camera.isThirdPerson() ? -1 : 1) * this.dispatcher.camera.getPitch(),
                             1.0F,
                             0.0F,
                             0.0F);
        RenderSystem.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
        MinecraftClient.getInstance().getTextureManager().bindTexture(BLOCK_TEXTURE);

        this.itemRenderer.renderItem(this
                .getStackToRender(entity), ModelTransformation.Mode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);

        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();

        super.render(entity, entityYaw, partialTicks, matrices, vertexConsumers, light);
    }

    @SuppressWarnings("unused")
    private void drawDebug(Vec point, Tessellator tessellator, BufferBuilder vertexbuffer, float R, float G, float B) {
        double X = point.x;
        double Y = point.y;
        double Z = point.z;

        vertexbuffer.begin(5, VertexFormats.POSITION_COLOR);

        vertexbuffer.vertex(X, Y + 0.025D, Z).color(R, G, B, 1.0F).next();
        vertexbuffer.vertex(X - 0.025D, Y, Z - 0.025D).color(R, G, B, 1.0F).next();
        vertexbuffer.vertex(X, Y + 2 + 0.025D, Z).color(R, G, B, 1.0F).next();
        vertexbuffer.vertex(X - 0.025D, Y + 2, Z - 0.025D).color(R, G, B, 1.0F).next();

        tessellator.draw();
        vertexbuffer.begin(5, VertexFormats.POSITION_COLOR);

        vertexbuffer.vertex(X + 0.025D, Y, Z - 0.025D).color(R, G, B, 1.0F).next();
        vertexbuffer.vertex(X, Y + 0.025D, Z).color(R, G, B, 1.0F).next();
        vertexbuffer.vertex(X + 0.025D, Y + 2, Z - 0.025D).color(R, G, B, 1.0F).next();
        vertexbuffer.vertex(X, Y + 2 + 0.025D, Z).color(R, G, B, 1.0F).next();

        tessellator.draw();
        vertexbuffer.begin(5, VertexFormats.POSITION_COLOR);

        vertexbuffer.vertex(X, Y - 0.025D, Z).color(R, G, B, 1.0F).next();
        vertexbuffer.vertex(X + 0.025D, Y, Z - 0.025D).color(R, G, B, 1.0F).next();
        vertexbuffer.vertex(X, Y + 2 - 0.025D, Z).color(R, G, B, 1.0F).next();
        vertexbuffer.vertex(X + 0.025D, Y + 2, Z - 0.025D).color(R, G, B, 1.0F).next();

        tessellator.draw();
        vertexbuffer.begin(5, VertexFormats.POSITION_COLOR);

        vertexbuffer.vertex(X - 0.025D, Y, Z - 0.025D).color(R, G, B, 1.0F).next();
        vertexbuffer.vertex(X, Y - 0.025D, Z).color(R, G, B, 1.0F).next();
        vertexbuffer.vertex(X - 0.025D, Y + 2, Z - 0.025D).color(R, G, B, 1.0F).next();
        vertexbuffer.vertex(X, Y + 2 - 0.025D, Z).color(R, G, B, 1.0F).next();

        tessellator.draw();
    }

    private void drawSegment(Vec start, Vec finish, double taut, Tessellator tessellator, BufferBuilder vertexbuffer) {
        if (start.sub(finish).length() < 0.05) {
            return;
        }

        double X;
        double Y;
        double Z;

        double x = start.x;
        double y = start.y;
        double z = start.z;
        double d10 = finish.x - x;
        double d11 = finish.y - y;
        double d12 = finish.z - z;

        Vec forward = finish.sub(start).changeLen(1);
        Vec up = forward.cross(new Vec(1, 0, 0));
        if (up.length() == 0) {
            up = new Vec(1, 0, 0);
        }
        up.changeLenIp(0.025);
        Vec side = forward.cross(up);
        side.changeLenIp(0.025);

        Vec[] corners = new Vec[] {
            up.mult(-1).add(side.mult(-1)), up.add(side.mult(-1)), up.add(side), up.mult(-1).add(side)
        };

        for (int corner = 0; corner < 4; corner++) {
            vertexbuffer.begin(5, VertexFormats.POSITION_TEXTURE);

            Vec corner1 = corners[corner];
            Vec corner2 = corners[(corner + 1) % 4];

            for (int i1 = 0; i1 <= 16; ++i1) {
//                float R = 0.5F;
//                float G = 0.4F;
//                float B = 0.3F;
//
//                if (i1 % 2 == 0) {
//                    R *= 0.7F;
//                    G *= 0.7F;
//                    B *= 0.7F;
//                }

                float f10 = i1 / 16.0F;
                X = x + d10 * f10;
                Z = z + d12 * f10;
                Y = y + d11 * f10 - (1 - taut) * (0.25 - Math.pow((f10 - 0.5), 2)) * 1.5;

                vertexbuffer.vertex(X + corner1.x, Y + corner1.y, Z + corner1.z).texture(0, i1 / 15.0F).next();
                vertexbuffer.vertex(X + corner2.x, Y + corner2.y, Z + corner2.z).texture(1, i1 / 15.0F).next();
            }

            tessellator.draw();
        }
    }

    @Override
    public boolean shouldRender(GrappleArrow livingEntity, Frustum frustum, double camX, double camY, double camZ) {
        return true;
    }

    private ItemStack getStackToRender(GrappleArrow entityIn) {
        return new ItemStack(this.item);
    }

    @Override
    public Identifier getTexture(GrappleArrow entity) {
        return LEASH_KNOT_TEXTURES;
    }
}
