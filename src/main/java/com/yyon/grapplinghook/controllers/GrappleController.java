package com.yyon.grapplinghook.controllers;

import java.util.HashSet;
import java.util.Set;

import com.yyon.grapplinghook.ClientProxyClass;
import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.Vec;
import com.yyon.grapplinghook.entities.GrappleArrow;
import com.yyon.grapplinghook.network.GrappleEndMessage;
import com.yyon.grapplinghook.network.PlayerMovementMessage;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
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

public class GrappleController {
//	public int arrowId;
	public int entityId;
	public World world;
//	public vec pos;
	
//	public grappleArrow arrow;
	public Entity entity;
	
	public Set<GrappleArrow> arrows = new HashSet<>();
	public Set<Integer> arrowIds = new HashSet<>();
	
	public boolean attached = true;
	
//	public double r;
	public Vec motion;
	
	public double playerForward = 0;
	public double playerStrafe = 0;
	public boolean playerJump = false;
//	public boolean waitingonplayerjump = false;
	public Vec playerMovementUnrotated = new Vec(0,0,0);
	public Vec playerMovement = new Vec(0,0,0);
	
//	public int counter = 0;
	public int onGroundTimer = 0;
	public int maxOnGroundTimer = 3;
	
	public double maxLen;
	
	public double playerMovementMult = 0;
	
	public int controllerId;
	
//	public ClientProxyClass clientproxy = null;
	
//	public final double playermovementmult = 0.5;
	
//	public SegmentHandler segmenthandler;
	
	public GrappleCustomization custom = null;
	
	public GrappleController(int arrowId, int entityId, World world, Vec pos, int controllerid, GrappleCustomization custom) {
		this.entityId = entityId;
		this.world = world;
//		this.pos = pos;
		this.custom = custom;
		
		if (this.custom != null) {
			this.playerMovementMult = this.custom.playermovementmult;
			this.maxLen = custom.maxlen;
		}
		
		this.controllerId = controllerid;
		
		this.entity = world.getEntityById(entityId);
//		grappleArrow arrow = (grappleArrow) world.getEntityByID(arrowId);
//		if (arrow != null) {
//			((grappleArrow) arrow).segmenthandler = this.segmenthandler;
//			this.segmenthandler = ((grappleArrow) arrow).segmenthandler;
//			arrow.r = ((grappleArrow) arrow).segmenthandler.getDist(this.pos, vec.positionvec(entity).add(new vec(0, entity.getEyeHeight(), 0)));
//		}
		
//		this.r = this.pos.sub(vec.positionvec(entity)).length();
		this.motion = Vec.motionVec(entity);
		
		this.onGroundTimer = 0;
		
		GrappleMod.registerController(this.entityId, this);
		
//		if (grapplemod.proxy instanceof ClientProxyClass) {
//			this.clientproxy = (ClientProxyClass) grapplemod.proxy;
//		}
		
		if (arrowId != -1) {
			Entity arrowentity = world.getEntityById(arrowId);
			if (arrowentity != null && arrowentity.isAlive() && arrowentity instanceof GrappleArrow) {
				this.addArrow((GrappleArrow)arrowentity);
			} else {
				this.unattach();
			}
		}
		
		if (custom != null && custom.rocket) {
			GrappleMod.proxy.updateRocketRegen(custom.rocketActiveTime, custom.rocketRefuelRatio);
		}
	}
	
	public void unattach() {
		if (GrappleMod.controllers.containsValue(this)) {
			this.attached = false;
			
			GrappleMod.unregisterController(this.entityId);
			
			if (this.controllerId != GrappleMod.AIRID) {
				new GrappleEndMessage(this.entityId, this.arrowIds).send();
				GrappleMod.createControl(GrappleMod.AIRID, -1, this.entityId, this.entity.world, new Vec(0,0,0), null, this.custom);
			}
		}
	}
	
/*	public grappleArrow getArrow() {
		return (grappleArrow) world.getEntityByID(arrowId);
	}*/
	
	public void doClientTick() {
		if (this.attached) {
			if (this.entity == null || !this.entity.isAlive()) {
				this.unattach();
			} else {
				GrappleMod.proxy.getplayermovement(this, this.entityId);
				this.updatePlayerPos();
			}
		}
	}
		
	public void receivePlayerMovementMessage(float strafe,
			float forward, boolean jump) {
		playerForward = forward;
		playerStrafe = strafe;
//		if (!jump) {
//			playerjump = false;
//		} else if (jump && !playerjump) {
//			playerjump = true;
//			waitingonplayerjump = true;
//		}
		playerMovementUnrotated = new Vec(strafe, 0, forward);
		playerMovement = playerMovementUnrotated.rotateYaw((float) (this.entity.yaw * (Math.PI / 180.0)));
	}
	
//	public boolean isjumping() {
//		if (playerjump && waitingonplayerjump) {
//			waitingonplayerjump = false;
//			return true;
//		}
//		return false;
//	}
		
	public void updatePlayerPos() {
		Entity entity = this.entity;
		
		if (this.attached) {
			if(entity != null) {
				if (true) {
					this.normalGround(true);
					this.normalCollisions(true);
					this.applyAirFriction();
					
					Vec playerpos = Vec.positionVec(entity);
					playerpos = playerpos.add(new Vec(0, entity.getEyeY(), 0));
					

//					Vec3 playermotion = new Vec3(entity.motionX, entity.motionY, entity.motionZ);
					
					Vec additionalmotion = new Vec(0,0,0);;
					
					Vec gravity = new Vec(0, -0.05, 0);

					
//					if (!(this.ongroundtimer > 0)) {
						motion.addIp(gravity);
//					}
					
					boolean doJump = false;
					double jumpSpeed = 0;
					boolean isClimbing = false;
					
					// is motor active? (check motorwhencrouching / motorwhennotcrouching)
					boolean motor = false;
					if (this.custom.motor) {
						if (ClientProxyClass.keyMotorOnOff.isPressed() && this.custom.motorwhencrouching) {
							motor = true;
						} else if (!ClientProxyClass.keyMotorOnOff.isPressed() && this.custom.motorwhennotcrouching) {
							motor = true;
						}
					}
					
//					double curspeed = 0;
					boolean close = false;
//					vec averagemotiontowards = new vec(0, 0, 0);
					
					for (GrappleArrow arrow : this.arrows) {
						Vec arrowpos = Vec.positionVec(arrow);//this.getPositionVector();
						
						// Update segment handler (handles rope bends)
						if (this.custom.phaserope) {
							arrow.segmenthandler.updatepos(arrowpos, playerpos, arrow.r);
						} else {
							arrow.segmenthandler.update(arrowpos, playerpos, arrow.r, false);
						}
						
						// vectors along rope
						Vec anchor = arrow.segmenthandler.getclosest(arrowpos);
						double distToAnchor = arrow.segmenthandler.getDistToAnchor();
						double remaininglength = arrow.r - distToAnchor;
						
						Vec oldspherevec = playerpos.sub(anchor);
						Vec spherevec = oldspherevec.changeLen(remaininglength);
						Vec spherechange = spherevec.sub(oldspherevec);
//						Vec3 spherepos = spherevec.add(arrowpos);
						
						if (motor) {
							arrow.r = distToAnchor + oldspherevec.length();
						}
						
//						averagemotiontowards.add_ip(spherevec.changelen(-1));
						
//						curspeed += motion.proj(oldspherevec).length();
						
						// snap to rope length
						if (oldspherevec.length() < remaininglength) {
						} else {
							if (!motor) {
								additionalmotion = spherechange;
							}
						}
						
						double dist = oldspherevec.length();
						
						this.calcTaut(dist, arrow);

						// handle keyboard input (jumping and climbing)
						if (entity instanceof PlayerEntity) {
							PlayerEntity player = (PlayerEntity) entity;
							boolean isjumping = ClientProxyClass.keyJumpAndDetach.isPressed();
							isjumping = isjumping && !playerJump; // only jump once when key is first pressed
							playerJump = ClientProxyClass.keyJumpAndDetach.isPressed();
							if (isjumping) {
								// jumping
								if (onGroundTimer > 0) { // on ground: jump normally
									
								} else {
									doJump = true;
									jumpSpeed = this.getJumpPower(player, spherevec, arrow);
								}
							}
							if (ClientProxyClass.keySlow.isPressed()) {
								// climbing
	//							motion = multvec(motion, 0.9);
								Vec motiontorwards = spherevec.changeLen(-0.1);
								motiontorwards = new Vec(motiontorwards.x, 0, motiontorwards.z);
								if (motion.dot(motiontorwards) < 0) {
									motion.addIp(motiontorwards);
								}

								Vec newmotion = dampEnmotion(motion, motiontorwards);
								motion = new Vec(newmotion.x, motion.y, newmotion.z);
	//							motion = multvec(motion, 0.98);

							}
							if ((ClientProxyClass.keyClimb.isPressed() || !this.custom.climbkey) && !motor) {
								isClimbing = true;
								if (anchor.y > playerpos.y) {
									// when shift is pressed, stop swinging
									
									// climb up/down rope
									float playerforward = 0;
									if (ClientProxyClass.keyClimbUp.isPressed()) { playerforward = 0.3f; }
									else if (ClientProxyClass.keyClimbDown.isPressed()) { playerforward = -0.3f; }
									if (playerforward != 0) {
											if (dist < maxLen || this.playerForward > 0 || maxLen == 0) {
//												double motionup = this.playerforward;
												additionalmotion = new Vec(0, playerforward, 0);
//												this.r = dist;
												arrow.r = dist + distToAnchor;
												arrow.r -= playerforward*0.3;
												if (arrow.r < distToAnchor) {
													arrow.r = dist + distToAnchor;
												}
											}
									}
								}
							}
						}
						if (dist + distToAnchor < 2) {
							close = true;
						}
						
						// swing along max rope length
						if (anchor.sub(playerpos.add(motion)).length() > remaininglength && !motor) { // moving away
							motion = motion.removeAlong(spherevec);
						}
					}
					
//					curspeed = curspeed / this.arrows.size();
//					averagemotiontowards.mult_ip(1 / this.arrows.size());
					
		        	Vec facing = new Vec(entity.getCameraPosVec(1f)).normalize();
		        	
		        	// Motor
					if (motor) {
						boolean dopull = true;
						
						// if only one rope is pulling and not oneropepull, disable motor
						if (this.custom.doublehook && this.arrows.size() == 1) {
							boolean isdouble = true;
							for (GrappleArrow arrow : this.arrows) {
								if (!arrow.isdouble) {
									isdouble = false;
								}
							}
							if (isdouble && !this.custom.oneropepull) {
								dopull = false;
							}
						}
						
/*						if (curspeed > this.custom.motormaxspeed) {
							motion.changelen_ip(this.custom.motormaxspeed);
						}*/
						
						Vec totalpull = new Vec(0, 0, 0);
						
						double accel = this.custom.motoracceleration / this.arrows.size();
						
						double minabssidewayspull = 999;
//						double maxabssidewayspull = 0;
						
						boolean firstpull = true;
						boolean pullispositive = true;
						boolean pullissameway = true;
						
						// set all motors to maximum pull and precalculate some stuff for smart motor / smart double motor
						for (GrappleArrow arrow : this.arrows) {
							Vec arrowpos = Vec.positionVec(arrow);//this.getPositionVector();
							Vec anchor = arrow.segmenthandler.getclosest(arrowpos);
							Vec spherevec = playerpos.sub(anchor);
							Vec pull = spherevec.mult(-1);
							
							arrow.pull = accel;
							
							totalpull.addIp(pull.changeLen(accel));
							
							pull.changeLenIp(arrow.pull);

							// precalculate some stuff for smart double motor
							// For smart double motor: the motors should pull left and right equally
							// one side will be less able to pull to its side due to the angle
							// therefore the other side should slow down in order to match and have both sides pull left/right equally
							// the amount each should pull (the lesser of the two) is minabssidewayspull
							if (pull.dot(facing) > 0 || this.custom.pullbackwards) {
								if (this.custom.smartdoublemotor && this.arrows.size() > 1) {
									Vec facingxy = new Vec(facing.x, 0, facing.z);
									Vec facingside = facingxy.cross(new Vec(0, 1, 0)).normalize();
//									vec pullxy = new vec(pull.x, 0, pull.z);
									Vec sideways = pull.proj(facingside); // pullxy.removealong(facing);
									Vec currentsideways = motion.proj(facingside);
									sideways.addIp(currentsideways);
									double sidewayspull = sideways.dot(facingside); // facingxy.cross(sideways).y;
									
									if (Math.abs(sidewayspull) < minabssidewayspull) {
										minabssidewayspull = Math.abs(sidewayspull);
									}
/*									if (Math.abs(sidewayspull) > maxabssidewayspull) {
										maxabssidewayspull = Math.abs(sidewayspull);
									}*/
									
									if (firstpull) {
										firstpull = false;
										pullispositive = (sidewayspull >= 0);
									} else {
										if (pullispositive != (sidewayspull >= 0)) {
											pullissameway = false;
										}
									}
								}
								
							}
						}
						
						// Smart double motor - calculate the speed each motor should pull at
						if (this.custom.smartdoublemotor && this.arrows.size() > 1) {
							totalpull = new Vec(0, 0, 0);
							
							for (GrappleArrow arrow : this.arrows) {
								Vec arrowpos = Vec.positionVec(arrow);//this.getPositionVector();
								Vec anchor = arrow.segmenthandler.getclosest(arrowpos);
								Vec spherevec = playerpos.sub(anchor);
								Vec pull = spherevec.mult(-1);
								pull.changeLenIp(arrow.pull);
								
								if (pull.dot(facing) > 0 || this.custom.pullbackwards) {
									Vec facingxy = new Vec(facing.x, 0, facing.z);
									Vec facingside = facingxy.cross(new Vec(0, 1, 0)).normalize();
//									vec pullxy = new vec(pull.x, 0, pull.z);
									Vec sideways = pull.proj(facingside); // pullxy.removealong(facing);
									Vec currentsideways = motion.proj(facingside);
									sideways.addIp(currentsideways);
									double sidewayspull = sideways.dot(facingside); // facingxy.cross(sideways).y;
									
									if (pullissameway) {
										// only 1 rope pulls
										if (Math.abs(sidewayspull) > minabssidewayspull+0.05) {
											arrow.pull = 0;
										}
									} else {
										arrow.pull = arrow.pull * minabssidewayspull / Math.abs(sidewayspull);
									}
									totalpull.addIp(pull.changeLen(arrow.pull));
								} else {
									if (arrow.isdouble) {
										if (!this.custom.oneropepull) {
											dopull = false;
										}
									}
								}
							}
						}
						
						// smart motor - angle of motion = angle facing
						// match angle (the ratio of pulling upwards to pulling sideways)
						// between the motion (after pulling and gravity) vector and the facing vector
						// if double hooks, all hooks are scaled by the same amount (to prevent pulling to the left/right)
						double pullmult = 1;
						if (this.custom.smartmotor && totalpull.y > 0 && !(this.onGroundTimer > 0 || entity.isOnGround())) {
							Vec pullxzvector = new Vec(totalpull.x, 0, totalpull.z);
							double pullxz = pullxzvector.length();
							double motionxz = motion.proj(pullxzvector).dot(pullxzvector.normalize());
//							double facingxz = new vec(facing.x, 0, facing.z).length();//.proj(ropexz);
							double facingxz = facing.proj(pullxzvector).dot(pullxzvector.normalize());
//							double facinglmult = (totalpull.length()) / (facing.length());
//							double pulll = gravity.y / (facing.y * facinglmult - totalpull.y);
							
							
							
							// (newpully + gravityy) / newpullx = facingy / facingxz
							// newmotiony = totalpully * pullmult + motion.y
							// newpullxz = pullxz * pullmult + motionxz
							// (totalpully * pullmult + motion.y + gravityy) / (pullxz * pullmult + motionxz) = facingy / facingxz
							// (y          * m        + a        + g       ) / (x      * m        + b       ) = f       / w          (1-letter vars for wolframalpha)
							// m = (w (a + g) - b f)/(f x - w y)
							// pullmult = (facingxz * (motion.y + gravity.y) - motionxz * facing.y)/(facing.y * pullxz - facingxz * totalpull.y)
							
							pullmult = (facingxz * (motion.y + gravity.y) - motionxz * facing.y)/(facing.y * pullxz - facingxz * totalpull.y); // (gravity.y * facingxz) / (facing.y * pullxz - facingxz * totalpull.y);
							
							if ((facing.y * pullxz - facingxz * totalpull.y) == 0) {
								// division by zero
								pullmult = 9999;
							}
									
							double pulll = pullmult * totalpull.length();
							
							if (pulll > this.custom.motoracceleration) {
								pulll = this.custom.motoracceleration;
							}
							if (pulll < 0) {
								pulll = 0;
							}
							
							pullmult = pulll / totalpull.length();
						}
						
						// Prevent motor from moving too fast (motormaxspeed)
						if (this.motion.dot(totalpull) > 0) {
							if (this.motion.proj(totalpull).length() + totalpull.mult(pullmult).length() > this.custom.motormaxspeed) {
								pullmult = (this.custom.motormaxspeed - this.motion.proj(totalpull).length()) / totalpull.length();
								if (pullmult < 0) {
									pullmult = 0;
								}
							}
						}
						
						// sideways dampener
						if (this.custom.motordampener && totalpull.length() != 0) {
							motion = dampEnmotion(motion, totalpull);
						}
						
						// actually pull with the motor
						if (dopull) {
							for (GrappleArrow arrow : this.arrows) {
								Vec arrowpos = Vec.positionVec(arrow);//this.getPositionVector();
								Vec anchor = arrow.segmenthandler.getclosest(arrowpos);
								Vec spherevec = playerpos.sub(anchor);
								Vec pull = spherevec.mult(-1);
								pull.changeLenIp(arrow.pull * pullmult);
								
//								System.out.print(arrow.pull * pullmult);
//								System.out.print(" ");
								
								if (pull.dot(facing) > 0 || this.custom.pullbackwards) {
									if (arrow.pull > 0) {
										motion.addIp(pull);
									}
								}
							}
						}
						
						// if player is at the destination, slow down
						if (close && !(this.arrows.size() > 1)) {
							if (entity.collides() || entity.isOnGround()) {
								motion.multIp(0.6);
							}
						}
//						System.out.println();
					}
					
					// forcefield
					if (this.custom.repel) {
						Vec blockpush = checkRepel(playerpos, entity.world);
						blockpush.multIp(this.custom.repelforce * 0.5);
						blockpush = new Vec(blockpush.x*0.5, blockpush.y*2, blockpush.z*0.5);
						this.motion.addIp(blockpush);
					}
					
					// rocket
					if (this.custom.rocket) {
						this.motion.addIp(this.rocket(entity));
					}
					
					// WASD movement
					if (!doJump && !isClimbing) {
						applyPlayerMovement();
					}
					
					// jump
					if (doJump) {
						if (jumpSpeed <= 0) {
							jumpSpeed = 0;
						}
						if (jumpSpeed > 1) {
							jumpSpeed = 1;
						}
						this.doJump(entity, jumpSpeed);
						return;
					}
					
					// now to actually apply everything to the player
					Vec newmotion = motion.add(additionalmotion);
					
					if (Double.isNaN(newmotion.x) || Double.isNaN(newmotion.y) || Double.isNaN(newmotion.z)) {
						newmotion = new Vec(0, 0, 0);
						motion = new Vec(0, 0, 0);
						System.out.println("error: motion is NaN");
					}
					
					entity.setVelocity(newmotion.toVec3d());
					
//					if (entity instanceof PlayerEntityMP) {
						
//						((PlayerEntityMP) entity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
						
						/*
						counter++;
						if (counter > 100) {
							counter = 0;
							grapplemod.network.sendTo(new PlayerPosMessage(entity.getEntityId(), entity.posX, entity.posY, entity.posZ), (PlayerEntityMP) entity);
						}
						*/
//					}
					
//					entity.fallDistance = 0;
					
					this.updateServerPos();
				}
			}
		}
	}
	
	public void calcTaut(double dist, GrappleArrow arrow) {
		if (arrow != null) {
    		if (dist < arrow.r) {
    			double taut = 1 - ((arrow.r - dist) / 5);
    			if (taut < 0) {
    				taut = 0;
    			}
    			arrow.taut = taut;
    		} else {
    			arrow.taut = 1;
    		}
    	}
	}
	
//	boolean prevcollision = false;
//	vec prevcollisionpos = new vec(0,0,0);

	public void normalCollisions(boolean refreshmotion) {
		// stop if collided with object
		Vec pos = Vec.positionVec(this.entity);
		if (entity.horizontalCollision) {
//			if (refreshmotion || prevcollision) {
				if (entity.getVelocity().x == 0) {
					if (refreshmotion || this.tryStepUp(new Vec(this.motion.x, 0, 0))) {
						this.motion.x = 0;
					}
				}
				if (entity.getVelocity().z == 0) {
					if (refreshmotion || this.tryStepUp(new Vec(0, 0, this.motion.z))) {
						this.motion.z = 0;
					}
				}
//			}
		}
//		prevcollision = entity.collidedHorizontally;
//		if (prevcollision) {
//			if (entity.motionX == 0 || entity.motionZ == 0) {
//				prevcollisionpos = pos;
//			}
//		}
		if (entity.verticalCollision) {
			if (entity.isOnGround()) {
				if (refreshmotion && MinecraftClient.getInstance().options.keyJump.isPressed()) {
					this.motion.y = entity.getVelocity().y;
				} else {
					if (this.motion.y < 0) {
						this.motion.y = 0;
					}
				}
			} else {
				if (this.motion.y > 0) {
					if (entity.lastRenderY == entity.getPos().y) {
						this.motion.y = 0;
					}
				}
			}
		}
	}
	
	public boolean tryStepUp(Vec collisionmotion) {
		if (collisionmotion.length() == 0) {return false;}
		Vec moveoffset = collisionmotion.changeLen(0.05).add(0, entity.stepHeight+0.01, 0);
		if (this.world.getBlockCollisions(this.entity, this.entity.getBoundingBox().offset(moveoffset.x, moveoffset.y, moveoffset.z)).count() == 0) {
			if (!this.entity.isOnGround()) {
				entity.updatePosition(moveoffset.x, moveoffset.y, moveoffset.z);
				this.entity.setPos(entity.getPos().x, entity.getPos().y, entity.getPos().z);
				entity.prevX = entity.getPos().x;
				entity.prevY = entity.getPos().y;
				entity.prevZ = entity.getPos().z;
			}
			entity.horizontalCollision = false;
			return false;
		}
		return true;
	}
	
	boolean prevOnGround = false;

	public void normalGround(boolean refreshmotion) {
		if (entity.isOnGround()) {
			onGroundTimer = maxOnGroundTimer;
//			if (this.motion.y < 0) {
//				this.motion.y = 0;
//			}
		} else {
			if (this.onGroundTimer > 0) {
				onGroundTimer--;
			}
		}
		if (entity.isOnGround() || onGroundTimer > 0) {
			if (refreshmotion) {
				this.motion = Vec.motionVec(entity);
				if (MinecraftClient.getInstance().options.keyJump.isPressed()) {
					this.motion.y += 0.05;
				}
			}
		}
		prevOnGround = entity.isOnGround();
	}

	public double getJumpPower(Entity player, double jumppower) {
		double maxjump = 1;
		if (onGroundTimer > 0) { // on ground: jump normally
			onGroundTimer = 20;
			return 0;
		}
		if (player.isOnGround()) {
			jumppower = 0;
		}
		if (player.collides()) {
			jumppower = maxjump;
		}
		if (jumppower < 0) {
			jumppower = 0;
		}
		
		return jumppower;
	}
	
	public void doJump(Entity player, double jumppower) {
		if (jumppower > 0) {
			if (jumppower > player.getVelocity().y + jumppower) {
				player.setVelocity(player.getVelocity().x, jumppower, player.getVelocity().z);
			} else {
				player.setVelocity(player.getVelocity().x, player.getVelocity().y + jumppower, player.getVelocity().z);
			}
		}
		
		this.unattach();
		
		this.updateServerPos();
	}
	
	public double getJumpPower(Entity player, Vec spherevec, GrappleArrow arrow) {
		double maxjump = 1;
		Vec jump = new Vec(0, maxjump, 0);
		if (spherevec != null) {
			jump = jump.proj(spherevec);
		}
		double jumppower = jump.y;
		
		if (spherevec != null && spherevec.y > 0) {
			jumppower = 0;
		}
		if ((arrow != null) && arrow.r < 1 && (player.getPos().y < arrow.getPos().y)) {
			jumppower = maxjump;
		}
		
		return this.getJumpPower(player, jumppower);
	}

	public Vec dampEnmotion(Vec motion, Vec forward) {
		Vec newmotion = motion.proj(forward);
		double dampening = 0.05;
		return new Vec(newmotion.x*dampening + motion.x*(1-dampening), newmotion.y*dampening + motion.y*(1-dampening), newmotion.z*dampening + motion.z*(1-dampening));
	}
	
	public void updateServerPos() {
		new PlayerMovementMessage(this.entityId, this.entity.getPos().x, this.entity.getPos().y, this.entity.getPos().z, this.entity.getVelocity().x, this.entity.getVelocity().y, this.entity.getVelocity().z).send();
	}
	
	// Vector stuff:
	
	public void receiveGrappleDetach() {
		this.unattach();
	}

	public void receiveEnderLaunch(double x, double y, double z) {
		this.motion.addIp(x, y, z);
		this.entity.setVelocity(this.motion.toVec3d());
	}
	
	public void applyAirFriction() {
		double dragforce = 1 / 200F;
		if (this.entity.isInsideWaterOrBubbleColumn()) {
//			this.applyWaterFriction();
			dragforce = 1 / 4F;
		}
		
		double vel = this.motion.length();
		dragforce = vel*vel * dragforce;
		
		Vec airfric = new Vec(this.motion.x, this.motion.y, this.motion.z);
		airfric.changeLenIp(-dragforce);
		this.motion.addIp(airfric);
	}
	
/*	public void applyWaterFriction() {
		double vel = this.motion.length();
		double dragforce = vel*vel / 4;
		
		vec airfric = new vec(this.motion.x, this.motion.y, this.motion.z);
		airfric.changelen_ip(-dragforce);
		this.motion.add_ip(airfric);
	}*/
	
	public void applyPlayerMovement() {
		motion.addIp(this.playerMovement.changeLen(0.015 + motion.length() * 0.01).mult(this.playerMovementMult));//0.02 * playermovementmult));
	}

	public void addArrow(GrappleArrow arrow) {
		this.arrows.add(arrow);
		arrow.r = arrow.segmenthandler.getDist(Vec.positionVec(arrow), Vec.positionVec(entity).add(new Vec(0, entity.getEyeY(), 0)));
		this.arrowIds.add(arrow.getEntityId());
	}
	
    public double repelMaxPush = 0.3;//0.25;
	
    // repel stuff
    public Vec checkRepel(Vec p, World w) {
//    	long startTime = System.nanoTime();
    	
    	p = p.add(0.0, 0.75, 0.0);
    	Vec v = new Vec(0, 0, 0);
    	
    	/*
    	for (int x = (int)p.x - radius; x <= (int)p.x + radius; x++) {
        	for (int y = (int)p.y - radius; y <= (int)p.y + radius; y++) {
            	for (int z = (int)p.z - radius; z <= (int)p.z + radius; z++) {
			    	BlockPos pos = new BlockPos(x, y, z);
			    	if (pos != null) {
				    	if (hasblock(pos, w)) {
				    		vec blockvec = new vec(((double) x)+0.5, ((double) y)+0.5, ((double) z)+0.5);
				    		blockvec.sub_ip(p);
				    		blockvec.changelen_ip(-1 / Math.pow(blockvec.length(), 2));
				    		v.add_ip(blockvec);
				    	}
			    	}
            	}
	    	}
    	}
    	*/
    	
    	double t = (1.0 + Math.sqrt(5.0)) / 2.0;
    	
		BlockPos pos = new BlockPos(Math.floor(p.x), Math.floor(p.y), Math.floor(p.z));
		if (hasBlock(pos, w)) {
			v.addIp(0, 1, 0);
		} else {
	    	v.addIp(vecdist(p, new Vec(-1,  t,  0), w));
	    	v.addIp(vecdist(p, new Vec( 1,  t,  0), w));
	    	v.addIp(vecdist(p, new Vec(-1, -t,  0), w));
	    	v.addIp(vecdist(p, new Vec( 1, -t,  0), w));
	    	v.addIp(vecdist(p, new Vec( 0, -1,  t), w));
	    	v.addIp(vecdist(p, new Vec( 0,  1,  t), w));
	    	v.addIp(vecdist(p, new Vec( 0, -1, -t), w));
	    	v.addIp(vecdist(p, new Vec( 0,  1, -t), w));
	    	v.addIp(vecdist(p, new Vec( t,  0, -1), w));
	    	v.addIp(vecdist(p, new Vec( t,  0,  1), w));
	    	v.addIp(vecdist(p, new Vec(-t,  0, -1), w));
	    	v.addIp(vecdist(p, new Vec(-t,  0,  1), w));
		}
    	
    	if (v.length() > repelMaxPush) {
    		v.changeLenIp(repelMaxPush);
    	}
    	
//    	long endTime = System.nanoTime();

//    	long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
//    	System.out.println(duration);
    	
		return v;
	}
    
    public Vec vecdist(Vec p, Vec v, World w) {
    	for (double i = 0.5; i < 10; i += 0.5) {
    		Vec v2 = v.changeLen(i);
    		BlockPos pos = new BlockPos(Math.floor(p.x + v2.x), Math.floor(p.y + v2.y), Math.floor(p.z + v2.z));
    		if (hasBlock(pos, w)) {
    			Vec v3 = new Vec(pos.getX() + 0.5 - p.x, pos.getY() + 0.5 - p.y, pos.getZ() + 0.5 - p.z);
    			v3.changeLenIp(-1 / Math.pow(v3.length(), 2));
    			return v3;
    		}
    	}
    	
    	return new Vec(0, 0, 0);
    }
    
	public boolean hasBlock(BlockPos pos, World w) {
//    	if (!blockcache.containsKey(pos)) {
    		boolean isblock = false;
	    	BlockState blockstate = w.getBlockState(pos);
	    	if (!(blockstate.isAir())) {
	    		isblock = true;
	    	}
			
//			blockcache.put(pos, (Boolean) isblock);
	    	return isblock;
//    	} else {
//    		return blockcache.get(pos);
//    	}
	}

	public void receiveGrappleDetachHook(int hookid) {
		if (this.arrowIds.contains(hookid)) {
			this.arrowIds.remove(hookid);
		} else {
			System.out.println("Error: controller received hook detach, but hook id not in arrowIds");
		}
		
		GrappleArrow arrowToRemove = null;
		for (GrappleArrow arrow : this.arrows) {
			if (arrow.getEntityId() == hookid) {
				arrowToRemove = arrow;
				break;
			}
		}
		
		if (arrowToRemove != null) {
			this.arrows.remove(arrowToRemove);
		} else {
			System.out.println("Error: controller received hook detach, but arrow not in arrows");
		}
	}
	
	public Vec rocket(Entity entity) {
		if (ClientProxyClass.keyRocket.isPressed()) {
			double rocket_force = this.custom.rocketForce * 0.225 * GrappleMod.proxy.getRocketFunctioning();
        	double yaw = entity.yaw;
        	double pitch = -entity.pitch;
        	pitch += this.custom.rocket_vertical_angle;
        	Vec force = new Vec(0, 0, rocket_force);
        	force = force.rotatePitch(Math.toRadians(pitch));
        	force = force.rotateYaw(Math.toRadians(yaw));
        	
        	return force;
		}
		return new Vec(0,0,0);
	}
	
	boolean isOnWall = false;
	Vec wallDirection = null;
//	boolean wallonleft = true;
	
	public Vec getnearbywall(Vec tryfirst, Vec trysecond, double extra) {
		float entitywidth = this.entity.getWidth();
		
		for (Vec direction : new Vec[] {tryfirst, trysecond, tryfirst.mult(-1), trysecond.mult(-1)}) {
			HitResult raytraceresult = this.entity.world.rayTraceBlock(this.entity.getPos(), Vec.positionVec(this.entity).add(direction.changeLen(entitywidth/2 + extra)).toVec3d(), null, null, null);
			if (raytraceresult != null && raytraceresult.getType() == HitResult.Type.BLOCK) {
				return direction;
			}
		}
		
		return null;
	}
	
	public Vec getWallDirection() {
		Vec tryfirst = new Vec(0, 0, 0);
		Vec trysecond = new Vec(0, 0, 0);
		
		if (Math.abs(this.motion.x) > Math.abs(this.motion.z)) {
			tryfirst.x = (this.motion.x > 0) ? 1 : -1;
			trysecond.z = (this.motion.z > 0) ? 1 : -1;
		} else {
			tryfirst.z = (this.motion.z > 0) ? 1 : -1;
			trysecond.x = (this.motion.x > 0) ? 1 : -1;
		}
		
		return getnearbywall(tryfirst, trysecond, 0.05);
	}
//	
//	public vec getclosebywall() {
//		if (this.walldirection != null) {
//			vec tryfirst = this.walldirection;
//			vec trysecond = new vec(0,0,0);
//			if (tryfirst.x == 0) {
//				trysecond.x = 1;
//			} else {
//				trysecond.z = 1;
//			}
//			
//			vec walldir = getnearbywall(tryfirst, trysecond, 1.05);
//			if (walldir != null && walldir.dot(this.motion) >= 0) {
//				return walldir;
//			}
//		}
//		
//		return null;
//	}
	
	public Vec getCorner(int cornernum, Vec facing, Vec sideways) {
		Vec corner = new Vec(0,0,0);
		if (cornernum / 2 == 0) {
			corner.addIp(facing);
		} else {
			corner.addIp(facing.mult(-1));
		}
		if (cornernum % 2 == 0) {
			corner.addIp(sideways);
		} else {
			corner.addIp(sideways.mult(-1));
		}
		return corner;
	}
	
	public boolean wallNearby(double dist) {
//		double boxsize = 2;
		
//		// facing 2d
//		vec facing = new vec(this.entity.getLookVec());
//		facing.y = 0;
//		if (facing.length() <= 0.01) {
//			facing = new vec(1, 0, 0);
//		}
//		facing.changelen_ip(boxsize);
//		
//		vec sideways = facing.cross(new vec(0,1,0));
//		sideways.changelen_ip(boxsize);
		
		float entitywidth = this.entity.getWidth();
		Vec v1 = new Vec(entitywidth/2 + dist, 0, 0);
		Vec v2 = new Vec(0, 0, entitywidth/2 + dist);
		
		for (int i = 0; i < 4; i++) {
			Vec corner1 = getCorner(i, v1, v2);
			Vec corner2 = getCorner((i + 1) % 4, v1, v2);
			
			HitResult raytraceresult = this.entity.world.rayTraceBlock(Vec.positionVec(this.entity).add(corner1).toVec3d(), Vec.positionVec(this.entity).add(corner2).toVec3d(), null, null, null);
			if (raytraceresult != null && raytraceresult.getType() == HitResult.Type.BLOCK) {
				return true;
			}
		}
		
		return false;
	}
	
	public int ticksWallRunning = 0;

	public boolean wallrun() {
		if (isOnWall) {
			ticksWallRunning += 1;
		}
		
		if (ticksWallRunning < GrappleConfig.getconf().max_wallrun_time * 40) {
			if (!(this.entity.isSneaking())) {
				// continue wallrun
				if (isOnWall && !this.entity.isOnGround() && this.entity.horizontalCollision) {
					return true;
				}
				
				// start wallrun
				if (GrappleMod.proxy.isWallRunning(this.entity)) {
					isOnWall = true;
					return true;
				}
			}
			
			isOnWall = false;
		}
		
		if (ticksWallRunning > 0 && (this.entity.isOnGround() || (!this.entity.horizontalCollision && !wallNearby(0.2)))) {
			ticksWallRunning = 0;
		}
		
		return false;
	}
	
	public boolean applyWallrun() {
		boolean wallrun = this.wallrun();
		if (wallrun && !ClientProxyClass.keyJumpAndDetach.isPressed()) {

			Vec wallside = this.getWallDirection();
			if (wallside != null) {
				this.wallDirection = wallside;
			}
			
			if (this.wallDirection == null) {
				return false;
			}

			if (!playerJump) {
				motion.y = 0;
			}

			// drag
			double dragforce = GrappleConfig.getconf().wallrun_drag;
			
			double vel = this.motion.length();
//			dragforce = vel*vel * dragforce;
			
			if (dragforce > vel) {dragforce = vel;}
			
			Vec wallfric = new Vec(this.motion);
			if (wallside != null) {
				wallfric.removeAlong(wallside);
			}
			wallfric.changeLenIp(-dragforce);
			this.motion.addIp(wallfric);
			
			
		}
		
		// jump
		boolean isjumping = ClientProxyClass.keyJumpAndDetach.isPressed() && isOnWall;
		isjumping = isjumping && !playerJump; // only jump once when key is first pressed
		playerJump = ClientProxyClass.keyJumpAndDetach.isPressed() && isOnWall;
		if (isjumping) {
			Vec jump = new Vec(0, GrappleConfig.getconf().wall_jump_up, 0);
			if (wallDirection != null) {
				jump.addIp(wallDirection.mult(-GrappleConfig.getconf().wall_jump_side));
			}
			motion.addIp(jump);
			
			wallrun = false;
		}
		
		return wallrun;
	}
	
	public void wallrunPressAgainstWall() {
		// press against wall
		if (this.wallDirection != null) {
			motion.addIp(this.wallDirection.changeLen(0.05));
		}
	}

	public void doubleJump() {
		if (this.motion.y < 0) {
			this.motion.y = 0;
		}
		this.motion.y += GrappleConfig.getconf().doublejumpforce;
	}
	
	public void applySlidingFriction() {
		double dragforce = GrappleConfig.getconf().sliding_friction;
		
//		double vel = this.motion.length();
//		dragforce = vel*vel * dragforce;
		
		if (dragforce > this.motion.length()) {dragforce = this.motion.length(); };
		
		Vec airfric = new Vec(this.motion.x, this.motion.y, this.motion.z);
		airfric.changeLenIp(-dragforce);
		this.motion.addIp(airfric);
	}

	public void slidingJump() {
		this.motion.y = GrappleConfig.getconf().slidingjumpforce;
	}
}
