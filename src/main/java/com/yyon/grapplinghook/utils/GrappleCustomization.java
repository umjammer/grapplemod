package com.yyon.grapplinghook.utils;

import java.util.Objects;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.TranslatableText;

import static com.yyon.grapplinghook.client.ClientSetup.clientProxy;


public class GrappleCustomization {
	public static final String[] booleanoptions = {"phaserope", "motor", "motorwhencrouching", "motorwhennotcrouching", "smartmotor", "enderstaff", "repel", "attract", "doublehook", "smartdoublemotor", "motordampener", "reelin", "pullbackwards", "oneropepull", "sticky", "detachonkeyrelease", "rocket"};
	public static final String[] doubleoptions = {"maxlen", "hookgravity", "throwspeed", "motormaxspeed", "motoracceleration", "playermovementmult", "repelforce", "attractradius", "angle", "sneakingangle", "verticalthrowangle", "sneakingverticalthrowangle", "rocket_force", "rocket_active_time", "rocket_refuel_ratio", "rocket_vertical_angle"};

	// rope
	public double maxlen;
	public boolean phaserope;
	public boolean sticky;

	// hook thrower
	public double hookgravity;
	public double throwspeed;
	public boolean reelin;
	public double verticalthrowangle;
	public double sneakingverticalthrowangle;
	public boolean detachonkeyrelease;

	// motor
	public boolean motor;
	public double motormaxspeed;
	public double motoracceleration;
	public boolean motorwhencrouching;
	public boolean motorwhennotcrouching;
	public boolean smartmotor;
	public boolean motordampener;
	public boolean pullbackwards;

	// swing speed
	public double playermovementmult;

	// ender staff
	public boolean enderstaff;

	// forcefield
	public boolean repel;
	public double repelforce;

	// hook magnet
	public boolean attract;
	public double attractradius;

	// double hook
	public boolean doublehook;
	public boolean smartdoublemotor;
	public double angle;
	public double sneakingangle;
	public boolean oneropepull;

	// rocket
	public boolean rocket;
	public double rocket_force;
	public double rocket_active_time;
	public double rocket_refuel_ratio;
	public double rocket_vertical_angle;

	public enum upgradeCategories {
		ROPE ("rope"), 
		THROW ("throw"), 
		MOTOR ("motor"), 
		SWING ("swing"), 
		STAFF ("staff"), 
		FORCEFIELD ("forcefield"), 
		MAGNET ("magnet"), 
		DOUBLE ("double"),
		LIMITS ("limits"),
		ROCKET ("rocket");

		private String nameUnlocalized;
		private upgradeCategories(String name) {
			this.nameUnlocalized = name;
		}

		public String getName() {
			if (clientProxy != null) {
				return new TranslatableText("grapplemod.upgradecategories." + this.nameUnlocalized).toString();
			} else {
				return nameUnlocalized;
			}
		}

		public static upgradeCategories fromInt(int i) {
			return upgradeCategories.values()[i];
		}
		public int toInt() {
			for (int i = 0; i < size(); i++) {
				if (upgradeCategories.values()[i] == this) {
					return i;
				}
			}
			return -1;
		}
		public static int size() {
			return upgradeCategories.values().length;
		}
		public Item getItem() {
			if (this == upgradeCategories.ROPE) {
				return CommonSetup.ropeUpgradeItem;
			} else if (this == upgradeCategories.THROW) {
				return CommonSetup.throwUpgradeItem;
			} else if (this == upgradeCategories.MOTOR) {
				return CommonSetup.motorUpgradeItem;
			} else if (this == upgradeCategories.SWING) {
				return CommonSetup.swingUpgradeItem;
			} else if (this == upgradeCategories.STAFF) {
				return CommonSetup.staffUpgradeItem;
			} else if (this == upgradeCategories.FORCEFIELD) {
				return CommonSetup.forcefieldUpgradeItem;
			} else if (this == upgradeCategories.MAGNET) {
				return CommonSetup.magnetUpgradeItem;
			} else if (this == upgradeCategories.DOUBLE) {
				return CommonSetup.doubleUpgradeItem;
			} else if (this == upgradeCategories.LIMITS) {
				return CommonSetup.limitsUpgradeItem;
			} else if (this == upgradeCategories.ROCKET) {
				return CommonSetup.rocketUpgradeItem;
			}
			return null;
		}
	};

	public GrappleCustomization() {
		for (String option : booleanoptions) {
			GrappleConfig.Config.GrapplingHook.Custom.BooleanCustomizationOption optionconfig = getBooleanConfig(option);
			this.setBoolean(option, optionconfig.default_value);
		}
		for (String option : doubleoptions) {
			GrappleConfig.Config.GrapplingHook.Custom.DoubleCustomizationOption optionconfig = getDoubleConfig(option);
			this.setDouble(option, optionconfig.default_value);
		}
	}

	public GrappleConfig.Config.GrapplingHook.Custom.BooleanCustomizationOption getBooleanConfig(String option) {
		return switch (option) {
			case "phaserope" -> GrappleConfig.getConf().grapplinghook.custom.rope.phaserope;
			case "motor" -> GrappleConfig.getConf().grapplinghook.custom.motor.motor;
			case "motorwhencrouching" -> GrappleConfig.getConf().grapplinghook.custom.motor.motorwhencrouching;
			case "motorwhennotcrouching" -> GrappleConfig.getConf().grapplinghook.custom.motor.motorwhennotcrouching;
			case "smartmotor" -> GrappleConfig.getConf().grapplinghook.custom.motor.smartmotor;
			case "enderstaff" -> GrappleConfig.getConf().grapplinghook.custom.enderstaff.enderstaff;
			case "repel" -> GrappleConfig.getConf().grapplinghook.custom.forcefield.repel;
			case "attract" -> GrappleConfig.getConf().grapplinghook.custom.magnet.attract;
			case "doublehook" -> GrappleConfig.getConf().grapplinghook.custom.doublehook.doublehook;
			case "smartdoublemotor" -> GrappleConfig.getConf().grapplinghook.custom.doublehook.smartdoublemotor;
			case "motordampener" -> GrappleConfig.getConf().grapplinghook.custom.motor.motordampener;
			case "reelin" -> GrappleConfig.getConf().grapplinghook.custom.hookthrower.reelin;
			case "pullbackwards" -> GrappleConfig.getConf().grapplinghook.custom.motor.pullbackwards;
			case "oneropepull" -> GrappleConfig.getConf().grapplinghook.custom.doublehook.oneropepull;
			case "sticky" -> GrappleConfig.getConf().grapplinghook.custom.rope.sticky;
			case "detachonkeyrelease" -> GrappleConfig.getConf().grapplinghook.custom.hookthrower.detachonkeyrelease;
			case "rocket" -> GrappleConfig.getConf().grapplinghook.custom.rocket.rocketenabled;
			default -> null;
		};
	}

	public GrappleConfig.Config.GrapplingHook.Custom.DoubleCustomizationOption getDoubleConfig(String option) {
		return switch (option) {
			case "maxlen" -> GrappleConfig.getConf().grapplinghook.custom.rope.maxlen;
			case "hookgravity" -> GrappleConfig.getConf().grapplinghook.custom.hookthrower.hookgravity;
			case "throwspeed" -> GrappleConfig.getConf().grapplinghook.custom.hookthrower.throwspeed;
			case "motormaxspeed" -> GrappleConfig.getConf().grapplinghook.custom.motor.motormaxspeed;
			case "motoracceleration" -> GrappleConfig.getConf().grapplinghook.custom.motor.motoracceleration;
			case "playermovementmult" -> GrappleConfig.getConf().grapplinghook.custom.swing.playermovementmult;
			case "repelforce" -> GrappleConfig.getConf().grapplinghook.custom.forcefield.repelforce;
			case "attractradius" -> GrappleConfig.getConf().grapplinghook.custom.magnet.attractradius;
			case "angle" -> GrappleConfig.getConf().grapplinghook.custom.doublehook.angle;
			case "sneakingangle" -> GrappleConfig.getConf().grapplinghook.custom.doublehook.sneakingangle;
			case "verticalthrowangle" -> GrappleConfig.getConf().grapplinghook.custom.hookthrower.verticalthrowangle;
			case "sneakingverticalthrowangle" ->
					GrappleConfig.getConf().grapplinghook.custom.hookthrower.sneakingverticalthrowangle;
			case "rocket_force" -> GrappleConfig.getConf().grapplinghook.custom.rocket.rocket_force;
			case "rocket_active_time" -> GrappleConfig.getConf().grapplinghook.custom.rocket.rocket_active_time;
			case "rocket_refuel_ratio" -> GrappleConfig.getConf().grapplinghook.custom.rocket.rocket_refuel_ratio;
			case "rocket_vertical_angle" -> GrappleConfig.getConf().grapplinghook.custom.rocket.rocket_vertical_angle;
			default -> null;
		};
	}

	public NbtCompound writeNBT() {
		NbtCompound compound = new NbtCompound();
		for (String option : booleanoptions) {
			compound.putBoolean(option, this.getBoolean(option));
		}
		for (String option : doubleoptions) {
			compound.putDouble(option, this.getDouble(option));
		}
		return compound;
	}

	public void loadNBT(NbtCompound compound) {
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
		switch (option) {
		case "phaserope" -> this.phaserope = bool;
		case "motor" -> this.motor = bool;
		case "motorwhencrouching" -> this.motorwhencrouching = bool;
		case "motorwhennotcrouching" -> this.motorwhennotcrouching = bool;
		case "smartmotor" -> this.smartmotor = bool;
		case "enderstaff" -> this.enderstaff = bool;
		case "repel" -> this.repel = bool;
		case "attract" -> this.attract = bool;
		case "doublehook" -> this.doublehook = bool;
		case "smartdoublemotor" -> this.smartdoublemotor = bool;
		case "motordampener" -> this.motordampener = bool;
		case "reelin" -> this.reelin = bool;
		case "pullbackwards" -> this.pullbackwards = bool;
		case "oneropepull" -> this.oneropepull = bool;
		case "sticky" -> this.sticky = bool;
		case "detachonkeyrelease" -> this.detachonkeyrelease = bool;
		case "rocket" -> this.rocket = bool;
		default -> System.out.println("Option doesn't exist: " + option);
		}
	}

	public boolean getBoolean(String option) {
		switch (option) {
		case "phaserope":
			return this.phaserope;
		case "motor":
			return this.motor;
		case "motorwhencrouching":
			return this.motorwhencrouching;
		case "motorwhennotcrouching":
			return this.motorwhennotcrouching;
		case "smartmotor":
			return this.smartmotor;
		case "enderstaff":
			return this.enderstaff;
		case "repel":
			return this.repel;
		case "attract":
			return this.attract;
		case "doublehook":
			return this.doublehook;
		case "smartdoublemotor":
			return this.smartdoublemotor;
		case "motordampener":
			return this.motordampener;
		case "reelin":
			return this.reelin;
		case "pullbackwards":
			return this.pullbackwards;
		case "oneropepull":
			return this.oneropepull;
		case "sticky":
			return this.sticky;
		case "detachonkeyrelease":
			return this.detachonkeyrelease;
		case "rocket":
			return this.rocket;
		}
		System.out.println("Option doesn't exist: " + option);
		return false;
	}

	public void setDouble(String option, double d) {
		switch (option) {
			case "maxlen" -> this.maxlen = d;
			case "hookgravity" -> this.hookgravity = d;
			case "throwspeed" -> this.throwspeed = d;
			case "motormaxspeed" -> this.motormaxspeed = d;
			case "motoracceleration" -> this.motoracceleration = d;
			case "playermovementmult" -> this.playermovementmult = d;
			case "repelforce" -> this.repelforce = d;
			case "attractradius" -> this.attractradius = d;
			case "angle" -> this.angle = d;
			case "sneakingangle" -> this.sneakingangle = d;
			case "verticalthrowangle" -> this.verticalthrowangle = d;
			case "sneakingverticalthrowangle" -> this.sneakingverticalthrowangle = d;
			case "rocket_force" -> this.rocket_force = d;
			case "rocket_active_time" -> this.rocket_active_time = d;
			case "rocket_refuel_ratio" -> this.rocket_refuel_ratio = d;
			case "rocket_vertical_angle" -> this.rocket_vertical_angle = d;
			default -> System.out.println("Option doesn't exist: " + option);
		}
	}
	
	public double getDouble(String option) {
		switch (option) {
			case "maxlen":
				return maxlen;
			case "hookgravity":
				return hookgravity;
			case "throwspeed":
				return throwspeed;
			case "motormaxspeed":
				return motormaxspeed;
			case "motoracceleration":
				return motoracceleration;
			case "playermovementmult":
				return playermovementmult;
			case "repelforce":
				return repelforce;
			case "attractradius":
				return attractradius;
			case "angle":
				return angle;
			case "sneakingangle":
				return sneakingangle;
			case "verticalthrowangle":
				return verticalthrowangle;
			case "sneakingverticalthrowangle":
				return sneakingverticalthrowangle;
			case "rocket_force":
				return this.rocket_force;
			case "rocket_active_time":
				return rocket_active_time;
			case "rocket_refuel_ratio":
				return rocket_refuel_ratio;
			case "rocket_vertical_angle":
				return rocket_vertical_angle;
		}
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
		return "grapplecustomization." + option;
	}

	public String getDescription(String option) {
		return "grapplecustomization." + option + ".desc";
	}

	public boolean isOptionValid(String option) {
		if (Objects.equals(option, "motormaxspeed") || Objects.equals(option, "motoracceleration") || Objects.equals(option, "motorwhencrouching") || Objects.equals(option, "motorwhennotcrouching") || Objects.equals(option, "smartmotor") || Objects.equals(option, "motordampener") || Objects.equals(option, "pullbackwards")) {
			return this.motor;
		}

		if (Objects.equals(option, "sticky")) {
			return !this.phaserope;
		}

		else if (Objects.equals(option, "sneakingangle")) {
			return this.doublehook && !this.reelin;
		}

		else if (Objects.equals(option, "repelforce")) {
			return this.repel;
		}

		else if (Objects.equals(option, "attractradius")) {
			return this.attract;
		}

		else if (Objects.equals(option, "angle")) {
			return this.doublehook;
		}

		else if (Objects.equals(option, "smartdoublemotor") || Objects.equals(option, "oneropepull")) {
			return this.doublehook && this.motor;
		}

		else if (Objects.equals(option, "rocket_active_time") || Objects.equals(option, "rocket_refuel_ratio") || Objects.equals(option, "rocket_force") || Objects.equals(option, "rocket_vertical_angle")) {
			return this.rocket;
		}

		return true;
	}

	public double getMax(String option, int upgrade) {
		GrappleConfig.Config.GrapplingHook.Custom.DoubleCustomizationOption configoption = this.getDoubleConfig(option);
		return upgrade == 1 ? configoption.max_upgraded : configoption.max;
	}

	public double getMin(String option, int upgrade) {
		GrappleConfig.Config.GrapplingHook.Custom.DoubleCustomizationOption configoption = this.getDoubleConfig(option);
		return upgrade == 1 ? configoption.min_upgraded : configoption.min;
	}

	public int optionEnabled(String option) {
		GrappleConfig.Config.GrapplingHook.Custom.BooleanCustomizationOption configoption = this.getBooleanConfig(option);
		if (configoption != null) {
			return configoption.enabled;
		}
		return this.getDoubleConfig(option).enabled;
	}

	public boolean equals(GrappleCustomization other) {
		for (String option : booleanoptions) {
			if (this.getBoolean(option) != other.getBoolean(option)) {
				return false;
			}
		}
		for (String option : doubleoptions) {
			if (this.getDouble(option) != other.getDouble(option)) {
				return false;
			}
		}
		return true;
	}

	public static GrappleCustomization DEFAULT = new GrappleCustomization();
}
