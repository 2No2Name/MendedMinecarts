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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Map;
import java.util.Set;

import static net.minecraft.util.math.Direction.*;

public class RailHitboxHelper {
    public static final VoxelShape WALL_SHAPE = VoxelShapes.cuboid(0.48, 0.5, 0.48, 0.52, 1.2, 0.52);
    public static final Map<Direction, VoxelShape> DIRECTION_2_SHAPE = Util.make(Maps.newEnumMap(Direction.class), map -> {
        map.put(EAST, getShapeForDirection(EAST));
        map.put(WEST, getShapeForDirection(WEST));
        map.put(NORTH, getShapeForDirection(NORTH));
        map.put(SOUTH, getShapeForDirection(SOUTH));
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

    public static final Map<Set<VoxelShape>, VoxelShape> WALL_SHAPES_UNION = new Object2ReferenceOpenHashMap<>();

    private static VoxelShape getShapeForDirection(Direction direction) {
        return WALL_SHAPE.offset(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ());
    }

    private static VoxelShape getUnionShape(Set<VoxelShape> shapes) {
        VoxelShape totalShape = VoxelShapes.empty();
        for (VoxelShape shape : shapes) {
            totalShape = totalShape.isEmpty() ? shape : VoxelShapes.union(totalShape, shape);
        }
        return totalShape;
    }

//    public static <T extends Comparable<T>> VoxelShape getOutlineShape(VoxelShape railCollisionShape, BlockState state, RailShape railShape, BlockView world, BlockPos pos, ShapeContext context) {
//        if (CartMod.DERAILING_CART_FIX.isEnabled() /*|| CartMod.DERAILING_CART_FIX_DEMO.isEnabled()*/) {
//            Set<Direction> derailFixWalls = RailHitboxHelper.DERAIL_FIX_WALLS.get(railShape);
//
//            if (derailFixWalls != null) {
//                ObjectArraySet<VoxelShape> wallShapes = new ObjectArraySet<>();
//                for (var wall : derailFixWalls) {
//                    wallShapes.add(DIRECTION_2_SHAPE.computeIfAbsent(wall, RailHitboxHelper::getShapeForDirection));
//                }
//                if (!railCollisionShape.isEmpty()) {
//                    wallShapes.add(railCollisionShape);
//                }
//                return WALL_SHAPES_UNION.computeIfAbsent(wallShapes, RailHitboxHelper::getUnionShape);
//            }
//        }
//        return railCollisionShape;
//    }

    public static VoxelShape getCollisionShape(VoxelShape railCollisionShape, RailShape railShape, BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
//        if (CartMod.DERAILING_CART_FIX_DEMO.isEnabled()) {
//            return getOutlineShape(railCollisionShape, state, railShape, world, pos, context);
//        }
        if (CartMod.DERAILING_CART_FIX.isEnabled()) {
            if (context instanceof EntityShapeContextAccessor entityContext) {
                Entity entity = entityContext.getEntity();
                if (entity instanceof AbstractMinecartEntity cart && cart instanceof AbstractMinecartEntityAccess_Physics cartAccess) {
                    if (cartAccess.isSelfMovingOnRail()) {
                        Set<Direction> derailFixWalls = RailHitboxHelper.DERAIL_FIX_WALLS.get(railShape);

                        Set<VoxelShape> selectedWalls = new ObjectArraySet<>();
                        Box offsetCartBox = cart.getBoundingBox().offset(-pos.getX(), -pos.getY(), -pos.getZ());
                        for (Direction direction : derailFixWalls) {
                            VoxelShape wallShape = RailHitboxHelper.DIRECTION_2_SHAPE.get(direction);
                            Axis axis = direction.getAxis();
                            AxisDirection axisDirection = direction.getDirection();
                            double distanceToHitbox = axisDirection == AxisDirection.NEGATIVE ? offsetCartBox.getMin(axis) - (wallShape.getMax(axis)) : wallShape.getMin(axis) - offsetCartBox.getMax(axis);
                            if (distanceToHitbox > 0) {
                                selectedWalls.add(wallShape);
                            }
                        }
                        if (!selectedWalls.isEmpty()) {
                            if (!railCollisionShape.isEmpty()) {
                                selectedWalls.add(railCollisionShape);
                            }
                            return RailHitboxHelper.WALL_SHAPES_UNION.computeIfAbsent(selectedWalls, RailHitboxHelper::getUnionShape);
                        }
                    }
                }
            }
        }
        return railCollisionShape;
    }


    public static boolean isCurvedShape(RailShape railShape) {
        return railShape == RailShape.NORTH_EAST || railShape == RailShape.NORTH_WEST || railShape == RailShape.SOUTH_EAST || railShape == RailShape.SOUTH_WEST;
    }

    public static Axis getRailAxis(RailShape railShape) {
        if (isCurvedShape(railShape)) {
            return null;
        }
        if (railShape == RailShape.ASCENDING_EAST || railShape == RailShape.ASCENDING_WEST || railShape == RailShape.EAST_WEST) {
            return EAST.getAxis();
        }
        return SOUTH.getAxis();
    }

}
