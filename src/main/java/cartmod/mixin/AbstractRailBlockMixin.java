package cartmod.mixin;

import cartmod.RailHitboxHelper;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AbstractRailBlock.class)
public abstract class AbstractRailBlockMixin extends Block {

    @Shadow
    public abstract Property<RailShape> getShapeProperty();

    @Shadow
    @Final
    protected static VoxelShape ASCENDING_SHAPE;

    @Shadow
    @Final
    protected static VoxelShape STRAIGHT_SHAPE;

    public AbstractRailBlockMixin(Settings settings) {
        super(settings);
    }

    /**
     * @author 2No2Name
     * @reason add our custom shape
     */
    @Overwrite
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape railCollisionShape;
        RailShape railShape = state.isOf(this) ? state.get(this.getShapeProperty()) : null;
        if (railShape != null && railShape.isAscending()) {
            railCollisionShape = ASCENDING_SHAPE;
        } else {
            railCollisionShape = STRAIGHT_SHAPE;
        }
        return RailHitboxHelper.getOutlineShape(railCollisionShape, state, state.get(this.getShapeProperty()), world, pos, context);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape railCollisionShape = super.getCollisionShape(state, world, pos, context);
        return RailHitboxHelper.getCollisionShape(railCollisionShape, state.get(this.getShapeProperty()), state, world, pos, context);
    }
}
