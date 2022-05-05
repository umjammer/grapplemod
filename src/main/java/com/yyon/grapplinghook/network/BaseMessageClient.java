package com.yyon.grapplinghook.network;

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
	default void send(ServerPlayerEntity player) {
		if (player == null) {
			LOGGER.warn("player is null: " + this.getClass().getName());
		} else {
			player.networkHandler.sendPacket(new CustomPayloadS2CPacket(getIdentifier(), toPacket()));
		}
	}
}
