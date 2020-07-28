package com.yyon.grapplinghook;

import com.yyon.grapplinghook.network.GrappleEndMessage;
import com.yyon.grapplinghook.network.GrappleModifierMessage;
import com.yyon.grapplinghook.network.KeypressMessage;
import com.yyon.grapplinghook.network.LoggedInMessage;
import com.yyon.grapplinghook.network.PlayerMovementMessage;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;


public class ServerProxyClass extends CommonProxyClass {
	
    @Override
    public void onInitialize() {
        ServerSidePacketRegistry.INSTANCE.register(GrappleEndMessage.IDENTIFIER, (packetContext, packetByteBuf) -> {
            GrappleEndMessage.run((ServerPlayerEntity) packetContext.getPlayer(), packetByteBuf);
        });

        ServerSidePacketRegistry.INSTANCE.register(PlayerMovementMessage.IDENTIFIER, (packetContext, packetByteBuf) -> {
            PlayerMovementMessage.run((ServerPlayerEntity) packetContext.getPlayer(), packetByteBuf);
        });

        ServerSidePacketRegistry.INSTANCE.register(GrappleModifierMessage.IDENTIFIER, (packetContext, packetByteBuf) -> {
            GrappleModifierMessage.run((ServerPlayerEntity) packetContext.getPlayer(), packetByteBuf);
        });

        ServerSidePacketRegistry.INSTANCE.register(KeypressMessage.IDENTIFIER, (packetContext, packetByteBuf) -> {
            KeypressMessage.run((ServerPlayerEntity) packetContext.getPlayer(), packetByteBuf);
        });
		
        if (GrappleConfig.getconf().overrideAllowFlight) {
		    MinecraftClient.getInstance().player.setAllowFlight(true);
		}

        ServerTickEvents.START_WORLD_TICK.register(world -> {
            System.out.println("Player logged in event");
            if (world.player instanceof ServerPlayerEntity) {
                new LoggedInMessage(GrappleConfig.options).send((PlayerEntity) e.player);
            } else {
                System.out.println("Not an EntityPlayerMP");
            }
        });
    }
	
	@Override
	public void handleDeath(Entity entity) {
		GrappleMod.attached.remove(entity.getEntityId());
	}
	
//	@SubscribeEvent
	public void onPlayerLoggedInEvent(/*PlayerLoggedInEvent e*/) {
	}
}
