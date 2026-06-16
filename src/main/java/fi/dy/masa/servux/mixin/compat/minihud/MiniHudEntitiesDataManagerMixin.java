package fi.dy.masa.servux.mixin.compat.minihud;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.minihud.data.EntitiesDataManager;
import fi.dy.masa.minihud.network.ServuxEntitiesHandler;
import fi.dy.masa.minihud.util.DataStorage;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.impl.networking.PayloadTypeRegistryImpl;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(
        require = @Condition("minihud")
)
@Mixin(EntitiesDataManager.class)
public class MiniHudEntitiesDataManagerMixin {
    @WrapOperation(method = "receiveServuxMetadata", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/util/DataStorage;hasIntegratedServer()Z"))
    private boolean hasIntegratedServer(DataStorage instance, Operation<Boolean> original) {
        return false;
    }

    //@WrapOperation(method = "onGameInit", at = @At(value = "INVOKE", target = "Lfi/dy/masa/minihud/network/ServuxEntitiesHandler;registerPlayPayload(Lnet/minecraft/network/packet/CustomPayload$Id;Lnet/minecraft/network/codec/PacketCodec;I)V"))
    @Inject(method = "onGameInit", at = @At("TAIL"))
    private void onGameInit(CallbackInfo ci) {
        System.out.println("TAIL Triggered");
        System.out.println(((PayloadTypeRegistryImpl)PayloadTypeRegistry.playC2S()).get(Identifier.of("servux", "entity_data")));
        System.out.println(((PayloadTypeRegistryImpl)PayloadTypeRegistry.playS2C()).get(Identifier.of("servux", "entity_data")));
    }
}