package com.yyon.grapplinghook.client;

import com.yyon.grapplinghook.config.GrappleConfig;
import net.minecraft.client.MinecraftClient;


/**
 * @forge.event LoggedOutEvent
 */
public class ClientStoppingEvent {

    public void onClientStopping(MinecraftClient client) {
        GrappleConfig.setServerOptions(null);
    }
}