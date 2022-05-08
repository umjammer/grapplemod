package com.yyon.grapplinghook.network;

import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static com.yyon.grapplinghook.grapplemod.LOGGER;


public interface BaseMessageClient {

	/** id */
	Identifier getIdentifier();

	/** serialize */
	PacketByteBuf toPacket();

	/** to client */
	default void send(Entity entity) {
		if (!(entity instanceof ServerPlayerEntity player)) {
			LOGGER.warn("entity is not a player: " + entity + ", " + this.getClass().getName());
		} else {
			player.networkHandler.sendPacket(new CustomPayloadS2CPacket(getIdentifier(), toPacket()));
		}
	}
}
