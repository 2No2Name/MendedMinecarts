package mendedminecarts.settings;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Properties;

public class IntegerSetting implements Setting {
    public final String name;
    private int state;
    public final int defaultState;
    public final Text description;

    public IntegerSetting(String name, int defaultState, Text description) {
        this.name = name;
        this.defaultState = defaultState;
        this.state = defaultState;
        this.description = description;
    }

    public void setEnabled(int state) {
        this.state = state;
    }

    public int getState() {
        return this.state;
    }

    @Override
    public boolean isDefault() {
        return this.defaultState == this.state;
    }

    @Override
    public Text asText() {
        return Text.literal(name).append(": ").append(Text.literal(String.valueOf(state)));
    }

    @Override
    public Text getDefault() {
        return Text.translatable("mendedminecarts.default").append(": ").append(String.valueOf(defaultState));
    }

    @Override
    public Text getDescription() {
        return this.description;
    }


    @Override
    public LiteralArgumentBuilder<ServerCommandSource> buildCommand() {
        return CommandManager.literal(this.name).executes(commandContext -> {
            commandContext.getSource().sendFeedback(() -> this.asText(), false);
            commandContext.getSource().sendFeedback(() -> this.getDefault(), false);
            return 1;
        }).
                then(CommandManager.argument("enabled", IntegerArgumentType.integer()).executes((context) -> {
                    this.setEnabled(context.getArgument("enabled", Integer.class));
                    Setting.super.onChangedByCommand();
                    context.getSource().sendFeedback(this::asText, false);
                    return 1;
                }));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void loadFromProperties(Properties properties, String namePrefix) {
        String property = properties.getProperty(namePrefix + this.name);
        if (property != null) {
            this.setFromStringValue(property);
        }
    }

    @Override
    public void writeToProperties(Properties properties, String namePrefix) {
        properties.setProperty(namePrefix + this.name, this.getStringValue());
    }

    @Override
    public void setFromStringValue(String settingValue) {
        this.setEnabled(Integer.parseInt(settingValue));
    }

    @Override
    public String getStringValue() {
        return String.valueOf(this.state);
    }


    @Override
    public boolean isEnabled() {
        return !this.isDefault();
    }
}
