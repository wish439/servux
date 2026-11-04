package fi.dy.masa.servux;

import fi.dy.masa.servux.event.ServerInitHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class EventRegistry {
    public static void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ((ServerInitHandler) ServerInitHandler.getInstance()).onServerInit();
        });
    }
}