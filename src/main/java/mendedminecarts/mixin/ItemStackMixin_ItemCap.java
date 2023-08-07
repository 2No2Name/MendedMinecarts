package mendedminecarts.mixin;

import mendedminecarts.MendedMinecartsMod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin_ItemCap {

    @Shadow private int count;

    @Inject(
            method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V",
            at = @At("RETURN")
    )
    private void readCustomCount(NbtCompound nbt, CallbackInfo ci) {
        if (MendedMinecartsMod.NO_CART_ITEM_CAP.isEnabled()) {
            if (nbt.contains("CustomCount")) {
                this.count = nbt.getInt("CustomCount");
            }
        }
    }
    @Inject(
            method = "writeNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/nbt/NbtCompound;",
            at = @At("RETURN")
    )
    private void writeCustomCount(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        if (MendedMinecartsMod.NO_CART_ITEM_CAP.isEnabled()) {
            if (this.count != (byte)this.count) {
                nbt.putInt("CustomCount", this.count);
            }
        }
    }
}
