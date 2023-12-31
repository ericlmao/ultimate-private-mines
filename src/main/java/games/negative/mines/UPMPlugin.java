package games.negative.mines;

import games.negative.alumina.AluminaPlugin;
import games.negative.mines.api.MinesAPI;
import games.negative.mines.core.Locale;
import games.negative.mines.core.provider.UltimatePrivateMinesAPI;
import org.jetbrains.annotations.NotNull;

public class UPMPlugin extends AluminaPlugin {

    private static UPMPlugin instance;

    private MinesAPI api;

    @Override
    public void load() {
        instance = this;
        this.api = new UltimatePrivateMinesAPI(this);
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

    /**
     * Returns the API of the plugin.
     * @return The API of the plugin.
     */
    @NotNull
    public MinesAPI api() {
        return api;
    }

    @NotNull
    public static MinesAPI getApi() {
        return instance.api();
    }
}
