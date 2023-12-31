package games.negative.mines.api.model.position;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Locatable {

    @NotNull
    Location toLocation();

    default void teleport(@NotNull Player player, @Nullable Sound sound) {
        player.teleport(toLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);

        if (sound == null) return;

        player.playSound(player, sound, 1, 1);
    }

}
