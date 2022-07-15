package cartmod.mixin;

import cartmod.AbstractMinecartEntityAccess;
import cartmod.CartMod;
import cartmod.MinecartDisplayData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecartEntityRenderer.class)
public class MinecartEntityRenderer_Client<T extends AbstractMinecartEntity> {
    
    @Inject(
            method = "render(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD")
    )
    private void renderMinecartInfo(T minecartEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        if ((CartMod.DISPLAY_CART_POSITION.isEnabled() || CartMod.DISPLAY_CART_DATA.isEnabled()) && minecartEntity instanceof AbstractMinecartEntityAccess entityAccess) {
            MinecartDisplayData displayInfo = entityAccess.getDisplayInfo();
            if (displayInfo == null) {
                return;
            }
            Box box = displayInfo.lastReceivedPosBox();
            if (box == null) {
                return;
            }
            box = box.offset(-minecartEntity.getX(), -minecartEntity.getY(), -minecartEntity.getZ());

            VertexConsumer buffer = vertexConsumerProvider.getBuffer(RenderLayer.getLines());
            WorldRenderer.drawBox(matrixStack, buffer, box, 1f, 1f, 1.0f, 1.0f);
        }
    }
}
