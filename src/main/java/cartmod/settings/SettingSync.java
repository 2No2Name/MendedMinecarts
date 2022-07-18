package cartmod.settings;

import cartmod.CartMod;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SettingSync {
    public static final Identifier CHANNEL = new Identifier("cartmod:settings");
    public static PlayerManager PLAYER_MANAGER = null;

    public static void handleData(PacketByteBuf data) {
        try {
            int version = data.readInt();
            if (CartMod.SETTING_VERSION == version) {
                int settingIndex = data.readInt();
                String settingValue = data.readString();
                if (settingIndex >= 0 && settingIndex < CartMod.FLAT_SETTINGS.size()) {
                    CartMod.FLAT_SETTINGS.get(settingIndex).setFromStringValue(settingValue);
                }
            }
        } catch (Exception ignored) {

        }
    }

    public static CustomPayloadS2CPacket makeSettingPacket(Setting setting) {
        PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
        packetByteBuf.writeInt(CartMod.SETTING_VERSION);
        packetByteBuf.writeInt(CartMod.FLAT_SETTINGS.indexOf(setting));
        packetByteBuf.writeString(setting.getStringValue());

        return new CustomPayloadS2CPacket(CHANNEL, packetByteBuf);
    }

    public static void updateAllToPlayer(ServerPlayerEntity player) {
        for (Setting setting : CartMod.FLAT_SETTINGS) {
            CustomPayloadS2CPacket packet = makeSettingPacket(setting);
            player.networkHandler.sendPacket(packet);
        }
    }

    public static void updateToClients(Setting setting) {
        PlayerManager playerManager = PLAYER_MANAGER;
        if (playerManager == null) {
            return;
        }
        CustomPayloadS2CPacket packet = makeSettingPacket(setting);
        playerManager.sendToAll(packet);
    }
}
