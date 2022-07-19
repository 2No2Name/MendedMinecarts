package mendedminecarts.mixin;

import mendedminecarts.MendedMinecartsMod;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin_ConfiguredSpeed {

    @Shadow protected abstract double getMaxSpeed();

    @Redirect(
            method = "moveOnRail",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;getMaxSpeed()D"
    )
    )
    public double getMaxSpeed(AbstractMinecartEntity instance) {
        if (MendedMinecartsMod.CUSTOM_RAIL_SPEED.isDefault()) {
            return this.getMaxSpeed();
        }
        return this.getMaxSpeed() * (MendedMinecartsMod.CUSTOM_RAIL_SPEED.getState() / (8d / 20d));
    }
}
