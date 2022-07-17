package cartmod;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;

public class CartHelper {


    public static double getMaxSpeed(AbstractMinecartEntity entity) {
        if (entity instanceof FurnaceMinecartEntity) {
            return (entity.isTouchingWater() ? 3.0 : 4.0) / 20.0;
        }
        return (entity.isTouchingWater() ? 4.0 : 8.0) / 20.0;
    }
}
