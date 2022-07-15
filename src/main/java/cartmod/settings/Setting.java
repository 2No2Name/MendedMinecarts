package cartmod.settings;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class Setting {
    public final String name;
    private boolean state;
    public final boolean defaultState;
    public final Text description;

    public Setting(String name, boolean defaultState, Text description) {
        this.name = name;
        this.defaultState = defaultState;
        this.state = defaultState;
        this.description = description;
    }

    public void setEnabled(boolean state) {
        this.state = state;
    }

    public boolean isEnabled() {
        return state;
    }

    public Text asText() {
        return new LiteralText(name).append(": ").append(new LiteralText(String.valueOf(state))).append(" ").append(new TranslatableText("cartmod.default")).append(" ").append(String.valueOf(defaultState));
    }

    public Text getDescription() {
        return this.description;
    }
}
