package mendedminecarts.mixin;

import mendedminecarts.AbstractMinecartEntityAccess;
import mendedminecarts.MendedMinecartsMod;
import mendedminecarts.MinecartDisplayData;
import net.minecraft.block.BlockState;
import net.minecraft.block.HopperBlock;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperMinecartEntity.class)
public class HopperMinecartEntityMixin {

    @Inject(
            method = "getDefaultContainedBlock", at = @At("RETURN"), cancellable = true
    )
    private void displayLockedHopperWhenLocked(CallbackInfoReturnable<BlockState> cir) {
        if (MendedMinecartsMod.VISUAL_HOPPER_CART_LOCKING.isEnabled() && this instanceof AbstractMinecartEntityAccess entityAccess) {
            MinecartDisplayData displayInfo = entityAccess.getDisplayInfo();
            if (displayInfo == null) {
                return;
            }
            if (displayInfo.hopperLocked()) {
                BlockState disabledHopper = cir.getReturnValue();
                disabledHopper = disabledHopper.withIfExists(HopperBlock.ENABLED, false);
                cir.setReturnValue(disabledHopper);
            }

        }
    }

    @Inject(
            method = "getDefaultBlockOffset", at = @At("RETURN"), cancellable = true
    )
    private void displayHopperLowerWhenLocked(CallbackInfoReturnable<Integer> cir) {
        if (MendedMinecartsMod.VISUAL_HOPPER_CART_LOCKING.isEnabled() && this instanceof AbstractMinecartEntityAccess entityAccess) {
            MinecartDisplayData displayInfo = entityAccess.getDisplayInfo();
            if (displayInfo == null) {
                return;
            }
            if (displayInfo.hopperLocked()) {
                cir.setReturnValue(-1);
            }

        }
    }
}
