package fi.dy.masa.servux.mixin.compat.minihud;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.malilib.network.IPluginClientPlayHandler;
import fi.dy.masa.minihud.network.ServuxStructuresHandler;
import fi.dy.masa.minihud.util.DataStorage;
import fi.dy.masa.servux.network.packet.ServuxStructuresPacket;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;


//First, you ought to understand a truth, this mixin only injected when servux and minihud are exist.
@Restriction(
        require = @Condition("minihud")
)
@Mixin(DataStorage.class)
public class MiniHudDataStorageMixin {
    @Shadow
    @Final
    private static ServuxStructuresHandler<ServuxStructuresPacket.Payload> HANDLER;

    @Shadow
    private IntegratedServer integratedServer;

    @WrapOperation(method = "receiveServuxStrucutresMetadata", at = @At(value = "FIELD", target = "Lfi/dy/masa/minihud/util/DataStorage;hasIntegratedServer:Z"))
    private boolean onReceive(DataStorage instance, Operation<Boolean> original) {
        return false;
    }
    @WrapOperation(method = "onGameInit", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/network/ServuxStructuresHandler;registerPlayPayload(Lnet/minecraft/network/packet/CustomPayload$Id;Lnet/minecraft/network/codec/PacketCodec;I)V"))
    private void onGameInit(ServuxStructuresHandler instance, CustomPayload.Id id, PacketCodec packetCodec, int i, Operation<Void> original) {
        //PacketCodec<PacketByteBuf, Either<ServuxStructuresPacket.Payload, fi.dy.masa.minihud.network.ServuxStructuresPacket.Payload>> either = PacketCodecs.either(ServuxStructuresPacket.Payload.CODEC, fi.dy.masa.minihud.network.ServuxStructuresPacket.Payload.CODEC);
        PacketCodec<PacketByteBuf, ServuxStructuresPacket.Payload> servuxCodec = ServuxStructuresPacket.Payload.CODEC;
        PacketCodec<PacketByteBuf, fi.dy.masa.minihud.network.ServuxStructuresPacket.Payload> minihudCodec = fi.dy.masa.minihud.network.ServuxStructuresPacket.Payload.CODEC;
        PacketCodec<PacketByteBuf, Object> codec = PacketCodec.of(
                (value, buf) -> {
                    System.out.println(value.getClass());
                    if (value instanceof ServuxStructuresPacket.Payload packet) {
                        System.out.println("[MiniHudDataStorageMixin]Into the ServuxStructuresPacket.Payload");
                        servuxCodec.encode(buf, packet);
                    }
                    if (value instanceof fi.dy.masa.minihud.network.ServuxStructuresPacket.Payload packet) {
                        System.out.println("[MiniHudDataStorageMixin]Into the fi.dy.masa.minihud.network.ServuxStructuresPacket.Payload");
                        minihudCodec.encode(buf, packet);
                    }
                }, buf -> {
                    System.out.println("triggering structure decode");
                    //System.out.println("b = " + b);
                    buf.markReaderIndex();
                    int varInt = buf.readVarInt();
                    buf.resetReaderIndex();
                    return switch (varInt) {
                        case 1,2,5,10,12 -> {
                            System.out.println("[MiniHudDataStorageMixin] Using minihudCodec");
                            yield minihudCodec.decode(buf);
                        }
                        default -> {
                            System.out.println("[MiniHudDataStorageMixin] Using servuxCodec");
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