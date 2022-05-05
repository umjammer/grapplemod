package com.yyon.grapplinghook.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;


public interface BaseMessageServer {

	/** id */
	Identifier getIdentifier();

	/** serialize */
	PacketByteBuf toPacket();

	/** to server */
	default void send() {
		MinecraftClient.getInstance().getNetworkHandler().getConnection().send(new CustomPayloadC2SPacket(getIdentifier(), toPacket()));
	}
}
