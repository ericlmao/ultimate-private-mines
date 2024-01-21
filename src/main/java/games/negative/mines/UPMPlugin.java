package games.negative.mines;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import games.negative.alumina.AluminaPlugin;
import games.negative.mines.api.UPMApi;
import games.negative.mines.config.BlockPalletConfiguration;
import games.negative.mines.config.MineConfiguration;
import games.negative.mines.core.Locale;
import games.negative.mines.core.adapter.BukkitWorldTypeAdapter;
import games.negative.mines.core.adapter.InstantTypeAdapter;
import games.negative.mines.core.provider.UltimatePrivateMinesAPI;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public class UPMPlugin extends AluminaPlugin {

    public static Gson GSON;

    private static UPMPlugin instance;

    private UPMApi api;

    @Override
    public void load() {
        GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().excludeFieldsWithoutExposeAnnotation().
                registerTypeAdapter(World.class, new BukkitWorldTypeAdapter())
                .registerTypeAdapter(Instant.class, new InstantTypeAdapter()).create();

        instance = this;
    }

    @Override
    public void enable() {
        // Registering configurations
        new BlockPalletConfiguration();
        new MineConfiguration();
        Locale.init(this);
        // ------------------------

        this.api = new UltimatePrivateMinesAPI(this);
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
    public UPMApi api() {
        return api;
    }

    @NotNull
    public static UPMApi getApi() {
        return instance.api();
    }
}
