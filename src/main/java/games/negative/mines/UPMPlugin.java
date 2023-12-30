package games.negative.mines;

import games.negative.alumina.AluminaPlugin;
import org.jetbrains.annotations.NotNull;

public class UPMPlugin extends AluminaPlugin {

    private static UPMPlugin instance;

    @Override
    public void load() {
        instance = this;
    }

    @Override
    public void enable() {

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
