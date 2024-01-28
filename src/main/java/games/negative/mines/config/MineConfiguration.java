package games.negative.mines.config;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class MineConfiguration extends UPMConfiguration {

    // Mine World for generating mines
    @Getter
    private static World world;

    // Invitation duration in seconds
    @Getter
    private static int inviteDuration;

    // Total grid size for mines to be generated
    @Getter
    private static int gridSize;

    // Distance between mines in blocks
    @Getter
    private static int distanceBetweenMines;

    @Getter
    private static FileConfiguration config;

    public MineConfiguration() {
        super("mines.yml");
    }

    @Override
    public void onReload(@NotNull FileConfiguration config) {
        MineConfiguration.config = config;
        
        String worldRaw = config.getString("world", "world");
        world = Bukkit.getWorld(worldRaw);
        if (world == null) throw new IllegalStateException("World " + worldRaw + " not found (mines.yml)");

        inviteDuration = config.getInt("invitation-duration", 60);

        gridSize = config.getInt("grid-size", 100000);

        distanceBetweenMines = config.getInt("distance-between", 1000);
    }

}
