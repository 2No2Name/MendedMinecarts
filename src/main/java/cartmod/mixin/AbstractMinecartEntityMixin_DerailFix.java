package cartmod.mixin;

import cartmod.AbstractMinecartEntityAccess_Physics;
import cartmod.RailHitboxHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin_DerailFix extends Entity implements AbstractMinecartEntityAccess_Physics {

    @Shadow protected abstract float getVelocityMultiplier();

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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;applySlowdown()V", shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void fixVelocityLoss(BlockPos previousPos, BlockState state, CallbackInfo ci, double d, double e, double f, Vec3d vec3d, boolean bl, boolean bl2, double g, Vec3d previousVelocity, RailShape prevPosRailShape) {
        if (this.getBlockPos().equals(previousPos)) {
            return;
        }
        boolean hasHitWall = false;
        Vec3d velocity = this.getVelocity();
        if (velocity.x == 0 && Math.abs(previousVelocity.x) > 0.5) {
            velocity = velocity.withAxis(Direction.Axis.X, previousVelocity.x * this.getVelocityMultiplier());
            hasHitWall = true;
        }
        if (velocity.z == 0 && Math.abs(previousVelocity.z) > 0.5) {
            velocity = velocity.withAxis(Direction.Axis.Z, previousVelocity.z * this.getVelocityMultiplier());
            hasHitWall = true;
        }
        if (!hasHitWall) {
            return;
        }
        BlockState blockState = this.world.getBlockState(this.getBlockPos());
        if (blockState.isOf(Blocks.RAIL)) {
            this.setVelocity(velocity);
        }
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
