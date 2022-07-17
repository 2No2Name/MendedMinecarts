package cartmod.settings;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.Arrays;

public class BooleanSettingGroup extends BooleanSetting {

    private BooleanSetting[] children;

    public BooleanSettingGroup(String name, boolean defaultState, Text description) {
        super(name, defaultState, description);
        this.children = new BooleanSetting[0];
    }

    public BooleanSettingGroup children(BooleanSetting... children) {
        this.children = children;
        return this;
    }

    @Override
    public boolean isEnabled() {
        return Arrays.stream(this.children).anyMatch(BooleanSetting::isEnabled);
    }

    @Override
    public boolean isDefault() {
        return Arrays.stream(this.children).allMatch(BooleanSetting::isDefault);
    }

    @Override
    public Text asText() {
        return super.asText();
    }

    @Override
    public Text getDefault() {
        return super.asText();
    }

    @Override
    public Text getDescription() {
        return super.getDescription();
    }

    @Override
    public LiteralArgumentBuilder<ServerCommandSource> buildCommand() {
        LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal(this.name).executes(commandContext -> {
            commandContext.getSource().sendFeedback(this.asText(), false);
            listChildren(commandContext.getSource(), this.children);
            return 1;
        });

        for (Setting setting : this.children) {
            builder = builder.then(setting.buildCommand());
        }
        return builder;
    }

    private static void listChildren(ServerCommandSource source, BooleanSetting[] children) {
        source.sendFeedback(new TranslatableText("cartmod.available_settings"), false);
        source.sendFeedback(new TranslatableText(""), false);
        for (Setting setting : children) {
            source.sendFeedback(setting.asText(), false);
            source.sendFeedback(setting.getDefault(), false);
            source.sendFeedback(setting.getDescription(), false);
            source.sendFeedback(new LiteralText(""), false);
        }
    }
}
