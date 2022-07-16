package cartmod;

import cartmod.settings.BooleanSetting;
import cartmod.settings.Setting;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

/**
 * Class for the /counter command which allows to use hoppers pointing into wool
 */

public class CartModSettingCommand
{
    /**
     * The method used to register the command and make it available for the players to use.
     */
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        LiteralArgumentBuilder<ServerCommandSource> literalargumentbuilder = CommandManager.literal(CartMod.SETTING_COMMAND).executes((context)
         -> listSettings(context.getSource()));

        for (Setting setting : CartMod.SETTINGS) {
            setting.buildCommand(literalargumentbuilder);
        }
        dispatcher.register(literalargumentbuilder);
    }


    private static int listSettings(ServerCommandSource source)
    {
        source.sendFeedback(new TranslatableText("cartmod.available_settings"), false);
        for (Setting setting : CartMod.SETTINGS)
        {
            source.sendFeedback(setting.asText(), false);
            source.sendFeedback(setting.getDescription(), false);
        }
        return 1;
    }

}
