package mendedminecarts.settings;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Properties;

public class DoubleSetting implements Setting {
    public final String name;
    private double state;
    public final double defaultState;
    public final Text description;
    private final double minValue, maxValue;

    public DoubleSetting(String name, double defaultState, Text description, double minValue, double maxValue) {
        this.name = name;
        this.defaultState = defaultState;
        this.state = defaultState;
        this.description = description;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public void setDouble(double state) {
        if (state >= this.minValue && state <= this.maxValue) {
            this.state = state;
        }
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
            commandContext.getSource().sendFeedback(this.asText(), false);
            commandContext.getSource().sendFeedback(this.getDefault(), false);
            return 1;
        }).then(CommandManager.argument("state", DoubleArgumentType.doubleArg(this.minValue, this.maxValue)).executes((context) -> {
            this.setDouble(context.getArgument("state", Double.class));
            this.onChangedByCommand();
            context.getSource().sendFeedback(this.asText(), false);
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
        this.setDouble(Double.parseDouble(settingValue));
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
