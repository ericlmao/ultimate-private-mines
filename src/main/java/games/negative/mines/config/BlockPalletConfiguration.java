package games.negative.mines.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class BlockPalletConfiguration extends UPMConfiguration {

    @Getter
    private static FileConfiguration config;

    public BlockPalletConfiguration() {
        super("block-pallets.yml");
    }

    @Override
    public void onReload(@NotNull FileConfiguration config) {
        BlockPalletConfiguration.config = config;
    }
}
