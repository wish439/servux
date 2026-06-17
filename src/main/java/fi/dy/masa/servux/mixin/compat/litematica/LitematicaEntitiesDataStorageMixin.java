package fi.dy.masa.servux.mixin.compat.litematica;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.data.EntitiesDataStorage;
import fi.dy.masa.litematica.network.ServuxLitematicaHandler;
import fi.dy.masa.malilib.network.IPluginClientPlayHandler;
import fi.dy.masa.servux.network.packet.ServuxLitematicaPacket;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

//@Pseudo
@Restriction(
        require = @Condition("litematica")
)
//@Mixin(targets = "fi.dy.masa.litematica.data.EntitiesDataStorage")
@Mixin(value = EntitiesDataStorage.class, remap = false)
public class LitematicaEntitiesDataStorageMixin {
    @Shadow
    @Final
    private static ServuxLitematicaHandler<ServuxLitematicaPacket.Payload> HANDLER;

    @WrapOperation(method = "receiveServuxMetadata", at = @At(value = "INVOKE", target = "Lfi/dy/masa/litematica/data/DataManager;hasIntegratedServer()Z"))
    private boolean hasIntegratedServer(DataManager instance, Operation<Boolean> original) {
        System.out.println("LitematicaEntitiesDataStorageMixin hasIntegratedServer");
        return false;
    }

    @WrapOperation(method = "onGameInit", at = @At(value = "INVOKE", target = "Lfi/dy/masa/litematica/network/ServuxLitematicaHandler;registerPlayPayload(Lnet/minecraft/network/packet/CustomPayload$Id;Lnet/minecraft/network/codec/PacketCodec;I)V"))
    private void onGameInit(ServuxLitematicaHandler instance, CustomPayload.Id id, PacketCodec packetCodec, int i, Operation<Void> original) {
        PacketCodec<PacketByteBuf, ServuxLitematicaPacket.Payload> servuxCodec = ServuxLitematicaPacket.Payload.CODEC;
        PacketCodec<PacketByteBuf, fi.dy.masa.litematica.network.ServuxLitematicaPacket.Payload> minihudCodec = fi.dy.masa.litematica.network.ServuxLitematicaPacket.Payload.CODEC;
        PacketCodec<PacketByteBuf, Object> codec = PacketCodec.of(
                (value, buf) -> {
                    System.out.println(value.getClass());
                    if (value instanceof ServuxLitematicaPacket.Payload packet) {
                        System.out.println("[LitematicaEntitiesDataStorageMixin]Into the ServuxLitematicaPacket.Payload");
                        servuxCodec.encode(buf, packet);
                    }
                    if (value instanceof fi.dy.masa.litematica.network.ServuxLitematicaPacket.Payload packet) {
                        System.out.println("[LitematicaEntitiesDataStorageMixin]Into the fi.dy.masa.litematica.network.ServuxLitematicaPacket.Payload");
                        minihudCodec.encode(buf, packet);
                    }
                }, buf -> {
                    System.out.println("triggering Litematica decode");
                    //System.out.println("b = " + b);
                    buf.markReaderIndex();
                    int varInt = buf.readVarInt();
                    buf.resetReaderIndex();
                    return switch (varInt) {
                        case 1,5,6,10,11 -> {
                            System.out.println("[LitematicaEntitiesDataStorageMixin] Using minihudCodec");
                            yield minihudCodec.decode(buf);
                        }
                        default -> {
                            System.out.println("[LitematicaEntitiesDataStorageMixin] Using servuxCodec");
                            yield servuxCodec.decode(buf);
                        }
                    };
                    /*if (!b) {
                        System.out.println("Using servuxCodec");
                        return servuxCodec.decode(buf);
                    }
                    System.out.println("Using minihudCodec");
                    return minihudCodec.decode(buf);*/
                    /*try {
                        return servuxCodec.decode(buf);
                    } catch (Exception e) {
                        System.out.println("minihudCodec.decode");
                        return minihudCodec.decode(buf);
                    }*/
                });
        instance.registerPlayPayload(id, codec, IPluginClientPlayHandler.BOTH_CLIENT);
        //instance.registerPlayPayload(id, packetCodec, IPluginClientPlayHandler.TO_SERVER);
    }
    /*@Inject(method = "onGameInit", at = @At("HEAD"))
    private void onGameInit(CallbackInfo info) {
        HANDLER.registerPlayPayload(ServuxLitematicaPacket.Payload.ID,ServuxLitematicaPacket.Payload.CODEC, IPluginClientPlayHandler.TO_CLIENT);
    }*/
}