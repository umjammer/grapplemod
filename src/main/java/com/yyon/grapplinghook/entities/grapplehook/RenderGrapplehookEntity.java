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

package com.yyon.grapplinghook.entities.grapplehook;

import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

import static com.yyon.grapplinghook.grapplemod.MODID;


public class RenderGrapplehookEntity extends EntityRenderer<GrapplehookEntity> {

//    protected final Item item;
    private static final Identifier HOOK_TEXTURES = new Identifier(MODID + ":textures/items/grapplinghook.png");
    private static final Identifier ROPE_TEXTURES = new Identifier(MODID + ":textures/entity/rope.png");

	public RenderGrapplehookEntity(EntityRendererFactory.Context context) {
		super(context);
	}

    @Override
	public void render(GrapplehookEntity hookEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if (hookEntity == null || !hookEntity.isAlive()) {
			return;
		}

		SegmentHandler segmenthandler = hookEntity.segmentHandler;

		LivingEntity playerentity= (LivingEntity) hookEntity.shootingEntity;

		if (playerentity == null || !playerentity.isAlive()) {
			return;
		}

		// draw hook

		// transformation so hook texture is facing camera
		matrices.push();
		matrices.scale(0.5F, 0.5F, 0.5F);
		matrices.multiply(this.dispatcher.camera.getRotation());
		matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		Matrix3f matrix3f = matrices.peek().getNormalMatrix();

		// draw hook texture
		VertexConsumer ivertexbuilder = vertexConsumers.getBuffer(RenderLayer.getText(HOOK_TEXTURES));
		vertex(ivertexbuilder, matrix4f, matrix3f, light, 0.0F, 0, 0, 1);
		vertex(ivertexbuilder, matrix4f, matrix3f, light, 1.0F, 0, 1, 1);
		vertex(ivertexbuilder, matrix4f, matrix3f, light, 1.0F, 1, 1, 0);
		vertex(ivertexbuilder, matrix4f, matrix3f, light, 0.0F, 1, 0, 0);

		// revert transformation
		matrices.pop();

		// get player hand position

		// is right hand?
		int hand_right = (playerentity.getMainArm() == Arm.RIGHT ? 1 : -1) * (hookEntity.rightHand ? 1 : -1);

		// attack/swing progress
		float f = playerentity.handSwingTicks; // TODO
		float f1 = (float) Math.sin(Math.sqrt(f) * (float) Math.PI);

		// get the offset from the center of the head to the hand
		Vec hand_offset;
		if (this.dispatcher.gameOptions == null || this.dispatcher.gameOptions.getPerspective().ordinal() <= 0) {
			// if first person

			// base hand offset (no swing, when facing +Z)
			double fov = this.dispatcher.gameOptions.fov;
			fov = fov / 100.0D;
			hand_offset = new Vec((double) hand_right * -0.46D * fov, -0.18D * fov, 0.38D);
			// apply swing
			hand_offset = hand_offset.rotatePitch(-f1 * 0.7F);
			hand_offset = hand_offset.rotateYaw(-f1 * 0.5F);
			// apply looking direction
			hand_offset = hand_offset.rotatePitch(-Vec.lerp(tickDelta, playerentity.prevPitch, playerentity.getPitch()) * ((float) Math.PI / 180F));
			hand_offset = hand_offset.rotateYaw(Vec.lerp(tickDelta, playerentity.prevYaw, playerentity.getYaw()) * ((float) Math.PI / 180F));
		} else {
			// if third person

			// base hand offset (no swing, when facing +Z)
			hand_offset = new Vec((double) hand_right * -0.36D, -0.65D + (playerentity.isSneaking() ? -0.1875F : 0.0F), 0.6D);
			// apply swing
			hand_offset = hand_offset.rotatePitch(f1 * 0.7F);
			// apply body rotation
			hand_offset = hand_offset.rotateYaw(Vec.lerp(tickDelta, playerentity.prevBodyYaw, playerentity.bodyYaw) * ((float) Math.PI / 180F));
		}

		// get the hand position
		hand_offset.y += playerentity.getEyeHeight(playerentity.getPose());
		Vec hand_position = hand_offset.add(Vec.partialPositionVec(playerentity, tickDelta));

		// draw rope

		// transformation (no tranformation)
        matrices.push();
		Matrix4f matrix4f1 = matrices.peek().getPositionMatrix();
		Matrix3f matrix3f1 = matrices.peek().getNormalMatrix();

        // initialize vertexbuffer (used for drawing)
        VertexConsumer vertexbuffer = vertexConsumers.getBuffer(RenderLayer.getText(ROPE_TEXTURES));
        
        // draw rope
        if (segmenthandler == null) {
        	// if no segmenthandler, straight line from hand to hook
    		drawSegment(new Vec(0,0,0), getRelativeToEntity(hookEntity, new Vec(hand_position), tickDelta), 1.0F, vertexbuffer, matrix4f1, matrix3f1, light);
        } else {
        	for (int i = 0; i < segmenthandler.segments.size() - 1; i++) {
        		Vec from = segmenthandler.segments.get(i);
        		Vec to = segmenthandler.segments.get(i+1);

        		if (i == 0) {
        			from = Vec.partialPositionVec(hookEntity, tickDelta);
        		}
        		if (i + 2 == segmenthandler.segments.size()) {
        			to = hand_position;
        		}

        		from = getRelativeToEntity(hookEntity, from, tickDelta);
        		to = getRelativeToEntity(hookEntity, to, tickDelta);

        		double taut = 1;
        		if (i == segmenthandler.segments.size() - 2) {
//        			taut = hookEntity.taut;
        		}

        		drawSegment(from, to, taut, vertexbuffer, matrix4f1, matrix3f1, light);
        	}
        }

		matrices.pop();

		super.render(hookEntity, yaw, tickDelta, matrices, vertexConsumers, light);
    }
    
    Vec getRelativeToEntity(GrapplehookEntity hookEntity, Vec inVec, float partialTicks) {
    	return inVec.sub(Vec.partialPositionVec(hookEntity, partialTicks));
    }
    
    // vertex for the hook
    private static void vertex(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, int light, float x, int y, int u, int v) {
        vertexConsumer.vertex(matrix4f, x - 0.5F, (float)y - 0.5F, 0.0F).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3f, 0.0F, 1.0F, 0.0F).next();
	}

    // draw a segment of the rope
    public void drawSegment(Vec start, Vec finish, double taut, VertexConsumer vertexbuffer, Matrix4f matrix, Matrix3f matrix3, int light) {
    	if (start.sub(finish).length() < 0.05) {
    		return;
    	}

        int number_squares = 16;
        if (taut == 1.0F) {
        	number_squares = 1;
        }

    	Vec diff = finish.sub(start);
        
        Vec forward = diff.changeLen(1);
        Vec up = forward.cross(new Vec(1, 0, 0));
        if (up.length() == 0) {
        	up = forward.cross(new Vec(0, 0, 1));
        }
        up.changeLen_ip(0.025);
        Vec side = forward.cross(up);
        side.changeLen_ip(0.025);

        Vec[] corners = new Vec[] {up.mult(-1).add(side.mult(-1)), up.add(side.mult(-1)), up.add(side), up.mult(-1).add(side)};

        for (int size = 0; size < 4; size++) {
            Vec corner1 = corners[size];
            Vec corner2 = corners[(size + 1) % 4];

        	Vec normal = new Vec(0,1,0);// corner1.add(corner2).normalize();

            for (int square_num = 0; square_num < number_squares; square_num++) {
                float squarefrac1 = (float)square_num / (float) number_squares;
                Vec pos1 = start.add(diff.mult(squarefrac1));
                pos1.y += - (1 - taut) * (0.25 - Math.pow((squarefrac1 - 0.5), 2)) * 1.5;
                float squarefrac2 = ((float) square_num+1) / (float) number_squares;
                Vec pos2 = start.add(diff.mult(squarefrac2));
                pos2.y += - (1 - taut) * (0.25 - Math.pow((squarefrac2 - 0.5), 2)) * 1.5;

                Vec corner1pos1 = pos1.add(corner1);
                Vec corner2pos1 = pos1.add(corner2);
                Vec corner1pos2 = pos2.add(corner1);
                Vec corner2pos2 = pos2.add(corner2);

                vertexbuffer.vertex(matrix, (float) corner1pos1.x, (float) corner1pos1.y, (float) corner1pos1.z).color(255, 255, 255, 255).texture(0, squarefrac1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3, (float) normal.x, (float) normal.y, (float) normal.z).next();
                vertexbuffer.vertex(matrix, (float) corner2pos1.x, (float) corner2pos1.y, (float) corner2pos1.z).color(255, 255, 255, 255).texture(1, squarefrac1).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3, (float) normal.x, (float) normal.y, (float) normal.z).next();
                vertexbuffer.vertex(matrix, (float) corner2pos2.x, (float) corner2pos2.y, (float) corner2pos2.z).color(255, 255, 255, 255).texture(1, squarefrac2).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3, (float) normal.x, (float) normal.y, (float) normal.z).next();
                vertexbuffer.vertex(matrix, (float) corner1pos2.x, (float) corner1pos2.y, (float) corner1pos2.z).color(255, 255, 255, 255).texture(0, squarefrac2).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix3, (float) normal.x, (float) normal.y, (float) normal.z).next();
            }
        }
    }

    @Override
	public boolean shouldRender(GrapplehookEntity entity, Frustum frustum, double x, double y, double z) {
		return true;
	}

//	public ItemStack getStackToRender(T entityIn)
//    {
//        return new ItemStack(this.item);
//    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
	@Override
	public Identifier getTexture(GrapplehookEntity entity) {
        return HOOK_TEXTURES;
	}
}
