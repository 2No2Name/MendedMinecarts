package mendedminecarts.settings;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Properties;

public class SettingGroup extends BooleanSetting {

    private Setting[] children;

    public SettingGroup(String name, boolean defaultState, Text description) {
        super(name, defaultState, description);
        this.children = new BooleanSetting[0];
    }

    public SettingGroup children(Setting... children) {
        this.children = children;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return Arrays.stream(this.children).anyMatch(Setting::isEnabled);
    }

    @Override
    public boolean isDefault() {
        return Arrays.stream(this.children).allMatch(Setting::isDefault);
    }

    @Override
    public Text asText() {
        //TODO
        return super.asText();
    }

    @Override
    public Text getDefault() {
        //TODO
        return super.getDefault();
    }

    @Override
    public Text getDescription() {
        //TODO
        return super.getDescription();
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> buildCommand() {
        LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal(this.name).executes(commandContext -> {
            commandContext.getSource().sendFeedback(this::asText, false);
            listChildren(commandContext.getSource(), this.children);
            return 1;
        });

        for (Setting setting : this.children) {
            builder = builder.then(setting.buildCommand());
        }
        return builder;
    }

    private static void listChildren(ServerCommandSource source, Setting[] children) {
        source.sendFeedback(() -> Text.translatable("mendedminecarts.available_settings"), false);
        source.sendFeedback(() -> Text.translatable(""), false);
        for (Setting setting : children) {
            source.sendFeedback(setting::asText, false);
            source.sendFeedback(setting::getDefault, false);
            source.sendFeedback(setting::getDescription, false);
            source.sendFeedback(() -> Text.literal(""), false);
        }
    }

    @Override
    public void loadFromProperties(Properties properties, String namePrefix) {
        for (Setting setting : this.children) {
            setting.loadFromProperties(properties, this.name + ".");
        }
    }

    @Override
    public void writeToProperties(Properties properties, String namePrefix) {
        for (Setting setting : this.children) {
            setting.writeToProperties(properties, this.name + ".");
        }
    }

    public Setting[] getChildren() {
        return this.children;
    }
}
