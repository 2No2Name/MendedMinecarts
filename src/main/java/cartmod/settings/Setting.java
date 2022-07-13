package cartmod.settings;

public class Setting {
    public final String name;
    private boolean state;
    public final boolean defaultState;

    public Setting(String name, boolean defaultState) {
        this.name = name;
        this.defaultState = defaultState;
        this.state = defaultState;
    }

    public void setEnabled(boolean state) {
        this.state = state;
    }

    public boolean isEnabled() {
        return state;
    }

    @Override
    public String toString() {
        return name + ": " + state + " default: " + defaultState;
    }
}
