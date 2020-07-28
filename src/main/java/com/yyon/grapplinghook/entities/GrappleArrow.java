package com.yyon.grapplinghook.entities;

import java.util.HashMap;

import com.yyon.grapplinghook.GrappleConfig;
import com.yyon.grapplinghook.GrappleCustomization;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.Vec;
import com.yyon.grapplinghook.controllers.SegmentHandler;
import com.yyon.grapplinghook.network.GrappleAttachMessage;
import com.yyon.grapplinghook.network.GrappleAttachPosMessage;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

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

public class GrappleArrow extends ThrownEntity //implements IEntityAdditionalSpawnData
{
	public Entity shootingEntity = null;
	public int shootingEntityID;
	
	private boolean firstattach = false;
	public Vec thispos;
	
	public boolean righthand = true;
	
	public boolean attached = false;
	
	public double pull;
	
	public double taut = 1;
	
	public boolean ignoreFrustumCheck = true;
	
	public boolean isdouble = false;
	
//	public double maxlen = 20;
	public double r;
	
	public SegmentHandler segmenthandler = null;
	
	public GrappleCustomization customization = null;
	
/*	public vec debugpos = null;
	public vec debugpos2 = null;
	public vec debugpos3 = null;*/
	
	// magnet attract
	public Vec prevpos = null;
	public boolean foundblock = false;
	public boolean wasinair = false;
	public BlockPos magnetblock = null;
	
	public GrappleArrow(EntityType<? extends GrappleArrow> type, World worldIn) {
		super(type, worldIn);
		
		this.segmenthandler = new SegmentHandler(this.world, this, Vec.positionVec(this), Vec.positionVec(this));
		this.customization = new GrappleCustomization();
	}
	
	public GrappleArrow(World worldIn, LivingEntity shooter,
			boolean righthand, GrappleCustomization customization, boolean isdouble) {
		super(EntityType.class.cast(EntityType.get("grappleArrow").get()), shooter, worldIn);
		
		this.shootingEntity = shooter;
		this.shootingEntityID = this.shootingEntity.getEntityId();
		
		this.isdouble = isdouble;
		
		/*
		double x = 0.36;
		if (righthand) {x = -0.36;}
        vec pos = vec.positionvec(this);
        pos.add_ip(new vec(x, -0.175, 0.45).rotate_yaw(Math.toRadians(shooter.rotationYaw)));
        this.setPosition(pos.x, pos.y, pos.z);
        */
		
		Vec pos = Vec.positionVec(this.shootingEntity).add(new Vec(0, this.shootingEntity.getEyeY(), 0));

		this.segmenthandler = new SegmentHandler(this.world, this, new Vec(pos), new Vec(pos));

		this.customization = customization;
		this.r = customization.maxlen;
		
		this.righthand = righthand;
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (this.shootingEntityID == 0 || this.shootingEntity == null) { // removes ghost grappling hooks
			this.remove();
		}
		
		if (this.firstattach) {
			this.setVelocity(Vec3d.ZERO);
			this.firstattach = false;
			super.setPos(this.thispos.x, this.thispos.y, this.thispos.z);
		}
		
		if (this.world.isClient) {
			if (this.shootingEntity != null)  {
				if (!this.attached) {
					if (this.segmenthandler.hookpastbend(this.r)) {
						System.out.println("around bend");
						Vec farthest = this.segmenthandler.getfarthest();
						this.serverAttach(this.segmenthandler.getbendblock(1), farthest, null);
					}
					
					if (!this.customization.phaserope) {
						this.segmenthandler.update(Vec.positionVec(this), Vec.positionVec(this.shootingEntity).add(new Vec(0, this.shootingEntity.getEyeY(), 0)), this.r, true);
						
						if (this.customization.sticky) {
							if (this.segmenthandler.segments.size() > 2) {
								int bendnumber = this.segmenthandler.segments.size() - 2;
								Vec closest = this.segmenthandler.segments.get(bendnumber);
								this.serverAttach(this.segmenthandler.getbendblock(bendnumber), closest, null);
							}
						}
					} else {
						this.segmenthandler.updatepos(Vec.positionVec(this), Vec.positionVec(this.shootingEntity).add(new Vec(0, this.shootingEntity.getEyeY(), 0)), this.r);
					}
					
					Vec farthest = this.segmenthandler.getfarthest();
					double distToFarthest = this.segmenthandler.getDistToFarthest();
					
					Vec ropevec = Vec.positionVec(this).sub(farthest);
					double d = ropevec.length();
					
					if (this.customization.reelin && this.shootingEntity.isSneaking()) {
						double newdist = d + distToFarthest - 0.4;
						if (newdist > 1 && newdist <= this.customization.maxlen) {
							this.r = newdist;
						}
					}


					if (d + distToFarthest > this.r) {
						Vec motion = Vec.motionVec(this);
						
						if (motion.dot(ropevec) > 0) {
							motion = motion.removeAlong(ropevec);
						}
						
						this.setVelocityActually(motion.x, motion.y, motion.z);
						
						ropevec.changeLenIp(this.r - distToFarthest);
						Vec newpos = ropevec.add(farthest);
						
						this.setPosition(newpos.x, newpos.y, newpos.z);
					}
					
				}
			}
/*		} else {
			vec farthest = this.segmenthandler.getfarthest();
			double distToFarthest = this.segmenthandler.getDistToFarthest();
			
			vec ropevec = vec.positionvec(this).sub(farthest);
			double d = ropevec.length();
			
			this.taut = d + distToFarthest / maxlen;*/
		}
		
		// magnet attraction
		if (this.customization.attract && Vec.positionVec(this).sub(Vec.positionVec(this.shootingEntity)).length() > this.customization.attractradius) {
	    	if (this.shootingEntity == null) {return;}
	    	if (!this.foundblock) {
	    		if (this.world.isClient) {
	    			Vec playerpos = Vec.positionVec(this.shootingEntity);
	    			Vec pos = Vec.positionVec(this);
	    			if (magnetblock == null) {
		    			if (prevpos != null) {
			    			HashMap<BlockPos, Boolean> checkedset = new HashMap<>();
			    			Vec vector = pos.sub(prevpos);
			    			Vec normvector = vector.normalize();
			    			for (int i = 0; i < vector.length(); i++) {
			    				double dist = prevpos.sub(playerpos).length();
			    				int radius = (int) dist / 4;
			    				BlockPos found = this.check(prevpos, checkedset);
			    				if (found != null) {
//			    					if (wasinair) {
							    		Vec distvec = new Vec(found.getX(), found.getY(), found.getZ());
							    		distvec.subIp(prevpos);
							    		if (distvec.length() < radius) {
					    					this.updatePosition(prevpos.x, prevpos.y, prevpos.z);
					    					pos = prevpos;
					    					
					    					magnetblock = found;
					    					
					    					break;
							    		}
//			    					}
			    				} else {
			    					wasinair = true;
			    				}
			    				
			    				prevpos.addIp(normvector);
			    			}
		    			}
	    			}
	    			
	    			if (magnetblock != null) {
				    	BlockState blockstate = this.world.getBlockState(magnetblock);
				    	VoxelShape BB = blockstate.getCollisionShape(this.world, magnetblock);

						Vec blockvec = new Vec(magnetblock.getX() + (BB.getMax(Axis.X) + BB.getMax(Axis.X)) / 2, magnetblock.getY() + (BB.getMax(Axis.Y) + BB.getMax(Axis.Y)) / 2, magnetblock.getZ() + (BB.getMax(Axis.Z) + BB.getMin(Axis.Z)) / 2);
						Vec newvel = blockvec.sub(pos);
						
						double l = newvel.length();
						
						newvel.changeLen(this.getVelocity_());
						
						this.setVelocity(newvel.toVec3d());
						
						if (l < 0.2) {
							this.serverAttach(magnetblock, blockvec, Direction.UP);
						}
	    			}
	    			
	    			prevpos = pos;
	    		}
	    	}
		}
	}
	
	public void setVelocityActually(double x, double y, double z) {
        this.setVelocity(x, y, z);

        if (this.prevPitch == 0.0F && this.prevYaw == 0.0F)
        {
            float f = MathHelper.sqrt(x * x + z * z);
            this.yaw = (float)(MathHelper.atan2(x, z) * (180D / Math.PI));
            this.pitch = (float)(MathHelper.atan2(y, f) * (180D / Math.PI));
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
        }
	}
	
	@Override
	@Environment(value=EnvType.CLIENT)
	public boolean isInRange(Entity entity, double distance) {
		return true;
	}

	public final int RenderBoundingBoxSize = 999;

	@Override
	@Environment(value=EnvType.CLIENT)
    public Box getVisibilityBoundingBox() {
	     return new Box(-RenderBoundingBoxSize, -RenderBoundingBoxSize, -RenderBoundingBoxSize, 
				RenderBoundingBoxSize, RenderBoundingBoxSize, RenderBoundingBoxSize);
	}

/*	public boolean toofaraway() {
    	if (this.shootingEntity == null) {return false;}
		if (!this.world.isRemote) {
			if (!grapplemod.attached.contains(this.shootingEntityID)) {
				if (grapplemod.grapplingLength != 0) {
					double d = vec.positionvec(this).sub(vec.positionvec(this.shootingEntity)).length();
					if (d > grapplemod.grapplingLength) {
						return true;
					}
				}
			}
		}
		return false;
	}*/

	public void setPosition(double x, double y, double z) {
		if (this.thispos != null) {
			x = this.thispos.x;
			y = this.thispos.y;
			z = this.thispos.z;
		}
		super.setPos(x, y, z);
	}
	
	@Override
    public void writeCustomDataToTag(CompoundTag data) {
	    data.putInt("entity", this.shootingEntity != null ? this.shootingEntity.getEntityId() : 0);
	    data.putBoolean("righthand", this.righthand);
	    data.putBoolean("isdouble", this.isdouble);
	    if (this.customization == null) {
	    	System.out.println("error: customization null");
	    }
	    data.put("customization", this.customization.toCompoundTag());
    }
	
    @Override
    public void readCustomDataFromTag(CompoundTag data)
    {
    	this.shootingEntityID = data.getInt("entity");
	    this.shootingEntity = this.world.getEntityById(this.shootingEntityID);
	    this.righthand = data.getBoolean("righthand");
	    this.isdouble = data.getBoolean("isdouble");
	    this.customization = new GrappleCustomization();
	    this.customization.loadNBT(data.getCompound("customization"));
    }
	
	public void remove() {
		this.destroy();
	}
	
	@Override
	public String toString() {
		return super.toString() + String.valueOf(System.identityHashCode(this)) + "]";
	}

	@Override
    protected void onEntityHit(EntityHitResult movingObjectPosition) {
		if (this.world.isClient()) {
			if (this.shootingEntityID != 0) {
				if (movingObjectPosition == null) {
					return;
				}
				
				if (GrappleConfig.getconf().hookaffectsentities) {
					// hit entity
					Entity hitEntity = movingObjectPosition.getEntity();
					if (hitEntity == this.shootingEntity) {
						return;
					}
					
					Vec playerPos = Vec.positionVec(this.shootingEntity);
					Vec entityPos = Vec.positionVec(hitEntity);
					Vec yank = playerPos.sub(entityPos).mult(0.4);
					hitEntity.addVelocity(yank.x, Math.min(yank.y, 2), yank.z);
					
					this.removeServer();
					return;
				}
			}
		}
	}

	@Override
    protected void onBlockHit(BlockHitResult movingObjectPosition) {
        if (this.world.isClient()) {
            if (this.shootingEntityID != 0) {
                if (movingObjectPosition == null) {
                    return;
                }

                BlockPos blockpos = movingObjectPosition.getBlockPos();

                this.serverAttach(blockpos, new Vec(movingObjectPosition.getPos()), movingObjectPosition.getSide());
            }
        }
    }

	public void serverAttach(BlockPos blockPos, Vec pos, Direction sideHit) {
		if (this.attached) {
			return;
		}
		this.attached = true;

		if (blockPos != null) {
			if (!GrappleMod.anyBlocks) {
				Block block = this.world.getBlockState(blockPos).getBlock();

				if ((!GrappleMod.removeBlocks && !GrappleMod.grapplingblocks.contains(block))
						|| (GrappleMod.removeBlocks && GrappleMod.grapplingblocks.contains(block))) {
					this.removeServer();
					return;
				}
			}
		}
		
		Vec vec3 = Vec.positionVec(this);
		vec3.addIp(Vec.motionVec(this));
		if (pos != null) {
            vec3 = pos;
            
            this.updatePosition(vec3.x, vec3.y, vec3.z);
		}
		
		//west -x
		//north -z
		if (sideHit == Direction.DOWN) {
			this.setPos(getPos().x, getPos().y - 0.3, getPos().z);
		} else if (sideHit == Direction.WEST) {
			this.setPos(getPos().x - 0.05, getPos().y, getPos().z);
		} else if (sideHit == Direction.NORTH) {
			this.setPos(getPos().x, getPos().y, getPos().z - 0.05);
		} else if (sideHit == Direction.SOUTH) {
			this.setPos(getPos().x, getPos().y, getPos().z + 0.05);
		} else if (sideHit == Direction.EAST) {
			this.setPos(getPos().x + 0.05, getPos().y, getPos().z);
		} else if (sideHit == Direction.UP) {
			this.setPos(getPos().x, getPos().y + 0.05, getPos().z);
		}
		
        this.setVelocity(Vec3d.ZERO);
        
        this.thispos = Vec.positionVec(this);
		this.firstattach = true;
		GrappleMod.attached.add(this.shootingEntityID);
		
		GrappleMod.sendToCorrectClient(new GrappleAttachMessage(this.getEntityId(), this.getPos().x, this.getPos().y, this.getPos().z, this.getControlId(), this.shootingEntityID, blockPos, this.segmenthandler.segments, this.segmenthandler.segmenttopsides, this.segmenthandler.segmentbottomsides, this.customization), this.shootingEntityID, this.world);
		if (this.shootingEntity instanceof PlayerEntity) { // fixes strange bug in LAN
		    PlayerEntity sender = (PlayerEntity) this.shootingEntity;
			DimensionType dimension = sender.world.getDimension();
			MinecraftServer minecraftServer = sender.getServer();
			for (PlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
				GrappleAttachPosMessage msg = new GrappleAttachPosMessage(this.getEntityId(), this.getPos().x, this.getPos().y, this.getPos().z);   // must generate a fresh message for every player!
				if (dimension == player.world.getDimension()) {
					GrappleMod.sendToCorrectClient(msg, player.getEntityId(), player.world);
				}
			}
		}
	}
	
	public void clientAttach(double x, double y, double z) {
		this.setAttachPos(x, y, z);
		
		if (this.shootingEntity instanceof PlayerEntity) {
			GrappleMod.proxy.resetlaunchertime(this.shootingEntityID);
		}
	}
	
	@Override
    protected float getGravity()
    {
        return (float) this.customization.hookGravity * 0.1F;
    }
	
    public float getVelocity_()
    {
        return (float) this.customization.throwSpeed;
    }

    public void removeServer() {
		this.remove();
		this.shootingEntityID = 0;

	}
	
    private int getControlId() {
		return GrappleMod.GRAPPLEID;
	}

    public void setAttachPos(double x, double y, double z) {
		this.updatePosition(x, y, z);

		this.setVelocity(Vec3d.ZERO);
		this.firstattach = true;
		this.attached = true;
        this.thispos = new Vec(x, y, z);
	}
	
	// used for magnet attraction
    private BlockPos check(Vec p, HashMap<BlockPos, Boolean> checkedSet) {
    	int radius = (int) Math.floor(this.customization.attractradius);
    	BlockPos closestPos = null;
    	double closestdist = 0;
    	for (int x = (int)p.x - radius; x <= (int)p.x + radius; x++) {
        	for (int y = (int)p.y - radius; y <= (int)p.y + radius; y++) {
            	for (int z = (int)p.z - radius; z <= (int)p.z + radius; z++) {
			    	BlockPos pos = new BlockPos(x, y, z);
			    	if (pos != null) {
				    	if (hasblock(pos, checkedSet)) {
				    		Vec distvec = new Vec(pos.getX(), pos.getY(), pos.getZ());
				    		distvec.subIp(p);
				    		double dist = distvec.length();
				    		if (closestPos == null || dist < closestdist) {
				    			closestPos = pos;
				    			closestdist = dist;
				    		}
				    	}
			    	}
            	}
	    	}
    	}
		return closestPos;
	}

	// used for magnet attraction
    private boolean hasblock(BlockPos pos, HashMap<BlockPos, Boolean> checkedset) {
    	if (!checkedset.containsKey(pos)) {
    		boolean isBlock = false;
	    	BlockState blockState = this.world.getBlockState(pos);
	    	Block b = blockState.getBlock();
			if (!GrappleMod.anyBlocks && ((!GrappleMod.removeBlocks && !GrappleMod.grapplingblocks.contains(b))
						|| (GrappleMod.removeBlocks && GrappleMod.grapplingblocks.contains(b)))) {
			} else {
		    	if (!(blockState.isAir())) {
		    	    VoxelShape shape = blockState.getCollisionShape(this.world, pos);
			    	if (shape != null) {
			    		isBlock = true;
			    	}
		    	}
			}
			
	    	checkedset.put(pos, isBlock);
	    	return isBlock;
    	} else {
    		return checkedset.get(pos);
    	}
	}

    @Override
    protected void initDataTracker() {
        // TODO Auto-generated method stub
        
    }
}
