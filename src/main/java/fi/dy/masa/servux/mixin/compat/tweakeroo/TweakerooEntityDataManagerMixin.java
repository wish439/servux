package fi.dy.masa.servux.mixin.compat.tweakeroo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.malilib.network.IPluginClientPlayHandler;
import fi.dy.masa.tweakeroo.data.DataManager;
import fi.dy.masa.tweakeroo.data.EntityDataManager;
import fi.dy.masa.tweakeroo.network.ServuxTweaksHandler;
import fi.dy.masa.tweakeroo.network.ServuxTweaksPacket;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(
        require = @Condition("tweakeroo")
)
@Mixin(EntityDataManager.class)
public class TweakerooEntityDataManagerMixin {
    @WrapOperation(method = "receiveServuxMetadata", at = @At(value = "INVOKE", target = "Lfi/dy/masa/tweakeroo/data/DataManager;hasIntegratedServer()Z"))
    private boolean onReceive(DataManager instance, Operation<Boolean> original) {
        return false;
    }
    @WrapOperation(method = "onGameInit", at = @At(value = "INVOKE", target = "Lfi/dy/masa/tweakeroo/network/ServuxTweaksHandler;registerPlayPayload(Lnet/minecraft/network/packet/CustomPayload$Id;Lnet/minecraft/network/codec/PacketCodec;I)V"))
    private void onGameInit(ServuxTweaksHandler instance, CustomPayload.Id id, PacketCodec packetCodec, int i, Operation<Void> original) {
        PacketCodec<PacketByteBuf, ServuxTweaksPacket.Payload> servuxCodec = ServuxTweaksPacket.Payload.CODEC;
        PacketCodec<PacketByteBuf, fi.dy.masa.tweakeroo.network.ServuxTweaksPacket.Payload> minihudCodec = fi.dy.masa.tweakeroo.network.ServuxTweaksPacket.Payload.CODEC;
        PacketCodec<PacketByteBuf, Object> codec = PacketCodec.of(
                (value, buf) -> {
                    System.out.println(value.getClass());
                    if (value instanceof ServuxTweaksPacket.Payload packet) {
                        //System.out.println("Into the ServuxStructuresPacket.Payload");
                        servuxCodec.encode(buf, packet);
                    }
                    if (value instanceof fi.dy.masa.tweakeroo.network.ServuxTweaksPacket.Payload packet) {
                        //System.out.println("Into the fi.dy.masa.minihud.network.ServuxStructuresPacket.Payload");
                        minihudCodec.encode(buf, packet);
                    }
                }, buf -> {
                    System.out.println("triggering tweakeroo decode");
                    //System.out.println("b = " + b);
                    buf.markReaderIndex();
                    int varInt = buf.readVarInt();
                    buf.resetReaderIndex();
                    return switch (varInt) {
                        case 1,5,6,10,11 -> {
                            System.out.println("[TweakerooEntityDataManagerMixin] Using minihudCodec");
                            yield minihudCodec.decode(buf);
                        }
                        default -> {
                            System.out.println("[TweakerooEntityDataManagerMixin] Using servuxCodec");
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