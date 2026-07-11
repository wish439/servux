package fi.dy.masa.servux.mixin.compat.minihud;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.malilib.network.IPluginClientPlayHandler;
import fi.dy.masa.minihud.data.DebugDataManager;
import fi.dy.masa.minihud.network.ServuxDebugHandler;
import fi.dy.masa.minihud.util.DataStorage;
import fi.dy.masa.servux.network.packet.ServuxDebugPacket;
import fi.dy.masa.servux.network.packet.ServuxStructuresPacket;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(
        require = @Condition("minihud")
)
@Mixin(DebugDataManager.class)
public class MiniHudDebugDataManagerMixin {
    @WrapOperation(method = "receiveMetadata", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/util/DataStorage;hasIntegratedServer()Z"))
    private boolean hasIntegratedServer(DataStorage instance, Operation<Boolean> original) {
        return false;
    }

    @WrapOperation(method = "onGameInit", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/network/ServuxDebugHandler;registerPlayPayload(Lnet/minecraft/network/packet/CustomPayload$Id;Lnet/minecraft/network/codec/PacketCodec;I)V"))
    private void onGameInit(ServuxDebugHandler instance, CustomPayload.Id id, PacketCodec packetCodec, int i, Operation<Void> original) {
        PacketCodec<PacketByteBuf, ServuxDebugPacket.Payload> servuxCodec = ServuxDebugPacket.Payload.CODEC;
        PacketCodec<PacketByteBuf, fi.dy.masa.minihud.network.ServuxDebugPacket.Payload> minihudCodec = fi.dy.masa.minihud.network.ServuxDebugPacket.Payload.CODEC;
        PacketCodec<PacketByteBuf, Object> codec = PacketCodec.of(
                (value, buf) -> {
                    System.out.println(value.getClass());
                    if (value instanceof ServuxDebugPacket.Payload packet) {
                        System.out.println("[MiniHudDebugDataManagerMixin]Into the ServuxDebugPacket.Payload");
                        servuxCodec.encode(buf, packet);
                    }
                    if (value instanceof fi.dy.masa.minihud.network.ServuxDebugPacket.Payload packet) {
                        System.out.println("[MiniHudDebugDataManagerMixin]Into the fi.dy.masa.minihud.network.ServuxDebugPacket.Payload");
                        minihudCodec.encode(buf, packet);
                    }
                }, buf -> {
                    System.out.println("triggering structure decode");
                    //System.out.println("b = " + b);
                    buf.markReaderIndex();
                    int varInt = buf.readVarInt();
                    buf.resetReaderIndex();
                    return switch (varInt) {
                        case 1,10,11 -> {
                            System.out.println("[MiniHudDebugDataManagerMixin] Using minihudCodec");
                            yield minihudCodec.decode(buf);
                        }
                        default -> {
                            System.out.println("[MiniHudDebugDataManagerMixin] Using servuxCodec");
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
    }
}