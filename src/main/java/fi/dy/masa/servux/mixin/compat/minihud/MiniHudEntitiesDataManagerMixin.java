package fi.dy.masa.servux.mixin.compat.minihud;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.malilib.network.IPluginClientPlayHandler;
import fi.dy.masa.minihud.data.EntitiesDataManager;
import fi.dy.masa.minihud.network.ServuxEntitiesHandler;
import fi.dy.masa.minihud.util.DataStorage;
import fi.dy.masa.servux.network.packet.ServuxEntitiesPacket;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(
        require = @Condition("minihud")
)
@Mixin(EntitiesDataManager.class)
public class MiniHudEntitiesDataManagerMixin {
    @WrapOperation(method = "receiveServuxMetadata", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/util/DataStorage;hasIntegratedServer()Z"))
    private boolean hasIntegratedServer(DataStorage instance, Operation<Boolean> original) {
        return false;
    }

    @Shadow
    @Final
    private static ServuxEntitiesHandler<fi.dy.masa.servux.network.packet.ServuxEntitiesPacket.Payload> HANDLER;

    @WrapOperation(method = "onGameInit", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/network/ServuxEntitiesHandler;registerPlayPayload(Lnet/minecraft/network/packet/CustomPayload$Id;Lnet/minecraft/network/codec/PacketCodec;I)V"))
    //@Inject(method = "onGameInit", at = @At("TAIL"))
    private void onGameInit(ServuxEntitiesHandler instance, CustomPayload.Id id, PacketCodec packetCodec, int i, Operation<Void> original) {
        PacketCodec<PacketByteBuf, ServuxEntitiesPacket.Payload> servuxCodec = ServuxEntitiesPacket.Payload.CODEC;
        PacketCodec<PacketByteBuf, fi.dy.masa.minihud.network.ServuxEntitiesPacket.Payload> minihudCodec = fi.dy.masa.minihud.network.ServuxEntitiesPacket.Payload.CODEC;
        PacketCodec<PacketByteBuf, Object> codec = PacketCodec.of(
                (value, buf) -> {
                    //System.out.println(value.getClass());
                    if (value instanceof ServuxEntitiesPacket.Payload packet) {
                        System.out.println("[MiniHudEntitiesDataManagerMixin]Into the ServuxEntitiesPacket.Payload");
                        //buf.writeBoolean(true);
                        servuxCodec.encode(buf, packet);
                    }
                    if (value instanceof fi.dy.masa.minihud.network.ServuxEntitiesPacket.Payload packet) {
                        System.out.println("[MiniHudEntitiesDataManagerMixin]Into the fi.dy.masa.minihud.network.ServuxEntitiesPacket.Payload");
                        //buf.writeBoolean(false);
                        minihudCodec.encode(buf, packet);
                    }
                }, buf -> {
                    System.out.println("triggering Entity Data decode");
                    //boolean b = buf.readBoolean();
                    buf.markReaderIndex();
                    int varInt = buf.readVarInt();
                    buf.resetReaderIndex();
                    System.out.println("VarInt:" + varInt);
                    //System.out.println(buf.readableBytes());
                    switch (varInt) {
                        //ServuxEntitiesPacket.Type.PACKET_S2C_METADATA -> 1
                        //ServuxEntitiesPacket.Type.PACKET_S2C_BLOCK_NBT_RESPONSE_SIMPLE -> 5
                        //ServuxEntitiesPacket.Type.PACKET_S2C_ENTITY_NBT_RESPONSE_SIMPLE -> 6
                        //ServuxEntitiesPacket.Type.PACKET_S2C_NBT_RESPONSE_DATA -> 10
                        //ServuxEntitiesPacket.Type.PACKET_S2C_NBT_RESPONSE_START -> 11
                        case 1,5,6,10,11 -> {
                            System.out.println("using minihud codec");
                            return minihudCodec.decode(buf);
                        }
                        default -> {
                            System.out.println("using servux codec");
                            return servuxCodec.decode(buf);
                        }
                    }
                    /*if (!b) {
                        System.out.println("using servux codec");
                        return servuxCodec.decode(buf);
                    }
                    System.out.println("using minihud codec");
                    return minihudCodec.decode(buf);*/
                    /*try {
                        return servuxCodec.decode(buf);
                    } catch (Exception e) {
                        System.out.println("minihudCodec.decode");
                        return minihudCodec.decode(buf);
                    }*/
                });
        instance.registerPlayPayload(id, codec, IPluginClientPlayHandler.BOTH_CLIENT);
        //HANDLER.registerPlayPayload(fi.dy.masa.servux.network.packet.ServuxEntitiesPacket.Payload.ID, fi.dy.masa.servux.network.packet.ServuxEntitiesPacket.Payload.CODEC, IPluginClientPlayHandler.BOTH_CLIENT);
        /*System.out.println("TAIL Triggered");
        System.out.println(((PayloadTypeRegistryImpl)PayloadTypeRegistry.playC2S()).get(Identifier.of("servux", "entity_data")));
        System.out.println(((PayloadTypeRegistryImpl)PayloadTypeRegistry.playS2C()).get(Identifier.of("servux", "entity_data")));*/
    }

    /*@Inject(method = "onGameInit", at = @At("HEAD"))
    private void onGameInitHead(CallbackInfo ci) {
        System.out.println("HEAD Triggered");
        System.out.println(((PayloadTypeRegistryImpl)PayloadTypeRegistry.playC2S()).get(Identifier.of("servux", "entity_data")));
        System.out.println(((PayloadTypeRegistryImpl)PayloadTypeRegistry.playS2C()).get(Identifier.of("servux", "entity_data")));
    }*/
}