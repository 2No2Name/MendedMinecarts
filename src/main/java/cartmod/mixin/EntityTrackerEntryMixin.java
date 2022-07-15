package cartmod.mixin;

import cartmod.CartMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.TrackedPosition;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {

    @Shadow @Final private Entity entity;

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/TrackedPosition;getDeltaX(Lnet/minecraft/util/math/Vec3d;)J"
            )
    )
    private long getDeltaX_alwaysTriggerCondition(TrackedPosition instance, Vec3d pos) {
        if (CartMod.ALWAYS_SYNC_CART_POSITION.isEnabled() && this.entity instanceof AbstractMinecartEntity) {
            return Long.MAX_VALUE;
        }
        return instance.getDeltaX(pos);
    }
}
