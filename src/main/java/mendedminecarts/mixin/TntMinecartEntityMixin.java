package mendedminecarts.mixin;

import mendedminecarts.MendedMinecartsMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.tag.BlockTags;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TntMinecartEntity.class)
public abstract class TntMinecartEntityMixin extends Entity {

    @Shadow
    private int fuseTicks;

    public TntMinecartEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/vehicle/TntMinecartEntity;explode(D)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void setFuseFixBlowupRail(CallbackInfo ci) {
        if (MendedMinecartsMod.EXPLODING_CART_FIX.isEnabled() && this.getBlockStateAtPos().isIn(BlockTags.RAILS)) {
            this.fuseTicks = 0;
        }
    }

    @Inject(
            method = "damage",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/vehicle/TntMinecartEntity;explode(D)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void setFuseFixBlowupRail1(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (MendedMinecartsMod.EXPLODING_CART_FIX.isEnabled() && this.getBlockStateAtPos().isIn(BlockTags.RAILS)) {
            this.fuseTicks = 0;
        }
    }
}
