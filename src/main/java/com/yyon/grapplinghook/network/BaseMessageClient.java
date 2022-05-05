package com.yyon.grapplinghook.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;


public interface BaseMessageClient {

	/** id */
	Identifier getIdentifier();

	/** serialize */
	PacketByteBuf toPacket();

	/** to client */
	default void send(ServerPlayerEntity player) {
		player.networkHandler.sendPacket(new CustomPayloadS2CPacket(getIdentifier(), toPacket()));
	}
}
