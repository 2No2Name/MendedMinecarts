package mendedminecarts;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;

public class CartHelper {


    public static double getMaxSpeed(AbstractMinecartEntity entity) {
        double maxSpeed;
        if (entity instanceof FurnaceMinecartEntity) {
            maxSpeed = (entity.isTouchingWater() ? 3.0 : 4.0) / 20.0;
        } else {
            maxSpeed = (entity.isTouchingWater() ? 4.0 : 8.0) / 20.0;
        }
        if (MendedMinecartsMod.CART_SPEED.isDefault()) {
            return maxSpeed;
        }
        return maxSpeed * (MendedMinecartsMod.CART_SPEED.getState() / (8d / 20d));
    }
}
