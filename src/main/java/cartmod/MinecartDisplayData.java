package cartmod;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Stores last received server side minecart data
 */
public record MinecartDisplayData(Box lastReceivedPosBox, Vec3d velocity, AbstractMinecartEntity entity) {


    public static MinecartDisplayData withVelocity(AbstractMinecartEntityAccess entity, Vec3d velocity) {
        MinecartDisplayData displayInfo = entity.getDisplayInfo();
        if (displayInfo == null) {
            return new MinecartDisplayData(null, velocity, (AbstractMinecartEntity) entity);
        } else {
            return new MinecartDisplayData(displayInfo.lastReceivedPosBox, velocity, (AbstractMinecartEntity) entity);
        }
    }

    public static MinecartDisplayData withBox(AbstractMinecartEntityAccess entity, Box boxAt) {
        MinecartDisplayData displayInfo = entity.getDisplayInfo();
        if (displayInfo == null) {
            return new MinecartDisplayData(boxAt, null, (AbstractMinecartEntity) entity);
        } else {
            return new MinecartDisplayData(displayInfo.lastReceivedPosBox, displayInfo.velocity, (AbstractMinecartEntity) entity);
        }
    }
}
