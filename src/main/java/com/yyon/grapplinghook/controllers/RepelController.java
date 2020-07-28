
package com.yyon.grapplinghook.controllers;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.Vec;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;


public class RepelController extends GrappleController {

    public RepelController(int arrowId, int entityId, World world, Vec pos, int id) {
        super(arrowId, entityId, world, pos, id, null);

        this.playerMovementMult = 1;
    }

    public void updatePlayerPos() {
        Entity entity = this.entity;

        if (this.attached) {
            if (entity != null) {
                if (true) {
                    this.normalGround(true);
                    this.normalCollisions(true);
//                    this.applyAirFriction();

                    Vec playerpos = Vec.positionVec(entity);

//                    double dist = oldspherevec.length();

                    if (entity instanceof PlayerEntity) {
//                        EntityPlayer player = (EntityPlayer) entity;
                        if (GrappleMod.proxy.isSneaking(entity)) {
                            motion.multIp(0.95);
                        }
                        applyPlayerMovement();
                    }

                    Vec blockpush = checkRepel(playerpos, entity.world);
                    blockpush.multIp(0.5);
                    blockpush = new Vec(blockpush.x * 0.5, blockpush.y * 2, blockpush.z * 0.5);
                    this.motion.addIp(blockpush);

                    if (!entity.isOnGround()) {
                        motion.addIp(0, -0.05, 0);
                    }

                    entity.setVelocity(motion.toVec3d());

                    this.updateServerPos();
                }
            }
        }
    }
}
