package mendedminecarts.settings;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mendedminecarts.MendedMinecartsMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Properties;

public interface Setting {
    boolean isClient = FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

    default void onChangedByCommand() {
        if (isClient) {
            MendedMinecartsMod.saveSettings();
        } else {
            SettingSync.updateToClients(this);
        }
    }

    boolean isDefault();

    Text asText();

    Text getDefault();

    Text getDescription();

    LiteralArgumentBuilder<ServerCommandSource> buildCommand();

    String getName();

    void loadFromProperties(Properties properties, String namePrefix);

    void writeToProperties(Properties properties, String namePrefix);

    void setFromStringValue(String settingValue);

    String getStringValue();

    boolean isEnabled();
}
