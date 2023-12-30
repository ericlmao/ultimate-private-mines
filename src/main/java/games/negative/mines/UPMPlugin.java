package games.negative.mines;

import games.negative.alumina.AluminaPlugin;
import games.negative.mines.core.Locale;
import org.jetbrains.annotations.NotNull;

public class UPMPlugin extends AluminaPlugin {

    private static UPMPlugin instance;

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {
        Locale.init(this);
    }

    @Override
    public void disable() {

    }

    /**
     * Returns the instance of the plugin.
     * @return The instance of the plugin.
     */
    @NotNull
    public static UPMPlugin instance() {
        return instance;
    }
}
