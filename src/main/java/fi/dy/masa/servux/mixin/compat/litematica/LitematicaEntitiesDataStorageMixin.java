package fi.dy.masa.servux.mixin.compat.litematica;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.data.EntityDataManager;
import fi.dy.masa.litematica.network.ServuxLitematicaHandler;
import fi.dy.masa.malilib.network.IPluginClientPlayHandler;
import fi.dy.masa.servux.network.packet.ServuxLitematicaPacket;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition("litematica"))
@Mixin(value = EntityDataManager.class, remap = false)
public class LitematicaEntitiesDataStorageMixin {
    @Shadow @Final
    private static ServuxLitematicaHandler<ServuxLitematicaPacket.Payload> HANDLER;

    @WrapOperation(method = "receiveServuxMetadata", at = @At(value = "INVOKE", target = "Lfi/dy/masa/litematica/data/DataManager;hasIntegratedServer()Z"))
    private boolean hasIntegratedServer(DataManager instance, Operation<Boolean> original) {
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @WrapOperation(method = "onGameInit", at = @At(value = "INVOKE", target = "Lfi/dy/masa/litematica/network/ServuxLitematicaHandler;registerPlayPayload(Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload$Type;Lnet/minecraft/network/codec/StreamCodec;I)V"))
    private void onGameInit(ServuxLitematicaHandler instance, CustomPacketPayload.Type type, StreamCodec streamCodec, int i, Operation<Void> original) {
        StreamCodec<FriendlyByteBuf, ServuxLitematicaPacket.Payload> servuxCodec = ServuxLitematicaPacket.Payload.CODEC;
        StreamCodec<FriendlyByteBuf, fi.dy.masa.litematica.network.ServuxLitematicaPacket.Payload> litematicaCodec = fi.dy.masa.litematica.network.ServuxLitematicaPacket.Payload.CODEC;
        StreamCodec<FriendlyByteBuf, Object> codec = StreamCodec.of(
                (buf, value) -> {
                    if (value instanceof ServuxLitematicaPacket.Payload p) {
                        servuxCodec.encode(buf, p);
                    } else if (value instanceof fi.dy.masa.litematica.network.ServuxLitematicaPacket.Payload p) {
                        litematicaCodec.encode(buf, p);
                    }
                },
                buf -> {
                    buf.markReaderIndex();
                    int varInt = buf.readVarInt();
                    buf.resetReaderIndex();
                    return switch (varInt) {
                        case 1, 5, 6, 10, 11 -> litematicaCodec.decode(buf);
                        default -> servuxCodec.decode(buf);
                    };
                });
        instance.registerPlayPayload(type, codec, IPluginClientPlayHandler.BOTH_CLIENT);
    }
}
