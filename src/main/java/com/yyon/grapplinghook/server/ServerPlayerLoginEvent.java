package com.yyon.grapplinghook.server;

import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.network.LoggedInMessage;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @forge.event PlayerLoggedInEvent
 */
public class ServerPlayerLoginEvent {

    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        new LoggedInMessage(GrappleConfig.getConf()).send(handler.player);
    }
}
