package cartmod.mixin;

import cartmod.CartMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin_Server extends Entity {

    public AbstractMinecartEntityMixin_Server(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void triggerPositionSending(CallbackInfo ci) {
        if (CartMod.ALWAYS_SEND_CART_POSITION.isEnabled()) {
            this.velocityDirty = true;
        }
    }
}
