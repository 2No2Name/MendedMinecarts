package mendedminecarts.mixin.placement;

import mendedminecarts.MendedMinecartsMod;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.RailShape;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractRailBlock.class)
public abstract class AbstractRailBlockMixin extends Block implements Waterloggable {

    @Shadow @Final public static BooleanProperty WATERLOGGED;

    @Shadow @Final private boolean forbidCurves;

    @Shadow public abstract Property<RailShape> getShapeProperty();

    @Shadow public abstract boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos);

    @Shadow private static native boolean shouldDropRail(BlockPos pos, World world, RailShape shape);

    public AbstractRailBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
    private void getSmartPlacementState(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir) {
        if (MendedMinecartsMod.RAIL_PLACEMENT.isEnabled() && ctx.getPlayer() != null && ctx.getPlayer().isSneaking()) {
            BlockPos blockPos = ctx.getBlockPos();
            boolean waterlogged = ctx.getWorld().getFluidState(blockPos).getFluid() == Fluids.WATER;
            BlockState retBlockState = super.getDefaultState();
            retBlockState = retBlockState.with(WATERLOGGED, waterlogged);

            Direction clickSide = ctx.getSide();

            Direction railDirection = ctx.getHorizontalPlayerFacing();
            //similar logic as placing a top slab for placing a ascending rail
            boolean shouldAscend = clickSide == Direction.DOWN ||
                    (clickSide != Direction.UP && ctx.getHitPos().y - (double) blockPos.getY() > 0.5D);
            boolean shouldCurve = (clickSide == Direction.UP || clickSide == Direction.DOWN);

            boolean isEastWest = railDirection == Direction.EAST || railDirection == Direction.WEST;
            RailShape railShape = isEastWest ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH;
            if (shouldAscend) {
                if (railDirection == Direction.DOWN || railDirection == Direction.UP) {
                    throw new IllegalArgumentException("Horizontal Direction must be horizontal!");
                } else {
                    if (clickSide.getAxis().isHorizontal()) {
                        railDirection = clickSide.getOpposite();
                    }
                    RailShape railShape1 = null;
                    if (railDirection == Direction.NORTH) {
                        railShape1 = RailShape.ASCENDING_NORTH;
                    } else if (railDirection == Direction.SOUTH) {
                        railShape1 = RailShape.ASCENDING_SOUTH;
                    } else if (railDirection == Direction.WEST) {
                        railShape1 = RailShape.ASCENDING_WEST;
                    } else if (railDirection == Direction.EAST) {
                        railShape1 = RailShape.ASCENDING_EAST;
                    }
                    if (railShape1 != null && !shouldDropRail(blockPos, ctx.getWorld(), railShape1)) {
                        railShape = railShape1;
                    }
                }
            } else if (shouldCurve && !this.forbidCurves) {
                boolean positiveX = (ctx.getHitPos().x - (double) blockPos.getX() > 11D/16D);
                boolean positiveZ = (ctx.getHitPos().z - (double) blockPos.getZ() > 11D/16D);
                boolean negativeX = (ctx.getHitPos().x - (double) blockPos.getX() < 5D/16D);
                boolean negativeZ = (ctx.getHitPos().z - (double) blockPos.getZ() < 5D/16D);
                //SOUTH AND EAST are positive. SOUTH is Z axis, EAST is X
                if (positiveX && positiveZ) {
                    railShape = RailShape.SOUTH_EAST;
                } else if (positiveX && negativeZ) {
                    railShape = RailShape.NORTH_EAST;
                } else if (negativeX && positiveZ) {
                    railShape = RailShape.SOUTH_WEST;
                } else if (negativeX && negativeZ) {
                    railShape = RailShape.NORTH_WEST;
                }
            }
            retBlockState = retBlockState.with(this.getShapeProperty(), railShape);
            cir.setReturnValue(retBlockState);
        }
    }
}
