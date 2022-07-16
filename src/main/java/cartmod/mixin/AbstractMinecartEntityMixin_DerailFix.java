package cartmod.mixin;

import cartmod.AbstractMinecartEntityAccess_Physics;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin_DerailFix extends Entity implements AbstractMinecartEntityAccess_Physics {

    private boolean isMovingOnRail;

    public AbstractMinecartEntityMixin_DerailFix(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(
            method = "moveOnRail",
            at = @At("HEAD")
    )
    public void moveUpdateCachedOnRail0(BlockPos pos, BlockState state, CallbackInfo ci) {
        this.isMovingOnRail = true;
    }
    @Inject(
            method = "moveOnRail",
            at = @At("RETURN")
    )
    public void moveUpdateCachedOnRail1(BlockPos pos, BlockState state, CallbackInfo ci) {
        this.isMovingOnRail = false;
    }

    @Override
    public boolean isSelfMovingOnRail() {
        return this.isMovingOnRail;
    }
}
