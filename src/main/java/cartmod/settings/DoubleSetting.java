package cartmod.settings;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class DoubleSetting implements Setting {
    public final String name;
    private double state;
    public final double defaultState;
    public final Text description;

    public DoubleSetting(String name, double defaultState, Text description) {
        this.name = name;
        this.defaultState = defaultState;
        this.state = defaultState;
        this.description = description;
    }

    public void setDouble(double state) {
        this.state = state;
    }

    public double getState() {
        return this.state;
    }

    @Override
    public boolean isDefault() {
        return this.state == this.defaultState;
    }

    @Override
    public Text asText() {
        return new LiteralText(name).append(": ").append(new LiteralText(String.valueOf(state)));
    }

    @Override
    public Text getDefault() {
        return new TranslatableText("cartmod.default").append(": ").append(String.valueOf(defaultState));
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
                    commandContext.getSource().sendFeedback(this.getDefault(), false);
                    return 1;
                })));
        literalargumentbuilder.then(CommandManager.literal(this.name).
                then(CommandManager.argument("state", DoubleArgumentType.doubleArg()).executes((context) -> {
                    this.setDouble(context.getArgument("state", Double.class));
                    context.getSource().sendFeedback(this.asText(), false);
                    return 1;
                })));
    }
}
