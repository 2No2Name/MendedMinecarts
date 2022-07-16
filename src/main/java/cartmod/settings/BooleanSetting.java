package cartmod.settings;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class BooleanSetting implements Setting {
    public final String name;
    private boolean state;
    public final boolean defaultState;
    public final Text description;

    public BooleanSetting(String name, boolean defaultState, Text description) {
        this.name = name;
        this.defaultState = defaultState;
        this.state = defaultState;
        this.description = description;
    }

    public void setEnabled(boolean state) {
        this.state = state;
    }

    public boolean isEnabled() {
        return this.state;
    }

    @Override
    public boolean isDefault() {
        return this.defaultState == this.state;
    }

    @Override
    public Text asText() {
        return new LiteralText(name).append(": ").append(new LiteralText(String.valueOf(state))).append(" ").append(new TranslatableText("cartmod.default")).append(" ").append(String.valueOf(defaultState));
    }

    @Override
    public Text getDescription() {
        return this.description;
    }


    @Override
    public void buildCommand(LiteralArgumentBuilder<ServerCommandSource> literalargumentbuilder) {
        literalargumentbuilder.
                then((CommandManager.literal(this.name).executes(commandContext -> {
                    commandContext.getSource().sendFeedback(this.asText(), false);
                    return 1;
                })));
        literalargumentbuilder.then(CommandManager.literal(this.name).
                then(CommandManager.argument("enabled", BoolArgumentType.bool()).executes((context) -> {
                    this.setEnabled(context.getArgument("enabled", Boolean.class));
                    return 1;
                })));
    }
}
