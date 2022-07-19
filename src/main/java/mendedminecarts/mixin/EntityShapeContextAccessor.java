package mendedminecarts.mixin;

import net.minecraft.block.EntityShapeContext;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityShapeContext.class)
public interface EntityShapeContextAccessor {

    @Accessor
    Entity getEntity();
}
