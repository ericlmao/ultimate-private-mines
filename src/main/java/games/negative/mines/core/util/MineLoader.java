package games.negative.mines.core.util;

import com.google.common.collect.Maps;
import games.negative.alumina.util.JsonUtil;
import games.negative.mines.UPMPlugin;
import games.negative.mines.api.model.PrivateMine;
import games.negative.mines.core.structure.UPMMine;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

@UtilityClass
public class MineLoader {

    @NotNull
    public Map<UUID, PrivateMine> loadMines(@NotNull UPMPlugin plugin) {
        Map<UUID, PrivateMine> mines = Maps.newHashMap();

        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        File directory = new File(dataFolder, "mines");
        if (!directory.exists()) directory.mkdirs();

        String[] list = directory.list((dir, name) -> name.endsWith(".json"));
        if (list == null) return mines;

        for (String name : list) {
            File file = new File(directory, name);
            if (!file.exists()) continue;

            PrivateMine mine = loadMine(file);
            if (mine == null) {
                plugin.getLogger().severe("Failed to load mine " + name + "! Deleting file...");
                file.delete();
                continue;
            }

            mines.put(mine.uuid(), mine);
        }

        return mines;
    }

    @Nullable
    public PrivateMine loadMine(@NotNull File file) {
        try {
            return JsonUtil.load(file, UPMMine.class, UPMPlugin.GSON);
        } catch (IOException e) {
            return null;
        }
    }

    public void saveMine(@NotNull UPMPlugin plugin, @NotNull PrivateMine mine) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        File directory = new File(dataFolder, "mines");
        if (!directory.exists()) directory.mkdirs();

        UUID uuid = mine.uuid();
        File file = new File(directory, uuid.toString() + ".json");

        Logger logger = plugin.getLogger();
        try {
            JsonUtil.save(mine, file, UPMPlugin.GSON);
            logger.info("Saved mine " + uuid + "!");
        } catch (IOException e) {
            logger.severe("Failed to save mine " + uuid + "!");
        }
    }

}
