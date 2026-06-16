package fi.dy.masa.servux.mixin.compat.minihud;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.minihud.data.DebugDataManager;
import fi.dy.masa.minihud.network.ServuxDebugHandler;
import fi.dy.masa.minihud.util.DataStorage;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(
        require = @Condition("minihud")
)
@Mixin(DebugDataManager.class)
public class MiniHudDebugDataManagerMixin {
    //@WrapOperation(method = "receiveMetadata", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/util/DataStorage;hasIntegratedServer()Z"))
    private boolean hasIntegratedServer(DataStorage instance, Operation<Boolean> original) {
        return false;
    }

    //@WrapOperation(method = "onGameInit", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/network/ServuxDebugHandler;registerPlayPayload(Lnet/minecraft/network/packet/CustomPayload$Id;Lnet/minecraft/network/codec/PacketCodec;I)V"))
    private void onGameInit(ServuxDebugHandler instance, CustomPayload.Id id, PacketCodec packetCodec, int i, Operation<Void> original) {

    }
}