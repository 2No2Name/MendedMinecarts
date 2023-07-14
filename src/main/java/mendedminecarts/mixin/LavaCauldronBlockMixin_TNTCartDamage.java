package mendedminecarts.mixin;

import mendedminecarts.MendedMinecartsMod;
import net.minecraft.block.LavaCauldronBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LavaCauldronBlock.class)
public class LavaCauldronBlockMixin_TNTCartDamage {


    @Redirect(
            method = "onEntityCollision",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setOnFireFromLava()V")
    )
    private void readCustomCount(Entity entity) {
        if (MendedMinecartsMod.LAVA_CAULDRON_KILLS_TNT_CART.isEnabled()) {
            if (entity instanceof TntMinecartEntity tntCart) {
                this.setOnFireFromLava(tntCart);
                return;
            }
        }
        entity.setOnFireFromLava();
    }

    private void setOnFireFromLava(TntMinecartEntity tntCart) {
        if (tntCart.isFireImmune()) {
            return;
        }
        tntCart.setOnFireFor(15);
        if (tntCart.damage(tntCart.getDamageSources().cactus(), 4.0f)) {
            tntCart.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4f, 2.0f + tntCart.getWorld().random.nextFloat() * 0.4f);
        }
    }
}
