
package com.yyon.grapplinghook.controllers;

import com.yyon.grapplinghook.ClientProxyClass;
import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.Vec;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

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


public class AirFrictionController extends GrappleController {
    public final double playerMovementMult = 0.5;

    public int ignoreGroundCounter = 0;

    public AirFrictionController(int arrowId, int entityId, World world, Vec pos, int id, GrappleCustomization custom) {
        super(arrowId, entityId, world, pos, id, custom);
    }

    @Override
    public void updatePlayerPos() {
        Entity entity = this.entity;

        if (this.attached) {
            boolean isSliding = ClientProxyClass.isWearingSlidingEnchant(this.entity) && ClientProxyClass.keySlide.isPressed();

            if (this.ignoreGroundCounter <= 0) {
                this.normalGround(!isSliding);
                this.normalCollisions(!isSliding);
            }
            this.applyAirFriction();

            isSliding = GrappleMod.proxy.isSliding(this.entity);

            if (this.entity.isInsideWaterOrBubbleColumn()) {
                this.unattach();
                return;
            }

            boolean doesRocket = false;
            if (this.custom != null) {
                if (this.custom.rocket) {
                    Vec rocket = this.rocket(entity);
                    this.motion.addIp(rocket);
                    if (rocket.length() > 0) {
                        doesRocket = true;
                    }
                }
            }

            if (isSliding) {
                this.applySlidingFriction();
            }

            boolean wallrun = this.applyWallrun();

            if (!isSliding) {
                if (wallrun) {
                    this.playerMovement.changeLenIp(GrappleConfig.getconf().wallrun_speed * 1.5);
                    if (this.wallDirection != null) {
                        this.playerMovement = this.playerMovement.removeAlong(this.wallDirection);
                    }
                    if (this.playerMovement.length() > GrappleConfig.getconf().wallrun_speed) {
                        this.playerMovement.changeLenIp(GrappleConfig.getconf().wallrun_speed);
                    }
                    motion.addIp(this.playerMovement);
                    if (this.motion.length() > GrappleConfig.getconf().wallrun_max_speed) {
                        this.motion.changeLenIp(GrappleConfig.getconf().wallrun_max_speed);
                    }
                    this.wallrunPressAgainstWall();
                } else {
                    motion.addIp(this.playerMovement.changeLen(0.01));
                }
            }

            if (entity instanceof LivingEntity) {
                LivingEntity entityliving = (LivingEntity) entity;
                if (entityliving.isFallFlying()) {
                    this.unattach();
                }
            }

            Vec gravity = new Vec(0, -0.05, 0);

            if (!wallrun) {
                motion.addIp(gravity);
            }

            Vec newmotion;

            newmotion = motion;

//			if (wallrun) {
//				newmotion.add_ip(this.walldirection);
//			}

            entity.setVelocity(newmotion.toVec3d());

            this.updateServerPos();

            if (entity.isOnGround()) {
                if (!isSliding) {
                    if (!wallrun) {
                        if (!doesRocket) {
                            if (ignoreGroundCounter <= 0) {
                                this.unattach();
                            }
                        } else {
                            motion = Vec.motionVec(entity);
                        }
                    }
                }
            }
            if (ignoreGroundCounter > 0) {
                ignoreGroundCounter--;
            }
        }
    }

    public void receiveEnderLaunch(double x, double y, double z) {
        super.receiveEnderLaunch(x, y, z);
        this.ignoreGroundCounter = 2;
    }

    public void slidingJump() {
        super.slidingJump();
        this.ignoreGroundCounter = 2;
    }
}
