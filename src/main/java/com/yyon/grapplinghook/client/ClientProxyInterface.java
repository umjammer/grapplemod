package com.yyon.grapplinghook.client;

import java.util.List;

import com.yyon.grapplinghook.blocks.modifierblock.TileEntityGrappleModifier;
import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.utils.GrappleCustomization;
import com.yyon.grapplinghook.utils.Vec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ClientProxyInterface {

	void resetLauncherTime(int playerid);

	void launchPlayer(PlayerEntity player);

	enum McKeys {
		keyBindUseItem,
		keyBindForward,
		keyBindLeft,
		keyBindBack,
		keyBindRight,
		keyBindJump,
		keyBindSneak,
		keyBindAttack
	}

	String getKeyname(McKeys keyenum);

	boolean isKeyDown(McKeys keybindjump);

	void openModifierScreen(TileEntityGrappleModifier tileent);

	void startRocket(PlayerEntity player, GrappleCustomization custom);

	void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio);

	double getRocketFunctioning();

	boolean isWallRunning(Entity entity, Vec motion);

	boolean isSliding(Entity entity, Vec motion);

	GrappleController createControl(int id, int hookEntityId, int entityid, World world, Vec pos, BlockPos blockpos, GrappleCustomization custom);

	void playSlideSound(Entity entity);

	void playWallrunJumpSound(Entity entity);

	void playDoubleJumpSound(Entity entity);

	void fillGrappleVariants(ItemGroup tab, List<ItemStack> items);

	enum GrappleKeys {
		key_boththrow,
		key_leftthrow,
		key_rightthrow,
		key_motoronoff,
		key_jumpanddetach,
		key_slow,
		key_climb,
		key_climbup,
		key_climbdown,
		key_enderlaunch,
		key_rocket,
		key_slide
	}

	boolean isKeyDown(GrappleKeys key);

	GrappleController unregisterController(int entityId);

	double getTimeSinceLastRopeJump(World world);

	void resetRopeJumpTime(World level);

	boolean isMovingSlowly(Entity entity);

	void playSound(Identifier loc, float volume);

	int getWallrunTicks();

	void setWallrunTicks(int newWallrunTicks);
}
