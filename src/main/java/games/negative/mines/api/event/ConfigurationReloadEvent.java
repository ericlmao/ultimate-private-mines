package games.negative.mines.api.event;

import games.negative.mines.config.UPMConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ConfigurationReloadEvent extends Event {

    private final UPMConfiguration config;
    private static final HandlerList handlers = new HandlerList();

    public ConfigurationReloadEvent(@NotNull UPMConfiguration config) {
        this.config = config;
    }

    @NotNull
    public UPMConfiguration config() {
        return config;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
