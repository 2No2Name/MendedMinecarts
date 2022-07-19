package mendedminecarts.mixin;

import mendedminecarts.MendedMinecartsMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
import net.minecraft.server.network.EntityTrackerEntry;
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
//                    target = "Lnet/minecraft/entity/TrackedPosition;getDeltaX(Lnet/minecraft/util/math/Vec3d;)J" //1.19
                    target = "Lnet/minecraft/network/packet/s2c/play/EntityS2CPacket;encodePacketCoordinate(D)J" //1.18
            )
    )
    private long getDeltaX_alwaysTriggerCondition(double coord) {
        if (MendedMinecartsMod.ALWAYS_SYNC_CART_POSITION.isEnabled() && this.entity instanceof AbstractMinecartEntity) {
            this.entity.velocityModified = true;
            return Long.MAX_VALUE;
        }
//        return instance.getDeltaX(pos); 1.19
        return EntityS2CPacket.encodePacketCoordinate(coord);
    }
}
