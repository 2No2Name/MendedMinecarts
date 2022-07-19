package mendedminecarts.mixin;

import mendedminecarts.AbstractMinecartEntityAccess;
import mendedminecarts.MendedMinecartsMod;
import mendedminecarts.MinecartDisplayData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin_Client {

    @Inject(
            method = "renderEntity(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
            at = @At("HEAD")
    )
    private <E extends Entity> void renderMinecartInfo(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if ((MendedMinecartsMod.DISPLAY_CART_POSITION.isEnabled()) && entity instanceof AbstractMinecartEntityAccess entityAccess) {
            MinecartDisplayData displayInfo = entityAccess.getDisplayInfo();
            if (displayInfo != null) {
                Box box = displayInfo.lastReceivedPosBox();
                if (box != null) {
                    matrices.push();
                    matrices.translate(-cameraX, -cameraY, -cameraZ);
                    VertexConsumer buffer = vertexConsumers.getBuffer(RenderLayer.getLines());
                    WorldRenderer.drawBox(matrices, buffer, box, 0.5f, 0.5f, 1.0f, 1.0f);
                    matrices.pop();
                }
            }
        }
    }
}
