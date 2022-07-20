package mendedminecarts.mixin;

import mendedminecarts.MendedMinecartsMod;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

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

    @ModifyConstant(method = "moveOnRail", constant = @Constant(doubleValue = 2.0, ordinal = 0))
    private double scaleSpeedLimit(double original) {
        if (MendedMinecartsMod.CUSTOM_RAIL_SPEED.isDefault()) {
            return original;
        }
        return original * (MendedMinecartsMod.CUSTOM_RAIL_SPEED.getState() / (8d / 20d));
    }

    //Fix for super acceleration:
    @ModifyVariable(
            method = "moveOnRail",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;setVelocity(DDD)V", ordinal = 0)
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            ),
            ordinal = 0
    )
    private int clampXDistance(int x, BlockPos pos) {
        int difference = MathHelper.clamp(x - pos.getX(), -1, 1);
        return pos.getX() + difference;
    }

    @ModifyVariable(
            method = "moveOnRail",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;setVelocity(DDD)V", ordinal = 0)
            ),
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;getVelocity()Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            ),
            ordinal = 1
    )
    private int clampZDistance(int z, BlockPos pos) {
        int difference = MathHelper.clamp(z - pos.getZ(), -1, 1);
        return pos.getZ() + difference;
    }
}
