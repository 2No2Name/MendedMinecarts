package cartmod;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Box;

public record MinecartDisplayData(Box lastReceivedPosBox, AbstractMinecartEntity entity) {
}
