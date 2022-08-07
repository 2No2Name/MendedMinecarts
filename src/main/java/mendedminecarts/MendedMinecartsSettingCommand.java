package mendedminecarts;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import mendedminecarts.settings.Setting;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * Class for the /counter command which allows to use hoppers pointing into wool
 */

public class MendedMinecartsSettingCommand {
    /**
     * The method used to register the command and make it available for the players to use.
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalargumentbuilder = CommandManager.literal(MendedMinecartsMod.SETTING_COMMAND).executes((context)
                -> listSettings(context.getSource()));

        for (Setting setting : MendedMinecartsMod.SETTINGS) {
            literalargumentbuilder = literalargumentbuilder.then(setting.buildCommand());
        }
        dispatcher.register(literalargumentbuilder);
    }


    private static int listSettings(ServerCommandSource source) {
        source.sendFeedback(Text.translatable("mendedminecarts.available_settings"), false);
        source.sendFeedback(Text.translatable(""), false);
        for (Setting setting : MendedMinecartsMod.SETTINGS) {
            source.sendFeedback(setting.asText(), false);
            source.sendFeedback(setting.getDefault(), false);
            source.sendFeedback(setting.getDescription(), false);
            source.sendFeedback(Text.literal(""), false);
        }
        return 1;
    }

}
