package mendedminecarts.mixin.boat;

import mendedminecarts.MendedMinecartsMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BoatEntity.class)
public abstract class BoatEntityMixin extends Entity {


    public BoatEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "updateTrackedPositionAndAngles(DDDFFIZ)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void setCartPosLikeOtherEntities(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate, CallbackInfo ci) {
        if (this.getWorld().isClient && (MendedMinecartsMod.ACCURATE_CLIENT_BOATS.isEnabled())) {
            ci.cancel();
            super.updateTrackedPositionAndAngles(x, y, z, yaw, pitch, interpolationSteps, interpolate);
        }
    }


    @Redirect(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/BoatEntity;isLogicalSideForUpdatingMovement()Z"))
    private boolean simulateCartsOnClientLikeOnServer(BoatEntity instance) {
        if (this.getWorld().isClient && (MendedMinecartsMod.ACCURATE_CLIENT_BOATS.isEnabled())) {
            return true;
        } else {
            return instance.isLogicalSideForUpdatingMovement();
        }
    }
}
