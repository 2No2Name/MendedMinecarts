package cartmod.mixin;

import cartmod.CartMod;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractMinecartEntity.class)
public class AbstractMinecartEntityMixin_Client {
	@Shadow private int clientInterpolationSteps;

//	@Redirect(
//			at = @At(value = "FIELD", target = "Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;clientInterpolationSteps:I", ordinal = 0),
//			method = "tick")
//	private int simulateCartOnClient(AbstractMinecartEntity instance) {
//		if (ExampleMod.SKIP_CLIENT_CART_INTERPOLATION) {
//			return 0;
//		}
//		return this.clientInterpolationSteps;
//	}
	@Redirect(
			at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"),
			method = "tick")
	private boolean simulateCartOnClient(World instance) {
		if (instance.isClient) {
			//false => do simulation
			return !CartMod.ACCURATE_CLIENT_MINECARTS.isEnabled();
		}
		return instance.isClient;
	}
}
