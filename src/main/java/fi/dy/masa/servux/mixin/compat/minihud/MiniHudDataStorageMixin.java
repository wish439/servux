package fi.dy.masa.servux.mixin.compat.minihud;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.malilib.network.IPluginClientPlayHandler;
import fi.dy.masa.minihud.network.ServuxStructuresHandler;
import fi.dy.masa.minihud.util.DataStorage;
import fi.dy.masa.servux.network.packet.ServuxStructuresPacket;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition("minihud"))
@Mixin(DataStorage.class)
public class MiniHudDataStorageMixin {
    @Shadow @Final
    private static ServuxStructuresHandler<ServuxStructuresPacket.Payload> HANDLER;

    @WrapOperation(method = "receiveServuxStrucutresMetadata", at = @At(value = "FIELD", target = "Lfi/dy/masa/minihud/util/DataStorage;hasIntegratedServer:Z"))
    private boolean onReceive(DataStorage instance, Operation<Boolean> original) {
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @WrapOperation(method = "onGameInit", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/network/ServuxStructuresHandler;registerPlayPayload(Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload$Type;Lnet/minecraft/network/codec/StreamCodec;I)V"))
    private void onGameInit(ServuxStructuresHandler instance, CustomPacketPayload.Type type, StreamCodec streamCodec, int i, Operation<Void> original) {
        StreamCodec<FriendlyByteBuf, ServuxStructuresPacket.Payload> servuxCodec = ServuxStructuresPacket.Payload.CODEC;
        StreamCodec<FriendlyByteBuf, fi.dy.masa.minihud.network.ServuxStructuresPacket.Payload> minihudCodec = fi.dy.masa.minihud.network.ServuxStructuresPacket.Payload.CODEC;
        StreamCodec<FriendlyByteBuf, Object> codec = StreamCodec.of(
                (buf, value) -> {
                    if (value instanceof ServuxStructuresPacket.Payload p) {
                        servuxCodec.encode(buf, p);
                    } else if (value instanceof fi.dy.masa.minihud.network.ServuxStructuresPacket.Payload p) {
                        minihudCodec.encode(buf, p);
                    }
                },
                buf -> {
                    buf.markReaderIndex();
                    int varInt = buf.readVarInt();
                    buf.resetReaderIndex();
                    return switch (varInt) {
                        case 1, 2, 5, 10, 12 -> minihudCodec.decode(buf);
                        default -> servuxCodec.decode(buf);
                    };
                });
        instance.registerPlayPayload(type, codec, IPluginClientPlayHandler.BOTH_CLIENT);
    }
}
