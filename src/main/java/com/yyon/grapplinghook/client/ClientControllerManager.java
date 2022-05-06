package com.yyon.grapplinghook.client;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.controllers.AirfrictionController;
import com.yyon.grapplinghook.controllers.ForcefieldController;
import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.entities.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.items.EnderStaffItem;
import com.yyon.grapplinghook.items.GrapplehookItem;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.GrapplemodUtils;
import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.yyon.grapplinghook.client.ClientSetup.clientProxy;
import static com.yyon.grapplinghook.grapplemod.MODID;


public class ClientControllerManager {

	public Map<Integer, GrappleController> controllers = new HashMap<>();

	public Map<Integer, Long> enderLaunchTimer = new HashMap<>();

	public double rocketFuel = 1.0;
	public double rocketIncreaseTick = 0.0;
	public double rocketDecreaseTick = 0.0;

	public int ticksWallRunning = 0;

	public void onClientTick(PlayerEntity player) {
		if (player.isOnGround() || (controllers.containsKey(player.getId()) && controllers.get(player.getId()).controllerId == GrapplemodUtils.GRAPPLEID)) {
			ticksWallRunning = 0;
		}

		if (this.isWallRunning(player, Vec.motionVec(player))) {
			if (!controllers.containsKey(player.getId())) {
				GrappleController controller = this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.world, new Vec(0,0,0), null, null);
				if (controller.getWallDirection() == null) {
					controller.unattach();
				}
			}

			if (controllers.containsKey(player.getId())) {
				ticksSinceLastOnGround = 0;
				alreadyUsedDoubleJump = false;
			}
		}

		this.checkDoubleJump();

		this.checkSlide(player);

		this.rocketFuel += this.rocketIncreaseTick;

		try {
			for (GrappleController controller : controllers.values()) {
				controller.doClientTick();
			}
		} catch (ConcurrentModificationException e) {
			System.out.println("ConcurrentModificationException caught");
		}

		if (this.rocketFuel > 1) {this.rocketFuel = 1;}

		if (player.isOnGround()) {
			if (enderLaunchTimer.containsKey(player.getId())) {
				long timer = GrapplemodUtils.getTime(player.world) - enderLaunchTimer.get(player.getId());
				if (timer > 10) {
					this.resetLauncherTime(player.getId());
				}
			}
		}
	}

	public void checkSlide(PlayerEntity player) {
		if (ClientSetup.key_slide.isPressed() && !controllers.containsKey(player.getId()) && this.isSliding(player, Vec.motionVec(player))) {
			this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.world, new Vec(0,0,0), null, null);
		}
	}

	public void launchPlayer(PlayerEntity player) {
		long prevtime;
		if (enderLaunchTimer.containsKey(player.getId())) {
			prevtime = enderLaunchTimer.get(player.getId());
		} else {
			prevtime = 0;
		}
		long timer = GrapplemodUtils.getTime(player.world) - prevtime;
		if (timer > GrappleConfig.getConf().enderstaff.ender_staff_recharge) {
			if ((player.getStackInHand(Hand.MAIN_HAND) != null && (player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof EnderStaffItem || player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof GrapplehookItem)) || (player.getStackInHand(Hand.OFF_HAND) != null && (player.getStackInHand(Hand.OFF_HAND).getItem() instanceof EnderStaffItem || player.getStackInHand(Hand.OFF_HAND).getItem() instanceof GrapplehookItem))) {
				enderLaunchTimer.put(player.getId(), GrapplemodUtils.getTime(player.world));

	        	Vec facing = Vec.lookVec(player);

	        	GrappleCustomization custom = null;
	        	if (player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof GrapplehookItem) {
	        		custom = ((GrapplehookItem) player.getStackInHand(Hand.MAIN_HAND).getItem()).getCustomization(player.getStackInHand(Hand.MAIN_HAND));
	        	} else if (player.getStackInHand(Hand.OFF_HAND).getItem() instanceof GrapplehookItem) {
	        		custom = ((GrapplehookItem) player.getStackInHand(Hand.OFF_HAND).getItem()).getCustomization(player.getStackInHand(Hand.OFF_HAND));
	        	}

				if (!controllers.containsKey(player.getId())) {
					player.setOnGround(false);
					this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.world, new Vec(0,0,0), null, custom);
				}
				facing.mult_ip(GrappleConfig.getConf().enderstaff.ender_staff_strength);
				receiveEnderLaunch(player.getId(), facing.x, facing.y, facing.z);
				clientProxy.playSound(new Identifier(MODID, "enderstaff"), GrappleConfig.getClientConf().sounds.enderstaff_sound_volume * 0.5F);
			}
		}
	}

	public void resetLauncherTime(int playerid) {
		if (enderLaunchTimer.containsKey(playerid)) {
			enderLaunchTimer.put(playerid, (long) 0);
		}
	}

	public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio) {
		this.rocketDecreaseTick = 0.05 / 2.0 / rocket_active_time;
		this.rocketIncreaseTick = 0.05 / 2.0 / rocket_active_time / rocket_refuel_ratio;
	}

	public double getRocketFunctioning() {
		this.rocketFuel -= this.rocketIncreaseTick;
		this.rocketFuel -= this.rocketDecreaseTick;

		if (this.rocketFuel >= 0) {
			return 1;
		} else {
			this.rocketFuel = 0;
			return this.rocketIncreaseTick / this.rocketDecreaseTick / 2.0;
		}
	}

	public boolean isWallRunning(Entity entity, Vec motion) {
		if (entity.horizontalCollision && !entity.isOnGround() && !entity.isSneaking()) {
			if (entity instanceof LivingEntity && ((LivingEntity) entity).isClimbing()) {
				return false;
			}
			for (ItemStack stack : entity.getArmorItems()) {
				if (stack != null) {
					Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
					if (enchantments.containsKey(CommonSetup.wallrunEnchantment)) {
						if (enchantments.get(CommonSetup.wallrunEnchantment) >= 1) {
							if (!ClientSetup.key_jumpanddetach.isPressed() && !MinecraftClient.getInstance().options.jumpKey.isPressed()) {
								BlockHitResult raytraceresult = GrapplemodUtils.rayTraceBlocks(entity.world, Vec.positionVec(entity), Vec.positionVec(entity).add(new Vec(0, -1, 0)), entity);
								if (raytraceresult == null) {
									double current_speed = Math.sqrt(Math.pow(motion.x, 2) + Math.pow(motion.z,  2));
									if (current_speed >= GrappleConfig.getConf().enchantments.wallrun.wallrun_min_speed) {
										return true;
									}
								}
							}
						}
						break;
					}
				}
			}
		}
		return false;
	}

	boolean prevJumpButton = false;
	int ticksSinceLastOnGround = 0;
	boolean alreadyUsedDoubleJump = false;

	public void checkDoubleJump() {
		PlayerEntity player = MinecraftClient.getInstance().player;

		if (player.isOnGround()) {
			ticksSinceLastOnGround = 0;
			alreadyUsedDoubleJump = false;
		} else {
			ticksSinceLastOnGround++;
		}

		boolean isjumpbuttondown = MinecraftClient.getInstance().options.jumpKey.isPressed();

		if (isjumpbuttondown && !prevJumpButton && !player.isInsideWaterOrBubbleColumn() && !player.isInLava()) {

			if (ticksSinceLastOnGround > 3) {
				if (!alreadyUsedDoubleJump) {
					if (wearingDoubleJumpEnchant(player)) {
						if (!controllers.containsKey(player.getId()) || controllers.get(player.getId()) instanceof AirfrictionController) {
							if (!controllers.containsKey(player.getId())) {
								this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.world, new Vec(0,0,0), null, null);
							}
							GrappleController controller = controllers.get(player.getId());
							if (controller instanceof AirfrictionController) {
								alreadyUsedDoubleJump = true;
								controller.doubleJump();
							}
							clientProxy.playDoubleJumpSound(controller.entity);
						}
					}
				}
			}
		}

		prevJumpButton = isjumpbuttondown;
	}

	public boolean wearingDoubleJumpEnchant(Entity entity) {
		if (entity instanceof PlayerEntity && ((PlayerEntity) entity).getAbilities().flying) {
			return false;
		}

		for (ItemStack stack : entity.getArmorItems()) {
			if (stack != null) {
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
				if (enchantments.containsKey(CommonSetup.doubleJumpEnchantment)) {
					if (enchantments.get(CommonSetup.doubleJumpEnchantment) >= 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean isWearingSlidingEnchant(Entity entity) {
		for (ItemStack stack : entity.getArmorItems()) {
			if (stack != null) {
				Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
				if (enchantments.containsKey(CommonSetup.slidingEnchantment)) {
					if (enchantments.get(CommonSetup.slidingEnchantment) >= 1) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean isSliding(Entity entity, Vec motion) {
		if (entity.isInsideWaterOrBubbleColumn() || entity.isInLava()) {return false;}

		if (entity.isOnGround() && ClientSetup.key_slide.isPressed()) {
			if (isWearingSlidingEnchant(entity)) {
				boolean was_sliding = false;
				int id = entity.getId();
				if (controllers.containsKey(id)) {
					GrappleController controller = controllers.get(id);
					if (controller instanceof AirfrictionController) {
						AirfrictionController afc = (AirfrictionController) controller;
						if (afc.wasSliding) {
							was_sliding = true;
						}
					}
				}
				double speed = motion.removeAlong(new Vec (0,1,0)).length();
				if (speed > GrappleConfig.getConf().enchantments.slide.sliding_end_min_speed && (was_sliding || speed > GrappleConfig.getConf().enchantments.slide.sliding_min_speed)) {
					return true;
				}
			}
		}

		return false;
	}


	public GrappleController createControl(int controllerId, int grapplehookEntityId, int playerId, World world, Vec pos, BlockPos blockPos, GrappleCustomization custom) {
		GrapplehookEntity grapplehookEntity = null;
		Entity grapplehookEntityUncast = world.getEntityById(grapplehookEntityId);
		if (grapplehookEntityUncast instanceof GrapplehookEntity) {
			grapplehookEntity = (GrapplehookEntity) grapplehookEntityUncast;
		}

		boolean multi = (custom != null) && (custom.doublehook);

		GrappleController currentcontroller = controllers.get(playerId);
		if (currentcontroller != null && !(multi && currentcontroller.custom != null && currentcontroller.custom.doublehook)) {
			currentcontroller.unattach();
		}

//		System.out.println(blockpos);

		GrappleController control = null;
		if (controllerId == GrapplemodUtils.GRAPPLEID) {
			if (!multi) {
				control = new GrappleController(grapplehookEntityId, playerId, world, pos, controllerId, custom);
			} else {
				control = controllers.get(playerId);
				boolean created = false;
				if (control != null && control.getClass().equals(GrappleController.class)) {
					GrappleController c = (GrappleController) control;
					if (control.custom.doublehook) {
						if (grapplehookEntity instanceof GrapplehookEntity) {
							GrapplehookEntity multiHookEntity = grapplehookEntity;
							created = true;
							c.addHookEntity(multiHookEntity);
							return control;
						}
					}
				}
				if (!created) {
					control = new GrappleController(grapplehookEntityId, playerId, world, pos, controllerId, custom);
				}
			}
		} else if (controllerId == GrapplemodUtils.REPELID) {
			control = new ForcefieldController(grapplehookEntityId, playerId, world, pos, controllerId);
		} else if (controllerId == GrapplemodUtils.AIRID) {
			control = new AirfrictionController(grapplehookEntityId, playerId, world, pos, controllerId, custom);
		} else {
			return null;
		}

		if (control == null) {
			return null;
		}

		if (blockPos != null) {
			controllerPos.put(blockPos, control);
		}

		registerController(playerId, control);

		Entity e = world.getEntityById(playerId);
		if (e instanceof ClientPlayerEntity p) {
			control.receivePlayerMovementMessage(p.input.movementSideways, p.input.movementForward, p.input.jumping, p.input.sneaking);
		}

		return control;
	}

	public void registerController(int entityId, GrappleController controller) {
		if (controllers.containsKey(entityId)) {
			controllers.get(entityId).unattach();
		}

		controllers.put(entityId, controller);
	}

	public GrappleController unregisterController(int entityId) {
		if (controllers.containsKey(entityId)) {
			GrappleController controller = controllers.get(entityId);
			controllers.remove(entityId);

			BlockPos pos = null;
			for (BlockPos blockpos : controllerPos.keySet()) {
				GrappleController otherController = controllerPos.get(blockpos);
				if (otherController == controller) {
					pos = blockpos;
				}
			}
			if (pos != null) {
				controllerPos.remove(pos);
			}
			return controller;
		}
		return null;
	}

	public void receiveGrappleDetach(int id) {
		GrappleController controller = controllers.get(id);
		if (controller != null) {
			controller.receiveGrappleDetach();
		}
	}

	public void receiveGrappleDetachHook(int id, int hookid) {
		GrappleController controller = controllers.get(id);
		if (controller != null) {
			controller.receiveGrappleDetachHook(hookid);
		}
	}

	public void receiveEnderLaunch(int id, double x, double y, double z) {
		GrappleController controller = controllers.get(id);
		if (controller != null) {
			controller.receiveEnderLaunch(x, y, z);
		} else {
			System.out.println("Couldn't find controller");
		}
	}

	public static class RocketSound extends MovingSoundInstance {
		GrappleController controller;
		boolean stopping = false;
		public float changespeed;
		protected RocketSound(GrappleController controller, SoundEvent sound, SoundCategory category) {
			super(sound, category);
			this.repeat = true;
			this.controller = controller;
			controller.rocket_key = true;
			controller.rocket_on = 1.0F;
			this.changespeed = GrappleConfig.getClientConf().sounds.rocket_sound_volume * 0.5F * 0.2F;
			this.volume = this.changespeed;
			this.repeatDelay = 0;
			this.attenuationType = SoundInstance.AttenuationType.NONE;
			this.relative = false;
		}

		@Override
		public void tick() {
			if (!controller.rocket_key || !controller.attached) {
				this.stopping = true;
			}
			float targetvolume = (float) controller.rocket_on * GrappleConfig.getClientConf().sounds.rocket_sound_volume * 0.5F;
			if (this.stopping) {
				targetvolume = 0;
			}
			float diff = Math.abs(targetvolume - this.volume);
			if (diff > changespeed) {
				this.volume = this.volume + changespeed * (this.volume > targetvolume ? -1 : 1);
			} else {
				this.volume = targetvolume;
			}
			if (this.volume == 0 && this.stopping) {
				this.setDone();
			}

			this.x = controller.entity.getX();
			this.y = controller.entity.getY();
			this.z = controller.entity.getZ();
		}
	}

	public void startRocket(PlayerEntity player, GrappleCustomization custom) {
		if (!custom.rocket) return;

		GrappleController controller;
		if (!controllers.containsKey(player.getId())) {
			controller = this.createControl(GrapplemodUtils.AIRID, -1, player.getId(), player.world, new Vec(0,0,0), null, custom);
		} else {
			controller = controllers.get(player.getId());
			if (controller.custom == null || !controller.custom.rocket) {
				if (controller.custom == null) {controller.custom = custom;}
				controller.custom.rocket = true;
				controller.custom.rocket_active_time = custom.rocket_active_time;
				controller.custom.rocket_force = custom.rocket_force;
				controller.custom.rocket_refuel_ratio = custom.rocket_refuel_ratio;
				this.updateRocketRegen(custom.rocket_active_time, custom.rocket_refuel_ratio);
			}
		}

		RocketSound sound = new RocketSound(controller, new SoundEvent(new Identifier(MODID, "rocket")), SoundCategory.PLAYERS);
		MinecraftClient.getInstance().getSoundManager().play(sound);
	}

	public Map<BlockPos, GrappleController> controllerPos = new HashMap<>();

	public long prevRopeJumpTime = 0;
}
