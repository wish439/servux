package fi.dy.masa.servux.mixin.world;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fi.dy.masa.servux.util.WorldUtils;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(WorldChunk.class)
public abstract class MixinWorldChunk_UpdateSuppression
{
    @WrapOperation(method = "setBlockState",
                slice = @Slice(from = @At(value = "INVOKE",
                                target = "Lnet/minecraft/world/chunk/ChunkSection;getBlockState(III)" +
                                          "Lnet/minecraft/block/BlockState;")),
                at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isClient()Z", ordinal = 0))
    private boolean servux_redirectIsRemote(World world, Operation<Boolean> original)
    {
        return WorldUtils.shouldPreventBlockUpdates(world) || original.call(world);
    }
}
