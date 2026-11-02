package fi.dy.masa.servux;

import fi.dy.masa.servux.event.ServerInitHandler;
import fi.dy.masa.servux.network.packet.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class EventRegistry {
    public static void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ((ServerInitHandler) ServerInitHandler.getInstance()).onServerInit();
        });
    }
}