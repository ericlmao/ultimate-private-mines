package games.negative.mines.api.event;

import games.negative.alumina.event.PluginEvent;
import games.negative.mines.config.UPMConfiguration;
import org.jetbrains.annotations.NotNull;

public class ConfigurationReloadEvent extends PluginEvent {

    private final UPMConfiguration config;

    public ConfigurationReloadEvent(@NotNull UPMConfiguration config) {
        this.config = config;
    }

    @NotNull
    public UPMConfiguration config() {
        return config;
    }

}
