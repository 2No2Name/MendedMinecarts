package cartmod.mixin;

import cartmod.AbstractMinecartEntityAccess;
import cartmod.CartMod;
import cartmod.MinecartDisplayData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin_Client extends Entity implements AbstractMinecartEntityAccess {

	private MinecartDisplayData displayData;

	@Shadow public abstract void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate);

	public AbstractMinecartEntityMixin_Client(EntityType<?> type, World world) {
		super(type, world);
	}

	@Redirect(
			method = "tick",
			at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"))
	private boolean simulateCartsOnClientLikeOnServer(World instance) {
		if (instance.isClient) {
			//false => do simulation
			return !CartMod.ACCURATE_CLIENT_MINECARTS.isEnabled();
		}
		return false;
	}

	@Inject(
			method = "updateTrackedPositionAndAngles(DDDFFIZ)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void setCartPosLikeOtherEntities(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate, CallbackInfo ci) {
		if (this.world.isClient && CartMod.NO_CLIENT_CART_INTERPOLATION.isEnabled()) {
			ci.cancel();
			super.updateTrackedPositionAndAngles(x, y, z, yaw, pitch, interpolationSteps, interpolate);
		}
		this.displayData = new MinecartDisplayData(this.getType().getDimensions().getBoxAt(x, y, z), (AbstractMinecartEntity) (Object) this);
	}

	@Override
	public MinecartDisplayData getDisplayInfo() {
		return displayData;
	}
}
