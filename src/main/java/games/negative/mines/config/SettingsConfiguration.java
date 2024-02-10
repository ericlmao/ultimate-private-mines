package games.negative.mines.config;

import games.negative.alumina.logger.Logs;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class SettingsConfiguration extends UPMConfiguration {
    public SettingsConfiguration() {
        super("settings.yml");
    }

    @Override
    public void onReload(@NotNull FileConfiguration config) {
        Logs.setDisabled(!config.getBoolean("logger-enabled", true));
    }
}
