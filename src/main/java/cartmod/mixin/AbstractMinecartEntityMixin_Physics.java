package cartmod.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin_Physics extends Entity {

    public AbstractMinecartEntityMixin_Physics(EntityType<?> type, World world) {
        super(type, world);
    }

//    @Redirect(
//            method = "moveOnRail",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"
//            )
//    )
//    private void moveAndSnapBackToRail(AbstractMinecartEntity instance, MovementType movementType, Vec3d vec3d) {
//        BlockPos pos = instance.getBlockPos();
//        instance.move(movementType, vec3d);
//        BlockPos newPos = instance.getBlockPos();
//
//        int yStep = newPos.getY() < pos.getY() ? -1 : 1;
//        for (int y = pos.getY(); y != newPos.getY(); y += yStep) {
//
//        }
//    }


}
