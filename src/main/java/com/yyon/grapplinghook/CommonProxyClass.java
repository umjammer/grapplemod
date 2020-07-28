package com.yyon.grapplinghook;

import com.yyon.grapplinghook.blocks.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.controllers.GrappleController;
import com.yyon.grapplinghook.entities.GrappleArrow;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public abstract class CommonProxyClass implements ModInitializer {

    public enum Keys {
		keyBindUseItem,
		keyBindForward,
		keyBindLeft,
		keyBindBack,
		keyBindRight,
		keyBindJump,
		keyBindSneak,
		keyBindAttack
	}
	
	public void sendplayermovementmessage(GrappleArrow grappleArrow, int playerid, int arrowid) {
	}

	public void getplayermovement(GrappleController control, int playerid) {
	}
	
//	@SubscribeEvent
//	public void onLivingFallEvent(LivingFallEvent event)
//	{
//		if (event.getEntity() != null && grapplemod.attached.contains(event.getEntity().getEntityId()))
//		{
//			event.setCanceled(true);
//		}
//	}
	
	
	public void resetlaunchertime(int playerid) {
	}

	public void launchplayer(PlayerEntity player) {
	}
	
	public boolean isSneaking(Entity entity) {
		return entity.isSneaking();
	}
	
//    @SubscribeEvent
    public void onBlockBreak(/*BreakEvent event*/){
    }
    
    
    public void blockbreak(/*BreakEvent event*/) {
    }

//    @SubscribeEvent
    public void onLivingHurt(/*LivingHurtEvent event*/) {
//    	if (event.getSource() == DamageSource.IN_WALL) {
//    		if (GrappleMod.attached.contains(event.getEntity().getEntityId())) {
//    			event.setCanceled(true);
//    		}
//    	}
    }
    
//    @SubscribeEvent
    public void onLivingDeath(/*LivingDeathEvent event*/) {
//    	this.handleDeath(event.getEntity());
    }
    
    public void handleDeath(Entity entity) {
    }
    
	public String getKeyName(CommonProxyClass.Keys keyenum) {
		return null;
	}

	public void openModifierScreen(GrappleModifierBlockEntity tileent) {
	}
	
	public String localize(String string) {
		return string;
	}

	public void startRocket(PlayerEntity player, GrappleCustomization custom) {
	}
	
	public void updateRocketRegen(double rocket_active_time, double rocket_refuel_ratio) {
	}

	public double getRocketFunctioning() {
		return 0;
	}

	public boolean isWallRunning(Entity entity) {
		return false;
	}
	
	public boolean isSliding(Entity entity) {
		return false;
	}
}
