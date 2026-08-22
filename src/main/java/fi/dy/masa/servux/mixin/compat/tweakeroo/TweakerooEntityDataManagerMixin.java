package fi.dy.masa.servux.mixin.compat.tweakeroo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.malilib.network.IPluginClientPlayHandler;
import fi.dy.masa.servux.network.packet.ServuxTweaksPacket;
import fi.dy.masa.tweakeroo.data.DataManager;
import fi.dy.masa.tweakeroo.data.EntityDataManager;
import fi.dy.masa.tweakeroo.network.ServuxTweaksHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition("tweakeroo"))
@Mixin(EntityDataManager.class)
public class TweakerooEntityDataManagerMixin {
    @WrapOperation(method = "receiveServuxMetadata(Lfi/dy/masa/malilib/util/data/tag/CompoundData;)Z", at = @At(value = "INVOKE", target = "Lfi/dy/masa/tweakeroo/data/DataManager;hasIntegratedServer()Z"))
    private boolean onReceive(DataManager instance, Operation<Boolean> original) {
        return false;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @WrapOperation(method = "onGameInit", at = @At(value = "INVOKE", target = "Lfi/dy/masa/tweakeroo/network/ServuxTweaksHandler;registerPlayPayload(Lnet/minecraft/network/protocol/common/custom/CustomPacketPayload$Type;Lnet/minecraft/network/codec/StreamCodec;I)V"))
    private void onGameInit(ServuxTweaksHandler instance, CustomPacketPayload.Type type, StreamCodec streamCodec, int i, Operation<Void> original) {
        StreamCodec<FriendlyByteBuf, ServuxTweaksPacket.Payload> servuxCodec = ServuxTweaksPacket.Payload.CODEC;
        StreamCodec<FriendlyByteBuf, fi.dy.masa.tweakeroo.network.ServuxTweaksPacket.Payload> tweakerooCodec = fi.dy.masa.tweakeroo.network.ServuxTweaksPacket.Payload.CODEC;
        StreamCodec<FriendlyByteBuf, Object> codec = StreamCodec.of(
                (buf, value) -> {
                    if (value instanceof ServuxTweaksPacket.Payload p) {
                        servuxCodec.encode(buf, p);
                    } else if (value instanceof fi.dy.masa.tweakeroo.network.ServuxTweaksPacket.Payload p) {
                        tweakerooCodec.encode(buf, p);
                    }
                },
                buf -> {
                    buf.markReaderIndex();
                    int varInt = buf.readVarInt();
                    buf.resetReaderIndex();
                    return switch (varInt) {
                        case 1, 5, 6, 10, 11 -> tweakerooCodec.decode(buf);
                        default -> servuxCodec.decode(buf);
                    };
                });
        instance.registerPlayPayload(type, codec, IPluginClientPlayHandler.BOTH_CLIENT);
    }
}
