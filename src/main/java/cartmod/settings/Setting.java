package cartmod.settings;

import net.minecraft.text.Text;

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
        return Text.literal(name).append(": ").append(Text.literal(String.valueOf(state))).append(" ").append(Text.translatable("cartmod.default")).append(" ").append(String.valueOf(defaultState));
    }

    public Text getDescription() {
        return this.description;
    }
}
