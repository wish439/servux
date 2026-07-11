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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition("minihud"))
@Mixin(HudDataManager.class)
public class MiniHudHudDataManagerMixin {
    @WrapOperation(method = "receiveMetadata", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/util/DataStorage;hasIntegratedServer()Z"))
    private boolean hasIntegratedServer(DataStorage instance, Operation<Boolean> original) {
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @WrapOperation(method = "onGameInit", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/network/ServuxHudHandler;registerPlayPayload(Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload$Type;Lnet/minecraft/network/codec/StreamCodec;I)V"))
    private void onGameInit(ServuxHudHandler instance, CustomPacketPayload.Type type, StreamCodec streamCodec, int i, Operation<Void> original) {
        StreamCodec<FriendlyByteBuf, fi.dy.masa.servux.network.packet.ServuxHudPacket.Payload> servuxCodec = fi.dy.masa.servux.network.packet.ServuxHudPacket.Payload.CODEC;
        StreamCodec<FriendlyByteBuf, ServuxHudPacket.Payload> minihudCodec = ServuxHudPacket.Payload.CODEC;
        StreamCodec<FriendlyByteBuf, Object> codec = StreamCodec.of(
                (buf, value) -> {
                    if (value instanceof fi.dy.masa.servux.network.packet.ServuxHudPacket.Payload p) {
                        servuxCodec.encode(buf, p);
                    } else if (value instanceof ServuxHudPacket.Payload p) {
                        minihudCodec.encode(buf, p);
                    }
                },
                buf -> {
                    buf.markReaderIndex();
                    int varInt = buf.readVarInt();
                    buf.resetReaderIndex();
                    return switch (varInt) {
                        case 1, 3, 5, 7, 10, 11 -> minihudCodec.decode(buf);
                        default -> servuxCodec.decode(buf);
                    };
                });
        instance.registerPlayPayload(type, codec, IPluginClientPlayHandler.BOTH_CLIENT);
    }
}
