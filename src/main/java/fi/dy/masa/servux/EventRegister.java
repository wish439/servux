package fi.dy.masa.servux;

import fi.dy.masa.servux.event.ServerInitHandler;
import fi.dy.masa.servux.network.packet.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class EventRegister {
    public static void registerEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            registerHandlers();
            ((ServerInitHandler) ServerInitHandler.getInstance()).onServerInit();
        });


    }
    private static void registerHandlers() {
         ServuxDebugHandler.setINSTANCE(new ServuxDebugHandler<>() {
             @Override
             public void receive(ServuxDebugPacket.Payload payload, ServerPlayNetworking.Context context)
             {
                 ServuxDebugHandler.getInstance().receivePlayPayload(payload, context);
             }
         });
        ServuxEntitiesHandler.setINSTANCE(new ServuxEntitiesHandler<>() {
            @Override
            public void receive(ServuxEntitiesPacket.Payload payload, ServerPlayNetworking.Context context)
            {
                ServuxEntitiesHandler.getInstance().receivePlayPayload(payload, context);
            }
        });
        ServuxHudHandler.setINSTANCE(new ServuxHudHandler<>() {
            @Override
            public void receive(ServuxHudPacket.Payload payload, ServerPlayNetworking.Context context)
            {
                ServuxHudHandler.getInstance().receivePlayPayload(payload, context);
            }
        });
        ServuxLitematicaHandler.setINSTANCE(new ServuxLitematicaHandler<>() {
            @Override
            public void receive(ServuxLitematicaPacket.Payload payload, ServerPlayNetworking.Context context)
            {
                ServuxLitematicaHandler.getInstance().receivePlayPayload(payload, context);
            }
        });

        ServuxStructuresHandler.setINSTANCE( new ServuxStructuresHandler<>() {
            @Override
            public void receive(ServuxStructuresPacket.Payload payload, ServerPlayNetworking.Context context)
            {
                ServuxStructuresHandler.getInstance().receivePlayPayload(payload, context);
            }
        });

        ServuxTweaksHandler.setINSTANCE(new ServuxTweaksHandler<>() {
            @Override
            public void receive(ServuxTweaksPacket.Payload payload, ServerPlayNetworking.Context context)
            {
                ServuxTweaksHandler.getInstance().receivePlayPayload(payload, context);
            }
        });
    }
}
