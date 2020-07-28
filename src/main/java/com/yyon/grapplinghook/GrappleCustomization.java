package com.yyon.grapplinghook;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

public class GrappleCustomization {
	public static final String[] booleanoptions = new String[] {"phaserope", "motor", "motorwhencrouching", "motorwhennotcrouching", "smartmotor", "enderstaff", "repel", "attract", "doublehook", "smartdoublemotor", "motordampener", "reelin", "pullbackwards", "oneropepull", "climbkey", "sticky", "detachonkeyrelease", "rocket"};
	public static final String[] doubleoptions = new String[] {"maxlen", "hookgravity", "throwspeed", "motormaxspeed", "motoracceleration", "playermovementmult", "repelforce", "attractradius", "angle", "sneakingangle", "verticalthrowangle", "sneakingverticalthrowangle", "rocket_force", "rocket_active_time", "rocket_refuel_ratio", "rocket_vertical_angle"};
	
	// rope
	public double maxlen = GrappleConfig.getconf().default_maxlen;
	public boolean phaserope = GrappleConfig.getconf().default_phaserope;
	public boolean climbkey = GrappleConfig.getconf().default_climbkey;
	public boolean sticky = GrappleConfig.getconf().default_sticky;

	// hook thrower
	public double hookGravity = GrappleConfig.getconf().default_hookgravity;
	public double throwSpeed = GrappleConfig.getconf().default_throwspeed;
	public boolean reelin = GrappleConfig.getconf().default_reelin;
	public double verticalThrowAngle = GrappleConfig.getconf().default_verticalthrowangle;
	public double sneakingVerticalThrowAngle = GrappleConfig.getconf().default_sneakingverticalthrowangle;
	public boolean detachonkeyrelease = GrappleConfig.getconf().default_detachonkeyrelease;

	// motor
	public boolean motor = GrappleConfig.getconf().default_motor;
	public double motormaxspeed = GrappleConfig.getconf().default_motormaxspeed;
	public double motoracceleration = GrappleConfig.getconf().default_motoracceleration;
	public boolean motorwhencrouching = GrappleConfig.getconf().default_motorwhencrouching;
	public boolean motorwhennotcrouching = GrappleConfig.getconf().default_motorwhennotcrouching;
	public boolean smartmotor = GrappleConfig.getconf().default_smartmotor;
	public boolean motordampener = GrappleConfig.getconf().default_motordampener;
	public boolean pullbackwards = GrappleConfig.getconf().default_pullbackwards;
	
	// swing speed
	public double playermovementmult = GrappleConfig.getconf().default_playermovementmult;

	// ender staff
	public boolean enderstaff = GrappleConfig.getconf().default_enderstaff;

	// forcefield
	public boolean repel = GrappleConfig.getconf().default_repel;
	public double repelforce = GrappleConfig.getconf().default_repelforce;
	
	// hook magnet
	public boolean attract = GrappleConfig.getconf().default_attract;
	public double attractradius = GrappleConfig.getconf().default_attractradius;
	
	// double hook
	public boolean doublehook = GrappleConfig.getconf().default_doublehook;
	public boolean smartdoublemotor = GrappleConfig.getconf().default_smartdoublemotor;
	public double angle = GrappleConfig.getconf().default_angle;
	public double sneakingAngle = GrappleConfig.getconf().default_sneakingangle;
	public boolean oneropepull = GrappleConfig.getconf().default_oneropepull;
	
	// rocket
	public boolean rocket = GrappleConfig.getconf().default_rocketenabled;
	public double rocketForce = GrappleConfig.getconf().default_rocket_force;
	public double rocketActiveTime = GrappleConfig.getconf().default_rocket_active_time;
	public double rocketRefuelRatio = GrappleConfig.getconf().default_rocket_refuel_ratio;
	public double rocket_vertical_angle = GrappleConfig.getconf().default_rocket_vertical_angle;
	
	public GrappleCustomization() {
		
	}
	
	public CompoundTag toCompoundTag() {
	    CompoundTag compound = new CompoundTag();
		for (String option : booleanoptions) {
			compound.putBoolean(option, this.getBoolean(option));
		}
		for (String option : doubleoptions) {
			compound.putDouble(option, this.getDouble(option));
		}
		return compound;
	}
	
	public void loadNBT(CompoundTag compound) {
		for (String option : booleanoptions) {
			if (compound.contains(option)) {
				this.setBoolean(option, compound.getBoolean(option));
			}
		}
		for (String option : doubleoptions) {
			if (compound.contains(option)) {
				this.setDouble(option, compound.getDouble(option));
			}
		}
	}
	
	public void setBoolean(String option, boolean bool) {
		if (option.equals("phaserope")) {this.phaserope = bool;}
		else if (option.equals("motor")) {this.motor = bool;}
		else if (option.equals("motorwhencrouching")) {this.motorwhencrouching = bool;}
		else if (option.equals("motorwhennotcrouching")) {this.motorwhennotcrouching = bool;}
		else if (option.equals("smartmotor")) {this.smartmotor = bool;}
		else if (option.equals("enderstaff")) {this.enderstaff = bool;}
		else if (option.equals("repel")) {this.repel = bool;}
		else if (option.equals("attract")) {this.attract = bool;}
		else if (option.equals("doublehook")) {this.doublehook = bool;}
		else if (option.equals("smartdoublemotor")) {this.smartdoublemotor = bool;}
		else if (option.equals("motordampener")) {this.motordampener = bool;}
		else if (option.equals("reelin")) {this.reelin = bool;}
		else if (option.equals("pullbackwards")) {this.pullbackwards = bool;}
		else if (option.equals("oneropepull")) {this.oneropepull = bool;}
		else if (option.equals("climbkey")) {this.climbkey = bool;}
		else if (option.equals("sticky")) {this.sticky = bool;}
		else if (option.equals("detachonkeyrelease")) {this.detachonkeyrelease = bool;}
		else if (option.equals("rocket")) {this.rocket = bool;}
		else {System.out.println("Option doesn't exist: " + option);}
	}
	
	public boolean getBoolean(String option) {
		if (option.equals("phaserope")) {return this.phaserope;}
		else if (option.equals("motor")) {return this.motor;}
		else if (option.equals("motorwhencrouching")) {return this.motorwhencrouching;}
		else if (option.equals("motorwhennotcrouching")) {return this.motorwhennotcrouching;}
		else if (option.equals("smartmotor")) {return this.smartmotor;}
		else if (option.equals("enderstaff")) {return this.enderstaff;}
		else if (option.equals("repel")) {return this.repel;}
		else if (option.equals("attract")) {return this.attract;}
		else if (option.equals("doublehook")) {return this.doublehook;}
		else if (option.equals("smartdoublemotor")) {return this.smartdoublemotor;}
		else if (option.equals("motordampener")) {return this.motordampener;}
		else if (option.equals("reelin")) {return this.reelin;}
		else if (option.equals("pullbackwards")) {return this.pullbackwards;}
		else if (option.equals("oneropepull")) {return this.oneropepull;}
		else if (option.equals("climbkey")) {return this.climbkey;}
		else if (option.equals("sticky")) {return this.sticky;}
		else if (option.equals("detachonkeyrelease")) {return this.detachonkeyrelease;}
		else if (option.equals("rocket")) {return this.rocket;}
		System.out.println("Option doesn't exist: " + option);
		return false;
	}
	
	public void setDouble(String option, double d) {
		if (option.equals("maxlen")) {this.maxlen = d;}
		else if (option.equals("hookgravity")) {this.hookGravity = d;}
		else if (option.equals("throwspeed")) {this.throwSpeed = d;}
		else if (option.equals("motormaxspeed")) {this.motormaxspeed = d;}
		else if (option.equals("motoracceleration")) {this.motoracceleration = d;}
		else if (option.equals("playermovementmult")) {this.playermovementmult = d;}
		else if (option.equals("repelforce")) {this.repelforce = d;}
		else if (option.equals("attractradius")) {this.attractradius = d;}
		else if (option.equals("angle")) {this.angle = d;}
		else if (option.equals("sneakingangle")) {this.sneakingAngle = d;}
		else if (option.equals("verticalthrowangle")) {this.verticalThrowAngle = d;}
		else if (option.equals("sneakingverticalthrowangle")) {this.sneakingVerticalThrowAngle = d;}
		else if (option.equals("rocket_force")) {this.rocketForce = d;}
		else if (option.equals("rocket_active_time")) {this.rocketActiveTime = d;}
		else if (option.equals("rocket_refuel_ratio")) {this.rocketRefuelRatio = d;}
		else if (option.equals("rocket_vertical_angle")) {this.rocket_vertical_angle = d;}
		else {System.out.println("Option doesn't exist: " + option);}
	}
	
	public double getDouble(String option) {
		if (option.equals("maxlen")) {return maxlen;}
		else if (option.equals("hookgravity")) {return hookGravity;}
		else if (option.equals("throwspeed")) {return throwSpeed;}
		else if (option.equals("motormaxspeed")) {return motormaxspeed;}
		else if (option.equals("motoracceleration")) {return motoracceleration;}
		else if (option.equals("playermovementmult")) {return playermovementmult;}
		else if (option.equals("repelforce")) {return repelforce;}
		else if (option.equals("attractradius")) {return attractradius;}
		else if (option.equals("angle")) {return angle;}
		else if (option.equals("sneakingangle")) {return sneakingAngle;}
		else if (option.equals("verticalthrowangle")) {return verticalThrowAngle;}
		else if (option.equals("sneakingverticalthrowangle")) {return sneakingVerticalThrowAngle;}
		else if (option.equals("rocket_force")) {return this.rocketForce;}
		else if (option.equals("rocket_active_time")) {return rocketActiveTime;}
		else if (option.equals("rocket_refuel_ratio")) {return rocketRefuelRatio;}
		else if (option.equals("rocket_vertical_angle")) {return rocket_vertical_angle;}
		System.out.println("Option doesn't exist: " + option);
		return 0;
	}
	
	public void writeToBuf(ByteBuf buf) {
		for (String option : booleanoptions) {
			buf.writeBoolean(this.getBoolean(option));
		}
		for (String option : doubleoptions) {
			buf.writeDouble(this.getDouble(option));
		}
	}
	
	public void readFromBuf(ByteBuf buf) {
		for (String option : booleanoptions) {
			this.setBoolean(option, buf.readBoolean());
		}
		for (String option : doubleoptions) {
			this.setDouble(option, buf.readDouble());
		}
	}

	public String getName(String option) {
		return "grapplecustomization." + option + ".name";
	}
	
	public String getDescription(String option) {
		return "grapplecustomization." + option + ".desc";
	}
	
	public boolean isoptionvalid(String option) {
		if (option == "motormaxspeed" || option == "motoracceleration" || option == "motorwhencrouching" || option == "motorwhennotcrouching" || option == "smartmotor" || option == "motordampener" || option == "pullbackwards") {
			return this.motor;
		}
		
		if (option == "sticky") {
			return !this.phaserope;
		}
		
		else if (option == "sneakingangle") {
			return this.doublehook && !this.reelin;
		}
		
		else if (option == "repelforce") {
			return this.repel;
		}
		
		else if (option == "attractradius") {
			return this.attract;
		}
		
		else if (option == "angle") {
			return this.doublehook;
		}
		
		else if (option == "smartdoublemotor" || option == "oneropepull") {
			return this.doublehook && this.motor;
		}
		
		else if (option == "rocket_active_time" || option == "rocket_refuel_ratio" || option == "rocket_force" || option == "rocket_vertical_angle") {
			return this.rocket;
		}
		
		return true;
	}
	
	public double getMax(String option, int upgrade) {
		if (option.equals("maxlen")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_maxlen : GrappleConfig.getconf().max_maxlen;}
		else if (option.equals("hookgravity")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_hookgravity : GrappleConfig.getconf().max_hookgravity;}
		else if (option.equals("throwspeed")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_throwspeed : GrappleConfig.getconf().max_throwspeed;}
		else if (option.equals("motormaxspeed")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_motormaxspeed : GrappleConfig.getconf().max_motormaxspeed;}
		else if (option.equals("motoracceleration")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_motoracceleration : GrappleConfig.getconf().max_motoracceleration;}
		else if (option.equals("playermovementmult")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_playermovementmult : GrappleConfig.getconf().max_playermovementmult;}
		else if (option.equals("repelforce")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_repelforce : GrappleConfig.getconf().max_repelforce;}
		else if (option.equals("attractradius")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_attractradius : GrappleConfig.getconf().max_attractradius;}
		else if (option.equals("angle")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_angle : GrappleConfig.getconf().max_angle;}
		else if (option.equals("sneakingangle")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_sneakingangle : GrappleConfig.getconf().max_sneakingangle;}
		else if (option.equals("verticalthrowangle")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_verticalthrowangle : GrappleConfig.getconf().max_verticalthrowangle;}
		else if (option.equals("sneakingverticalthrowangle")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_sneakingverticalthrowangle : GrappleConfig.getconf().max_sneakingverticalthrowangle;}
		else if (option.equals("rocket_force")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_rocket_force: GrappleConfig.getconf().max_rocket_force;}
		else if (option.equals("rocket_active_time")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_rocket_active_time : GrappleConfig.getconf().max_rocket_active_time;}
		else if (option.equals("rocket_refuel_ratio")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_rocket_refuel_ratio : GrappleConfig.getconf().max_rocket_refuel_ratio;}
		else if (option.equals("rocket_vertical_angle")) {return upgrade == 1 ? GrappleConfig.getconf().max_upgrade_rocket_vertical_angle : GrappleConfig.getconf().max_rocket_vertical_angle;}
		System.out.println("Option doesn't exist: " + option);
		return 0;
	}
	
	public double getMin(String option, int upgrade) {
		if (option.equals("hookgravity")) {return upgrade == 1 ? GrappleConfig.getconf().min_upgrade_hookgravity : GrappleConfig.getconf().min_hookgravity;}
		if (option.equals("rocket_refuel_ratio")) {return upgrade == 1 ? GrappleConfig.getconf().min_upgrade_rocket_refuel_ratio : GrappleConfig.getconf().min_rocket_refuel_ratio;}
		
		return 0;
	}
	
	public int optionEnabled(String option) {
		if (option.equals("maxlen")) {return GrappleConfig.getconf().enable_maxlen;}
		else if (option.equals("phaserope")) {return GrappleConfig.getconf().enable_phaserope;}
		else if (option.equals("hookgravity")) {return GrappleConfig.getconf().enable_hookgravity;}
		else if (option.equals("throwspeed")) {return GrappleConfig.getconf().enable_throwspeed;}
		else if (option.equals("reelin")) {return GrappleConfig.getconf().enable_reelin;}
		else if (option.equals("verticalthrowangle")) {return GrappleConfig.getconf().enable_verticalthrowangle;}
		else if (option.equals("motor")) {return GrappleConfig.getconf().enable_motor;}
		else if (option.equals("motormaxspeed")) {return GrappleConfig.getconf().enable_motormaxspeed;}
		else if (option.equals("motoracceleration")) {return GrappleConfig.getconf().enable_motoracceleration;}
		else if (option.equals("motorwhencrouching")) {return GrappleConfig.getconf().enable_motorwhencrouching;}
		else if (option.equals("motorwhennotcrouching")) {return GrappleConfig.getconf().enable_motorwhennotcrouching;}
		else if (option.equals("smartmotor")) {return GrappleConfig.getconf().enable_smartmotor;}
		else if (option.equals("motordampener")) {return GrappleConfig.getconf().enable_motordampener;}
		else if (option.equals("pullbackwards")) {return GrappleConfig.getconf().enable_pullbackwards;}
		else if (option.equals("playermovementmult")) {return GrappleConfig.getconf().enable_playermovementmult;}
		else if (option.equals("enderstaff")) {return GrappleConfig.getconf().enable_enderstaff;}
		else if (option.equals("repel")) {return GrappleConfig.getconf().enable_repel;}
		else if (option.equals("repelforce")) {return GrappleConfig.getconf().enable_repelforce;}
		else if (option.equals("attract")) {return GrappleConfig.getconf().enable_attract;}
		else if (option.equals("attractradius")) {return GrappleConfig.getconf().enable_attractradius;}
		else if (option.equals("doublehook")) {return GrappleConfig.getconf().enable_doublehook;}
		else if (option.equals("smartdoublemotor")) {return GrappleConfig.getconf().enable_smartdoublemotor;}
		else if (option.equals("angle")) {return GrappleConfig.getconf().enable_angle;}
		else if (option.equals("sneakingangle")) {return GrappleConfig.getconf().enable_sneakingangle;}
		else if (option.equals("oneropepull")) {return GrappleConfig.getconf().enable_oneropepull;}
		else if (option.equals("sneakingverticalthrowangle")) {return GrappleConfig.getconf().enable_sneakingverticalthrowangle;}
		else if (option.equals("climbkey")) {return GrappleConfig.getconf().enable_climbkey;}
		else if (option.equals("sticky")) {return GrappleConfig.getconf().enable_sticky;}
		else if (option.equals("detachonkeyrelease")) {return GrappleConfig.getconf().enable_detachonkeyrelease;}
		else if (option.equals("rocket")) {return GrappleConfig.getconf().enable_rocket;}
		else if (option.equals("rocket_force")) {return GrappleConfig.getconf().enable_rocket_force;}
		else if (option.equals("rocket_active_time")) {return GrappleConfig.getconf().enable_rocket_active_time;}
		else if (option.equals("rocket_refuel_ratio")) {return GrappleConfig.getconf().enable_rocket_refuel_ratio;}
		else if (option.equals("rocket_vertical_angle")) {return GrappleConfig.getconf().enable_rocket_vertical_angle;}
		System.out.println("Unknown option");
		return 0;
	}
}
