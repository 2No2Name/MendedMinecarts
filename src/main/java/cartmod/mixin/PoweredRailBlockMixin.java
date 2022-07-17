package cartmod.mixin;

import cartmod.RailHitboxHelper;
import net.minecraft.block.*;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PoweredRailBlock.class)
public abstract class PoweredRailBlockMixin extends AbstractRailBlock {
    @Shadow @Final public static EnumProperty<RailShape> SHAPE;

    protected PoweredRailBlockMixin(boolean forbidCurves, Settings settings) {
        super(forbidCurves, settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape railCollisionShape = super.getOutlineShape(state, world, pos, context);
        return RailHitboxHelper.getOutlineShape(railCollisionShape, state, state.get(SHAPE), world, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape railCollisionShape = super.getCollisionShape(state, world, pos, context);
        return RailHitboxHelper.getCollisionShape(railCollisionShape, state.get(SHAPE), state, world, pos, context);
    }
}
