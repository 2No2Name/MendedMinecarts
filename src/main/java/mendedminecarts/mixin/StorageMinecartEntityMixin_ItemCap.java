package mendedminecarts.mixin;

import mendedminecarts.MendedMinecartsMod;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(StorageMinecartEntity.class)
public abstract class StorageMinecartEntityMixin_ItemCap implements VehicleInventory {

    @Override
    public void setInventoryStack(int slot, ItemStack stack) {
        if (MendedMinecartsMod.NO_CART_ITEM_CAP.isEnabled()) {
            this.generateInventoryLoot(null);
            this.getInventory().set(slot, stack);
        } else {
            //Copypaste from VehicleInventory
            this.generateInventoryLoot(null);
            this.getInventory().set(slot, stack);
            if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
                stack.setCount(this.getMaxCountPerStack());
            }
        }
    }
}
