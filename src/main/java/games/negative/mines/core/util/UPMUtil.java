package games.negative.mines.core.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class UPMUtil {

    public static boolean isOnline(@NotNull UUID uuid) {
        return Bukkit.getPlayer(uuid) != null;
    }
}
