package cartmod.mixin;

import cartmod.CartMod;
import cartmod.AbstractMinecartEntityAccess_Physics;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Map;

import static net.minecraft.util.math.Direction.*;

@Mixin(RailBlock.class)
public abstract class RailBlockMixin extends AbstractRailBlock {
    @Shadow @Final public static EnumProperty<RailShape> SHAPE;

    private static final Map<Direction, VoxelShape> DIRECTION_2_SHAPE = Util.make(Maps.newEnumMap(Direction.class), map -> {
        map.put(EAST,  VoxelShapes.cuboid(0.99, 0.7, 0.48, 1, 1.2, 0.52));
        map.put(WEST,  VoxelShapes.cuboid(0.0, 0.7, 0.48, 0.01, 1.2, 0.52));
        map.put(SOUTH, VoxelShapes.cuboid(0.48, 0.7, 0.99,0.52, 1.2, 1));
        map.put(NORTH, VoxelShapes.cuboid(0.48, 0.7, 0.0, 0.52, 1.2, 0.01));
    });
    private static final Map<Direction[], VoxelShape> DIRECTIONS_2_SHAPES = new Object2ReferenceOpenHashMap<>();

    private static final Map<RailShape, Direction[]> DERAIL_FIX_WALLS = Util.make(Maps.newEnumMap(RailShape.class), map -> {
        map.put(RailShape.NORTH_WEST, new Direction[]{SOUTH, EAST});
        map.put(RailShape.NORTH_EAST, new Direction[]{SOUTH, WEST});
        map.put(RailShape.SOUTH_EAST, new Direction[]{NORTH, WEST});
        map.put(RailShape.SOUTH_WEST, new Direction[]{NORTH, EAST});
    });

    protected RailBlockMixin(boolean forbidCurves, Settings settings) {
        super(forbidCurves, settings);
    }

    private static VoxelShape getShapeForDirections(Direction[] directions) {
        VoxelShape totalShape = VoxelShapes.empty();
        for (Direction dir : directions) {
            VoxelShape voxelShape = DIRECTION_2_SHAPE.get(dir);
            totalShape = totalShape.isEmpty() ? voxelShape : VoxelShapes.union(totalShape, voxelShape);
        }
        return totalShape;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape railCollisionShape = super.getOutlineShape(state, world, pos, context);
        if (CartMod.DERAILING_CART_FIX.isEnabled()) {
            RailShape railShape = state.get(SHAPE);
            Direction[] derailFixWall = DERAIL_FIX_WALLS.get(railShape);

            if (derailFixWall != null) {
                VoxelShape derailFixShape = DIRECTIONS_2_SHAPES.computeIfAbsent(derailFixWall, RailBlockMixin::getShapeForDirections);
                if (railCollisionShape.isEmpty()) {
                    return derailFixShape;
                }
                return VoxelShapes.union(railCollisionShape, derailFixShape);
            }
        }
        return railCollisionShape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape railCollisionShape = super.getCollisionShape(state, world, pos, context);
        if (CartMod.DERAILING_CART_FIX.isEnabled()) {
            if (context instanceof EntityShapeContextAccessor entityContext) {
                Entity entity = entityContext.getEntity();
                if (entity instanceof AbstractMinecartEntity cart && cart instanceof AbstractMinecartEntityAccess_Physics cartAccess) {
                    if (cartAccess.isSelfMovingOnRail()) {
                        RailShape railShape = state.get(SHAPE);
                        Direction[] derailFixWalls = DERAIL_FIX_WALLS.get(railShape);
                        ArrayList<Direction> selectedWalls = new ArrayList<>();
                        BlockPos cartOffset = cart.getBlockPos().subtract(pos);
                        for (Direction direction : derailFixWalls) {
                            Axis axis = direction.getAxis();
                            int axisOffset = axis.choose(cartOffset.getX(), 0, cartOffset.getZ());
                            if (axisOffset > 0 && direction.getDirection() == AxisDirection.POSITIVE ||
                                    axisOffset < 0 && direction.getDirection() == AxisDirection.NEGATIVE) {
                                selectedWalls.add(direction);
                            }
                        }
                        if (!selectedWalls.isEmpty()) {
                            VoxelShape derailFixShape = DIRECTIONS_2_SHAPES.computeIfAbsent(selectedWalls.toArray(new Direction[0]), RailBlockMixin::getShapeForDirections);
                            if (railCollisionShape.isEmpty()) {
                                return derailFixShape;
                            }
                            return VoxelShapes.union(railCollisionShape, derailFixShape);
                        }
                    }
                }
            }
        }
        return railCollisionShape;
    }
}
