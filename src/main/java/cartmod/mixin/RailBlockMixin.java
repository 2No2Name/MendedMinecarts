package cartmod.mixin;

import cartmod.CartMod;
import cartmod.AbstractMinecartEntityAccess_Physics;
import com.google.common.collect.Maps;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(RailBlock.class)
public abstract class RailBlockMixin extends AbstractRailBlock {
    @Shadow @Final public static EnumProperty<RailShape> SHAPE;
    
    private static final VoxelShape CURVED_RAIL_WALL_POS_X = VoxelShapes.cuboid(0.99, 0.7, 0.48, 1, 1.2, 0.52);
    private static final VoxelShape CURVED_RAIL_WALL_NEG_X = VoxelShapes.cuboid(0.0, 0.7, 0.48, 0.01, 1.2, 0.52);
    private static final VoxelShape CURVED_RAIL_WALL_POS_Z = VoxelShapes.cuboid(0.48, 0.7, 0.99,0.52, 1.2, 1);
    private static final VoxelShape CURVED_RAIL_WALL_NEG_Z = VoxelShapes.cuboid(0.48, 0.7, 0.0, 0.52, 1.2, 0.01);

    private static final Map<RailShape, VoxelShape> DERAIL_FIX_WALLS = Util.make(Maps.newEnumMap(RailShape.class), map -> {
        map.put(RailShape.SOUTH_EAST, VoxelShapes.union(CURVED_RAIL_WALL_POS_Z, CURVED_RAIL_WALL_POS_X));
        map.put(RailShape.SOUTH_WEST, VoxelShapes.union(CURVED_RAIL_WALL_POS_Z, CURVED_RAIL_WALL_NEG_X));
        map.put(RailShape.NORTH_WEST, VoxelShapes.union(CURVED_RAIL_WALL_NEG_Z, CURVED_RAIL_WALL_NEG_X));
        map.put(RailShape.NORTH_EAST, VoxelShapes.union(CURVED_RAIL_WALL_NEG_Z, CURVED_RAIL_WALL_POS_X));
    });

    protected RailBlockMixin(boolean forbidCurves, Settings settings) {
        super(forbidCurves, settings);
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
                        VoxelShape derailFixWall = DERAIL_FIX_WALLS.get(railShape);
                        if (derailFixWall != null)
                            if (railCollisionShape.isEmpty()) {
                                return derailFixWall;
                            }
                        return VoxelShapes.union(railCollisionShape, derailFixWall);
                    }
                }
            }
        }
        return railCollisionShape;
    }
}
