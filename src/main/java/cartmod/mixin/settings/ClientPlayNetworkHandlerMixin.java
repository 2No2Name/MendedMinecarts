package cartmod.mixin.settings;

import cartmod.settings.SettingSync;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(
            method = "onCustomPayload",
            at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V", shift = At.Shift.BEFORE, remap = false),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void onCustomPayload(CustomPayloadS2CPacket packet, CallbackInfo ci, Identifier identifier, PacketByteBuf packetByteBuf) {
        if (packet.getChannel().equals(SettingSync.CHANNEL)) {
            ci.cancel();
            SettingSync.handleData(packetByteBuf);
        }
    }
}
