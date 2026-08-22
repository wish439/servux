package fi.dy.masa.servux.mixin.compat.minihud;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.malilib.network.IPluginClientPlayHandler;
import fi.dy.masa.minihud.data.EntityDataManager;
import fi.dy.masa.minihud.network.ServuxEntitiesHandler;
import fi.dy.masa.minihud.util.DataStorage;
import fi.dy.masa.servux.network.packet.ServuxEntitiesPacket;
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
@Mixin(EntityDataManager.class)
public class MiniHudEntitiesDataManagerMixin {
    @Shadow @Final
    private static ServuxEntitiesHandler<ServuxEntitiesPacket.Payload> HANDLER;

    @WrapOperation(method = "receiveServuxMetadata", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/util/DataStorage;hasIntegratedServer()Z"))
    private boolean hasIntegratedServer(DataStorage instance, Operation<Boolean> original) {
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @WrapOperation(method = "onGameInit", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/network/ServuxEntitiesHandler;registerPlayPayload(Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload$Type;Lnet/minecraft/network/codec/StreamCodec;I)V"))
    private void onGameInit(ServuxEntitiesHandler instance, CustomPacketPayload.Type type, StreamCodec streamCodec, int i, Operation<Void> original) {
        StreamCodec<FriendlyByteBuf, ServuxEntitiesPacket.Payload> servuxCodec = ServuxEntitiesPacket.Payload.CODEC;
        StreamCodec<FriendlyByteBuf, fi.dy.masa.minihud.network.ServuxEntitiesPacket.Payload> minihudCodec = fi.dy.masa.minihud.network.ServuxEntitiesPacket.Payload.CODEC;
        StreamCodec<FriendlyByteBuf, Object> codec = StreamCodec.of(
                (buf, value) -> {
                    if (value instanceof ServuxEntitiesPacket.Payload p) {
                        servuxCodec.encode(buf, p);
                    } else if (value instanceof fi.dy.masa.minihud.network.ServuxEntitiesPacket.Payload p) {
                        minihudCodec.encode(buf, p);
                    }
                },
                buf -> {
                    buf.markReaderIndex();
                    int varInt = buf.readVarInt();
                    buf.resetReaderIndex();
                    return switch (varInt) {
                        case 1, 5, 6, 10, 11 -> minihudCodec.decode(buf);
                        default -> servuxCodec.decode(buf);
                    };
                });
        instance.registerPlayPayload(type, codec, IPluginClientPlayHandler.BOTH_CLIENT);
    }
}
