package com.yyon.grapplinghook.server;

import com.yyon.grapplinghook.config.GrappleConfig;
import net.minecraft.server.MinecraftServer;

/**
 * @forge.event ServerStartedEvent
 */
public class ServerStartEvent {

    public void onServerStarted(MinecraftServer server) {
        if (GrappleConfig.getConf().other.override_allowflight) {
            server.setFlightEnabled(true);
        }
    }
}
