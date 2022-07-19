package mendedminecarts.mixin;

import mendedminecarts.AbstractMinecartEntityAccess;
import mendedminecarts.MendedMinecartsMod;
import mendedminecarts.MinecartDisplayData;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(MinecartEntityRenderer.class)
public abstract class MinecartEntityRendererMixin<T extends AbstractMinecartEntity> extends EntityRenderer<T> {

    protected MinecartEntityRendererMixin(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Inject(
            method = "render(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("RETURN")
    )
    private void renderInfo(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (MendedMinecartsMod.DISPLAY_CART_DATA.isEnabled() && entity instanceof AbstractMinecartEntityAccess entityAccess) {
            double d = this.dispatcher.getSquaredDistanceToCamera(entity);
            if (d > MendedMinecartsMod.DATA_RENDER_DISTANCE_SQ) {
                return;
            }

            MinecartDisplayData displayInfo = entityAccess.getDisplayInfo();
            if (displayInfo == null)
                return;

            ArrayList<Text> infoTexts = displayInfo.getInfoTexts();
            if (infoTexts.isEmpty()) {
                return;
            }

            TextRenderer textRenderer = this.getTextRenderer();


            float f = entity.getHeight() + 0.5f;

            float yOffset = 20;
            for (Text infoText : infoTexts) {
                matrices.push();
                matrices.translate(0.0, f, 0.0);
                matrices.multiply(this.dispatcher.getRotation());
                matrices.scale(-0.025f, -0.025f, 0.025f);
                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                float h = -textRenderer.getWidth(infoText) / 2f;
                textRenderer.draw(infoText, h, yOffset, -1, false, matrix4f, vertexConsumerProvider, true, 0, light);

                matrices.pop();

                yOffset += 10;
            }
        }
    }
}
