package cartmod;

import cartmod.mixin.EntityShapeContextAccessor;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static net.minecraft.util.math.Direction.*;

public class RailHitboxHelper {
    public static final Map<Direction, VoxelShape> DIRECTION_2_SHAPE = Util.make(Maps.newEnumMap(Direction.class), map -> {
        map.put(EAST, VoxelShapes.cuboid(0.99, 0.5, 0.48, 1.01, 1.2, 0.52));
        map.put(WEST, VoxelShapes.cuboid(-0.01, 0.5, 0.48, 0.01, 1.2, 0.52));
        map.put(SOUTH, VoxelShapes.cuboid(0.48, 0.5, 0.99, 0.52, 1.2, 1.01));
        map.put(NORTH, VoxelShapes.cuboid(0.48, 0.5, -0.01, 0.52, 1.2, 0.01));
    });
    public static final Map<RailShape, Set<Direction>> DERAIL_FIX_WALLS = Util.make(Maps.newEnumMap(RailShape.class), map -> {
        map.put(RailShape.NORTH_WEST, new ObjectArraySet<>(new Direction[]{SOUTH, EAST}));
        map.put(RailShape.NORTH_EAST, new ObjectArraySet<>(new Direction[]{SOUTH, WEST}));
        map.put(RailShape.SOUTH_EAST, new ObjectArraySet<>(new Direction[]{NORTH, WEST}));
        map.put(RailShape.SOUTH_WEST, new ObjectArraySet<>(new Direction[]{NORTH, EAST}));

        map.put(RailShape.NORTH_SOUTH, new ObjectArraySet<>(new Direction[]{WEST, EAST}));
        map.put(RailShape.EAST_WEST, new ObjectArraySet<>(new Direction[]{NORTH, SOUTH}));
        map.put(RailShape.ASCENDING_EAST, new ObjectArraySet<>(new Direction[]{NORTH, SOUTH}));
        map.put(RailShape.ASCENDING_WEST, new ObjectArraySet<>(new Direction[]{NORTH, SOUTH}));
        map.put(RailShape.ASCENDING_NORTH, new ObjectArraySet<>(new Direction[]{WEST, EAST}));
        map.put(RailShape.ASCENDING_SOUTH, new ObjectArraySet<>(new Direction[]{WEST, EAST}));
    });

    public static final Map<Set<Direction>, VoxelShape> DIRECTIONS_2_SHAPES = new Object2ReferenceOpenHashMap<>();


    private static VoxelShape getShapeForDirections(Set<Direction> directions) {
        VoxelShape totalShape = VoxelShapes.empty();
        for (Direction dir : directions) {
            VoxelShape voxelShape = RailHitboxHelper.DIRECTION_2_SHAPE.get(dir);
            totalShape = totalShape.isEmpty() ? voxelShape : VoxelShapes.union(totalShape, voxelShape);
        }
        return totalShape;
    }

    public static <T extends Comparable<T>> VoxelShape getOutlineShape(VoxelShape railCollisionShape, BlockState state, RailShape railShape, BlockView world, BlockPos pos, ShapeContext context) {
        if (CartMod.DERAILING_CART_FIX.isEnabled()) {
            Set<Direction> derailFixWall = RailHitboxHelper.DERAIL_FIX_WALLS.get(railShape);

            if (derailFixWall != null) {
                VoxelShape derailFixShape = RailHitboxHelper.DIRECTIONS_2_SHAPES.computeIfAbsent(derailFixWall, RailHitboxHelper::getShapeForDirections);
                if (railCollisionShape.isEmpty()) {
                    return derailFixShape;
                }
                return VoxelShapes.union(railCollisionShape, derailFixShape);
            }
        }
        return railCollisionShape;
    }

    public static VoxelShape getCollisionShape(VoxelShape railCollisionShape, RailShape railShape, BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (CartMod.DERAILING_CART_FIX.isEnabled()) {
            if (context instanceof EntityShapeContextAccessor entityContext) {
                Entity entity = entityContext.getEntity();
                if (entity instanceof AbstractMinecartEntity cart && cart instanceof AbstractMinecartEntityAccess_Physics cartAccess) {
                    if (cartAccess.isSelfMovingOnRail()) {
                        Set<Direction> derailFixWalls = RailHitboxHelper.DERAIL_FIX_WALLS.get(railShape);

                        Set<Direction> selectedWalls = new ObjectArraySet<>();
                        BlockPos cartOffset = cart.getBlockPos().subtract(pos);
                        for (Direction direction : derailFixWalls) {
                            Axis axis = direction.getAxis();
                            int axisOffset = axis.choose(cartOffset.getX(), 0, cartOffset.getZ());
                            if ((axisOffset <= 0 || direction.getDirection() != AxisDirection.POSITIVE) &&
                                    (axisOffset >= 0 || direction.getDirection() != AxisDirection.NEGATIVE)) {
                                selectedWalls.add(direction);

                            }
                        }
                        if (!selectedWalls.isEmpty()) {
                            VoxelShape derailFixShape = RailHitboxHelper.DIRECTIONS_2_SHAPES.computeIfAbsent(selectedWalls, RailHitboxHelper::getShapeForDirections);
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
