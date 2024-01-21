package games.negative.mines.config;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import games.negative.alumina.AluminaPlugin;
import games.negative.alumina.event.Events;
import games.negative.alumina.util.FileLoader;
import games.negative.mines.api.event.ConfigurationReloadEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Set;

public abstract class UPMConfiguration {

    public static Set<UPMConfiguration> REGISTRY = Sets.newHashSet();

    private final File file;
    private FileConfiguration config;

    public UPMConfiguration(@NotNull String name) {
        this.file = FileLoader.loadFile(AluminaPlugin.getAluminaInstance(), name);
        Preconditions.checkNotNull(file, "File cannot be null");

        this.config = YamlConfiguration.loadConfiguration(file);

        REGISTRY.add(this);
        onReload(config);
    }

    public abstract void onReload(@NotNull FileConfiguration config);

    @NotNull
    public File file() {
        return file;
    }

    @NotNull
    public FileConfiguration config() {
        return config;
    }

    public static void reloadAll() {
        for (UPMConfiguration config : REGISTRY) {
            File file = config.file;
            FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);

            config.config = yaml;
            config.onReload(yaml);

            ConfigurationReloadEvent event = new ConfigurationReloadEvent(config);
            Events.call(event);
        }
    }

}
