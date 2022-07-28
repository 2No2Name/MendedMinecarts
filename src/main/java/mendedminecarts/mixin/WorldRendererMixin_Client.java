package mendedminecarts.mixin;

import mendedminecarts.AbstractMinecartEntityAccess;
import mendedminecarts.MendedMinecartsMod;
import mendedminecarts.MinecartDisplayData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin_Client {

    @Shadow
    @Final
    private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(
            method = "renderEntity(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
            at = @At("HEAD")
    )
    private <E extends Entity> void renderMinecartInfo(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (
                (
                        MendedMinecartsMod.DISPLAY_CART_DATA_BOX.isEnabled() ||
                                MendedMinecartsMod.DISPLAY_CART_DATA_HOPPER_PICKUP_VOLUME.isEnabled() ||
                                MendedMinecartsMod.DISPLAY_CART_DATA_HOPPER_EXTRACT_VOLUME.isEnabled() ||
                                MendedMinecartsMod.DISPLAY_CART_DATA_HOPPER_EXTRACT_BLOCK.isEnabled()
                ) && entity instanceof AbstractMinecartEntityAccess entityAccess) {
            if (this.entityRenderDispatcher.getSquaredDistanceToCamera(entity) > MendedMinecartsMod.DATA_RENDER_DISTANCE_SQ) {
                return;
            }
            MinecartDisplayData displayInfo = entityAccess.getDisplayInfo();
            if (displayInfo != null) {
                VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getLines());
                matrices.push();
                matrices.translate(-cameraX, -cameraY, -cameraZ);

                if (MendedMinecartsMod.DISPLAY_CART_DATA_BOX.isEnabled()) {
                    Box box = displayInfo.boundingBox();
                    if (box != null) {
                        WorldRenderer.drawBox(matrices, buffer, box, 0.5f, 0.5f, 1.0f, 1.0f);
                    }
                }
                if (MendedMinecartsMod.DISPLAY_CART_DATA_HOPPER_PICKUP_VOLUME.isEnabled()) {
                    Box pickupArea2 = displayInfo.hopperPickupArea2();
                    if (pickupArea2 != null) {
                        WorldRenderer.drawBox(matrices, buffer, pickupArea2, 0.7f, 0.3f, 0.3f, 1.0f);
                    }

                    Object[] pickupArea1 = displayInfo.hopperPickupArea1();
                    for (Object pickupArea : pickupArea1) {
                        if (pickupArea instanceof Box box) {
                            WorldRenderer.drawBox(matrices, buffer, box, 0.7f, 0.3f, 0.3f, 1.0f);
                        }
                    }
                }
                if (MendedMinecartsMod.DISPLAY_CART_DATA_HOPPER_EXTRACT_BLOCK.isEnabled()) {
                    BlockPos pos = displayInfo.hopperExtractBlock();
                    if (pos != null) {
                        WorldRenderer.drawBox(matrices, buffer, new Box(pos), 0.3f, 0.7f, 0.3f, 1.0f);
                    }
                }
                if (MendedMinecartsMod.DISPLAY_CART_DATA_HOPPER_EXTRACT_VOLUME.isEnabled()) {
                    Box box = displayInfo.hopperExtractBox();
                    if (box != null) {
                        WorldRenderer.drawBox(matrices, buffer, box, 0.3f, 0.3f, 0.7f, 1.0f);
                    }
                }

                matrices.pop();
            }
        }
    }
}
