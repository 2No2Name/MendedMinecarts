package mendedminecarts.mixin;

import mendedminecarts.MendedMinecartsMod;
import mendedminecarts.RailHitboxHelper;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityChangeListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin_YawFix extends Entity {

    public AbstractMinecartEntityMixin_YawFix(EntityType<?> type, World world) {
        super(type, world);
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;setRotation(FF)V",
                    ordinal = 2
            )
    )
    private void setRotationBetter(AbstractMinecartEntity instance, float yaw, float pitch) {
        if (MendedMinecartsMod.ROTATE_CART_TO_RAIL.isEnabled()) {
            if (this.fixRotationToRail(yaw, pitch)) {
                return;
            }
        }
        this.setRotation(yaw, pitch);
    }

    private boolean fixRotationToRail(float yaw, float pitch) {
        BlockState blockState = this.world.getBlockState(this.getBlockPos());
        if (blockState.getBlock() instanceof AbstractRailBlock railBlock) {
            RailShape railShape = blockState.get(railBlock.getShapeProperty());
            Direction.Axis railAxis = RailHitboxHelper.getRailAxis(railShape);
            if (railAxis != null) {
                int yawRounded90 = (Math.round(yaw / 90f) & 3);
                if (yawRounded90 == 0 || yawRounded90 == 2) {
                    if (railAxis == Direction.Axis.Z) {
                        yawRounded90++;
                    }
                } else {
                    if (railAxis == Direction.Axis.X) {
                        yawRounded90--;
                    }
                }
                yaw = yawRounded90 * 90f;

                this.setRotation(yaw, pitch);
                return true;
            }
        }
        return false;
    }

    @Override
    public void setChangeListener(EntityChangeListener changeListener) {
        super.setChangeListener(changeListener);
        if (MendedMinecartsMod.ROTATE_CART_TO_RAIL.isEnabled()) {
            this.fixRotationToRail(this.getYaw(), this.getPitch());
        }
    }
}
