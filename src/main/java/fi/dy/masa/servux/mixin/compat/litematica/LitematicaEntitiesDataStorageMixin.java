package fi.dy.masa.servux.mixin.compat.litematica;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.litematica.data.DataManager;
import fi.dy.masa.litematica.data.EntitiesDataStorage;
import fi.dy.masa.litematica.network.ServuxLitematicaHandler;
import fi.dy.masa.litematica.network.ServuxLitematicaPacket;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

//@Pseudo
@Restriction(
        require = @Condition("litematica")
)
//@Mixin(targets = "fi.dy.masa.litematica.data.EntitiesDataStorage")
@Mixin(value = EntitiesDataStorage.class, remap = false)
public class LitematicaEntitiesDataStorageMixin {
    @Shadow
    @Final
    private static ServuxLitematicaHandler<ServuxLitematicaPacket.Payload> HANDLER;

    @WrapOperation(method = "receiveServuxMetadata", at = @At(value = "INVOKE", target = "Lfi/dy/masa/litematica/data/DataManager;hasIntegratedServer()Z"))
    private boolean hasIntegratedServer(DataManager instance, Operation<Boolean> original) {
        System.out.println("LitematicaEntitiesDataStorageMixin hasIntegratedServer");
        return false;
    }

    //@WrapOperation(method = "onGameInit", at = @At(value = "INVOKE", target = "Lfi/dy/masa/litematica/network/ServuxLitematicaHandler;registerPlayPayload(Lnet/minecraft/network/packet/CustomPayload$Id;Lnet/minecraft/network/codec/PacketCodec;I)V"))
    private void onGameInit(ServuxLitematicaHandler instance, CustomPayload.Id id, PacketCodec packetCodec, int i, Operation<Void> original) {
        //instance.registerPlayPayload(id, packetCodec, IPluginClientPlayHandler.TO_SERVER);
    }
    /*@Inject(method = "onGameInit", at = @At("HEAD"))
    private void onGameInit(CallbackInfo info) {
        HANDLER.registerPlayPayload(ServuxLitematicaPacket.Payload.ID,ServuxLitematicaPacket.Payload.CODEC, IPluginClientPlayHandler.TO_CLIENT);
    }*/
}