package fi.dy.masa.servux.mixin.compat.minihud;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.malilib.network.IPluginClientPlayHandler;
import fi.dy.masa.minihud.data.HudDataManager;
import fi.dy.masa.minihud.network.ServuxHudHandler;
import fi.dy.masa.minihud.network.ServuxHudPacket;
import fi.dy.masa.minihud.util.DataStorage;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Arrays;

@Restriction(
        require = @Condition("minihud")
)
@Mixin(HudDataManager.class)
public class MiniHudHudDataManagerMixin {
    @WrapOperation(method = "receiveMetadata", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/util/DataStorage;hasIntegratedServer()Z"))
    private boolean hasIntegratedServer(DataStorage instance, Operation<Boolean> original) {
        return false;
    }
    @WrapOperation(method = "onGameInit", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/network/ServuxHudHandler;registerPlayPayload(Lnet/minecraft/network/packet/CustomPayload$Id;Lnet/minecraft/network/codec/PacketCodec;I)V"))
    private void onGameInit(ServuxHudHandler instance, CustomPayload.Id id, PacketCodec packetCodec, int i, Operation<Void> original) {
        PacketCodec<PacketByteBuf, fi.dy.masa.servux.network.packet.ServuxHudPacket.Payload> servuxCodec = fi.dy.masa.servux.network.packet.ServuxHudPacket.Payload.CODEC;
        PacketCodec<PacketByteBuf, ServuxHudPacket.Payload> minihudCodec = ServuxHudPacket.Payload.CODEC;
        PacketCodec<PacketByteBuf, Object> codec = PacketCodec.of(
                (value, buf) -> {
                    System.out.println(value.getClass());
                    if (value instanceof fi.dy.masa.servux.network.packet.ServuxHudPacket.Payload packet) {
                        System.out.println("[MiniHudHudDataManagerMixin]Into the ServuxHudPacket.Payload");
                        servuxCodec.encode(buf, packet);
                    }
                    if (value instanceof ServuxHudPacket.Payload packet) {
                        System.out.println("[MiniHudHudDataManagerMixin]Into the fi.dy.masa.minihud.network.ServuxHudPacket.Payload");
                        minihudCodec.encode(buf, packet);
                    }
                }, buf -> {
                    System.out.println("triggering Hud decode");
                    //System.out.println("b = " + b);
                    /*if (buf.readableBytes() <= 0) {
                        return servuxCodec.decode(buf);
                    }*/
                    buf.markReaderIndex();
                    int varInt = buf.readVarInt();
                    buf.resetReaderIndex();
                    return switch (varInt) {
                        case 1,3,5,7,10,11 -> {
                            System.out.println("[MiniHudHudDataManagerMixin] Using minihudCodec");
                            yield minihudCodec.decode(buf);
                        }
                        default -> {
                            System.out.println("[MiniHudHudDataManagerMixin] Using servuxCodec");
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