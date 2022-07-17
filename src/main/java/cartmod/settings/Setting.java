package cartmod.settings;

import cartmod.CartMod;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Properties;

public interface Setting {
    default void onChanged() {
        CartMod.saveSettings();
    }

    boolean isDefault();

    Text asText();

    Text getDefault();

    Text getDescription();

    LiteralArgumentBuilder<ServerCommandSource> buildCommand();

    String getName();

    void loadFromProperties(Properties properties, String namePrefix);

    void writeToProperties(Properties properties, String namePrefix);
}
