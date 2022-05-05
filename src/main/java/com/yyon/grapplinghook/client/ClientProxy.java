package com.yyon.grapplinghook.client;

import java.util.ArrayList;
import java.util.List;

import com.yyon.grapplinghook.blocks.modifierblock.GuiModifier;
import com.yyon.grapplinghook.blocks.modifierblock.TileEntityGrappleModifier;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.items.GrapplehookItem;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.GrapplemodUtils;
import com.yyon.grapplinghook.utils.Vec;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.yyon.grapplinghook.client.ClientSetup.clientControllerManager;
import static com.yyon.grapplinghook.grapplemod.MODID;


public class ClientProxy implements ClientProxyInterface {

	public static final Identifier doubleJumpSoundLoc = new Identifier(MODID, "doublejump");
	public static final Identifier slideSoundLoc = new Identifier(MODID, "slide");

	@Override
	public void startRocket(PlayerEntity player, GrappleCustomization custom) {
		clientControllerManager.startRocket(player, custom);
	}

	@Override
	public String getKeyname(McKeys keyenum) {
		KeyBinding binding = null;

		GameOptions gs = MinecraftClient.getInstance().options;

		if (keyenum == McKeys.keyBindAttack) {
			binding = gs.attackKey;
		} else if (keyenum == McKeys.keyBindBack) {
			binding = gs.backKey;
		} else if (keyenum == McKeys.keyBindForward) {
			binding = gs.forwardKey;
		} else if (keyenum == McKeys.keyBindJump) {
			binding = gs.jumpKey;
		} else if (keyenum == McKeys.keyBindLeft) {
			binding = gs.leftKey;
		} else if (keyenum == McKeys.keyBindRight) {
			binding = gs.rightKey;
		} else if (keyenum == McKeys.keyBindSneak) {
			binding = gs.sneakKey;
		} else if (keyenum == McKeys.keyBindUseItem) {
			binding = gs.useKey;
		}

		if (binding == null) {
			return "";
		}

		String displayname = binding.getBoundKeyTranslationKey();
		if (displayname.equals("Button 1")) {
			return "Left Click";
		} else if (displayname.equals("Button 2")) {
			return "Right Click";
		} else {
			return displayname;
		}
	}

	@Override
	public void openModifierScreen(TileEntityGrappleModifier tileent) {
		MinecraftClient.getInstance().setScreen(new GuiModifier(tileent));
	}

	@Override
	public void playSlideSound(Entity entity) {
		this.playSound(slideSoundLoc, GrappleConfig.getClientConf().sounds.slide_sound_volume);
	}

	@Override
	public void playDoubleJumpSound(Entity entity) {
		this.playSound(doubleJumpSoundLoc, GrappleConfig.getClientConf().sounds.doublejump_sound_volume * 0.7F);
	}

	@Override
	public void playWallrunJumpSound(Entity entity) {
		this.playSound(doubleJumpSoundLoc, GrappleConfig.getClientConf().sounds.wallrunjump_sound_volume * 0.7F);
	}

	List<ItemStack> grapplingHookVariants = null;

	RecipeType<Recipe<PlayerInventory>> grapplehookRecipeType;

	@Override
	public void fillGrappleVariants(ItemGroup tab, List<ItemStack> items) {
		if (!MinecraftClient.getInstance().isRunning() || MinecraftClient.getInstance().player == null || MinecraftClient.getInstance().player.world == null || MinecraftClient.getInstance().player.world.getRecipeManager() == null) {
			return;
		}

		if (grapplingHookVariants == null) {
			grapplingHookVariants = new ArrayList<>();
			World world = MinecraftClient.getInstance().world;
			PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory(); // TODO
			RecipeManager recipemanager = MinecraftClient.getInstance().player.world.getRecipeManager();
			recipemanager.getAllMatches(grapplehookRecipeType, inventory, world).forEach(r -> {
				ItemStack stack = r.getOutput();
				if (stack.getItem() instanceof GrapplehookItem) {
					if (!CommonSetup.grapplingHookItem.getCustomization(stack).equals(new GrappleCustomization())) {
						grapplingHookVariants.add(stack);
					}
				}
			});
		}

		items.addAll(grapplingHookVariants);
	}

	// LESSON: when a dependence mapping is different from the current project,
	// include the different mapping jar into this project.
	// i.e. cloth-config mapping is the mojang's mapping and this project is the yarn v2 mapping
	//  - mojang: net.minecraft.client.gui.screens.Screen
	//  - yarn: net.minecraft.client.gui.screen.Screen
	// @see build.gradle/dependencies/include
	public Screen onConfigScreen(MinecraftClient mc, Screen screen) {
		return AutoConfig.getConfigScreen(GrappleConfig.class, screen).get();
	}

	@Override
	public void resetLauncherTime(int playerid) {
		clientControllerManager.resetLauncherTime(playerid);
	}

	@Override
	public void launchPlayer(PlayerEntity player) {
		clientControllerManager.launchPlayer(player);
	}

	@Override
	public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio) {
		clientControllerManager.updateRocketRegen(rocket_active_time, rocket_refuel_ratio);
	}

	@Override
	public double getRocketFunctioning() {
		return clientControllerManager.getRocketFunctioning();
	}

	@Override
	public boolean isWallRunning(Entity entity, Vec motion) {
		return clientControllerManager.isWallRunning(entity, motion);
	}

	@Override
	public boolean isSliding(Entity entity, Vec motion) {
		return clientControllerManager.isSliding(entity, motion);
	}

	@Override
	public GrappleController createControl(int id, int hookEntityId, int entityid, World world, Vec pos, BlockPos blockpos,
										   GrappleCustomization custom) {
		return clientControllerManager.createControl(id, hookEntityId, entityid, world, pos, blockpos, custom);
	}

	@Override
	public boolean isKeyDown(GrappleKeys key) {
		if (key == GrappleKeys.key_boththrow) {return ClientSetup.key_boththrow.isPressed();}
		else if (key == GrappleKeys.key_leftthrow) {return ClientSetup.key_leftthrow.isPressed();}
		else if (key == GrappleKeys.key_rightthrow) {return ClientSetup.key_rightthrow.isPressed();}
		else if (key == GrappleKeys.key_motoronoff) {return ClientSetup.key_motoronoff.isPressed();}
		else if (key == GrappleKeys.key_jumpanddetach) {return ClientSetup.key_jumpanddetach.isPressed();}
		else if (key == GrappleKeys.key_slow) {return ClientSetup.key_slow.isPressed();}
		else if (key == GrappleKeys.key_climb) {return ClientSetup.key_climb.isPressed();}
		else if (key == GrappleKeys.key_climbup) {return ClientSetup.key_climbup.isPressed();}
		else if (key == GrappleKeys.key_climbdown) {return ClientSetup.key_climbdown.isPressed();}
		else if (key == GrappleKeys.key_enderlaunch) {return ClientSetup.key_enderlaunch.isPressed();}
		else if (key == GrappleKeys.key_rocket) {return ClientSetup.key_rocket.isPressed();}
		else if (key == GrappleKeys.key_slide) {return ClientSetup.key_slide.isPressed();}
		return false;
	}

	@Override
	public GrappleController unregisterController(int entityId) {
		return ClientControllerManager.unregisterController(entityId);
	}

	@Override
	public double getTimeSinceLastRopeJump(World world) {
		return GrapplemodUtils.getTime(world) - ClientControllerManager.prevRopeJumpTime;
	}

	@Override
	public void resetRopeJumpTime(World world) {
		ClientControllerManager.prevRopeJumpTime = GrapplemodUtils.getTime(world);
	}

	@Override
	public boolean isKeyDown(McKeys keyenum) {
		if (keyenum == McKeys.keyBindAttack) {
			return MinecraftClient.getInstance().options.attackKey.isPressed();
		} else if (keyenum == McKeys.keyBindBack) {
			return MinecraftClient.getInstance().options.backKey.isPressed();
		} else if (keyenum == McKeys.keyBindForward) {
			return MinecraftClient.getInstance().options.forwardKey.isPressed();
		} else if (keyenum == McKeys.keyBindJump) {
			return MinecraftClient.getInstance().options.jumpKey.isPressed();
		} else if (keyenum == McKeys.keyBindLeft) {
			return MinecraftClient.getInstance().options.leftKey.isPressed();
		} else if (keyenum == McKeys.keyBindRight) {
			return MinecraftClient.getInstance().options.rightKey.isPressed();
		} else if (keyenum == McKeys.keyBindSneak) {
			return MinecraftClient.getInstance().options.sneakKey.isPressed();
		} else if (keyenum == McKeys.keyBindUseItem) {
			return MinecraftClient.getInstance().options.useKey.isPressed();
		}
		return false;
	}

	@Override
	public boolean isMovingSlowly(Entity entity) {
		if (entity instanceof PlayerEntity playerEntity) {
			return playerEntity.hasStatusEffect(StatusEffects.SLOW_FALLING); // TODO
		}
		return false;
	}

	@Override
	public void playSound(Identifier loc, float volume) {
		PlayerEntity player = MinecraftClient.getInstance().player;
		MinecraftClient.getInstance().getSoundManager().play(new PositionedSoundInstance(loc, SoundCategory.PLAYERS, volume, 1.0F, false, 0, SoundInstance.AttenuationType.NONE, player.getX(), player.getY(), player.getZ(), false));
	}

	@Override
	public int getWallrunTicks() {
		return clientControllerManager.ticksWallRunning;
	}

	@Override
	public void setWallrunTicks(int newWallrunTicks) {
		clientControllerManager.ticksWallRunning = newWallrunTicks;
	}
}
