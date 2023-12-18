package mendedminecarts.mixin;

import mendedminecarts.AbstractMinecartEntityAccess;
import mendedminecarts.MendedMinecartsMod;
import mendedminecarts.MinecartDisplayData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
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
			return !MendedMinecartsMod.ACCURATE_CLIENT_MINECARTS.isEnabled();
		}
		return false;
	}

//	@Inject(
//			method = "setVelocityClient",
//			at = @At("HEAD")
//	)
//	private void rememberCartVelocity(double x, double y, double z, CallbackInfo ci) {
//		this.displayData = MinecartDisplayData.withVelocity(this, new Vec3d(x,y,z));
//	}

	@Inject(
			method = "updateTrackedPositionAndAngles(DDDFFIZ)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private void setCartPosLikeOtherEntities(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate, CallbackInfo ci) {
		if (this.getWorld().isClient && (MendedMinecartsMod.ACCURATE_CLIENT_MINECARTS.isEnabled() || MendedMinecartsMod.NO_CLIENT_CART_INTERPOLATION.isEnabled())) {
			ci.cancel();
			super.updateTrackedPositionAndAngles(x, y, z, yaw, pitch, interpolationSteps, interpolate);
		}
//		this.displayData = MinecartDisplayData.withPos(this, new Vec3d(x, y, z));
	}

	@Inject(
			method = "tick",
			at = @At("HEAD")
	)
	private void updateDisplayInfo(CallbackInfo ci) {
		if (!this.getWorld().isClient()) {
			return;
		}
		if ((MendedMinecartsMod.DISPLAY_CART_DATA.isEnabled() || MendedMinecartsMod.VISUAL_HOPPER_CART_LOCKING.isEnabled()) &&
				MinecraftClient.getInstance().player != null &&
				MinecraftClient.getInstance().player.hasPermissionLevel(2) &&
				MinecraftClient.getInstance().player.getPos().squaredDistanceTo(this.getPos()) < MendedMinecartsMod.DATA_RENDER_DISTANCE_SQ) {
			MinecraftClient.getInstance().player.networkHandler.getDataQueryHandler().queryEntityNbt(this.getId(), nbt -> {
				try {
					this.displayData = MinecartDisplayData.fromNBT(this, nbt);
				} catch (Exception e) {
					this.displayData = null;
					e.printStackTrace();
				}
			});
		} else {
			this.displayData = null;
		}
	}

	@Override
	public MinecartDisplayData getDisplayInfo() {
		return displayData;
	}
}
