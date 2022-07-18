package cartmod.mixin.settings;

import cartmod.settings.SettingSync;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(
            method = "onPlayerConnect",
            at = @At("RETURN")
    )
    private void syncSettings(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        SettingSync.PLAYER_MANAGER = (PlayerManager) (Object) this;
        SettingSync.updateAllToPlayer(player);
    }
}
