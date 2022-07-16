package cartmod.settings;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public interface Setting {
    boolean isDefault();

    Text asText();

    Text getDescription();

    void buildCommand(LiteralArgumentBuilder<ServerCommandSource> literalargumentbuilder);
}
