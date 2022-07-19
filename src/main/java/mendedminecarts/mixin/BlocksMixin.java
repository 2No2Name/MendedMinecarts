package mendedminecarts.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Blocks.class)
public class BlocksMixin {

    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/RailBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"
            )
    )
    private static AbstractBlock.Settings modifyRailBlockSettings(AbstractBlock.Settings settings) {
        ((AbstractBlockSettingAccessor)settings).setDynamicBounds(true);
        return settings;
    }
    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/PoweredRailBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"
            )
    )
    private static AbstractBlock.Settings modifyRailBlockSettings1(AbstractBlock.Settings settings) {
        ((AbstractBlockSettingAccessor)settings).setDynamicBounds(true);
        return settings;
    }
    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/DetectorRailBlock;<init>(Lnet/minecraft/block/AbstractBlock$Settings;)V"
            )
    )
    private static AbstractBlock.Settings modifyRailBlockSettings2(AbstractBlock.Settings settings) {
        ((AbstractBlockSettingAccessor)settings).setDynamicBounds(true);
        return settings;
    }
}
