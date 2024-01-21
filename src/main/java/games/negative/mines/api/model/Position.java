package games.negative.mines.api.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record Position(
        @Expose
        @SerializedName("world")
        World world,

        @Expose
        @SerializedName("x")
        double x,

        @Expose
        @SerializedName("y")
        double y,

        @Expose
        @SerializedName("z")
        double z,

        @Expose
        @SerializedName("yaw")
        float yaw,

        @Expose
        @SerializedName("pitch")
        float pitch

) {

    @NotNull
    public Location toLocation() {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public void teleport(@NotNull Player player, @Nullable Sound sound) {
        player.teleport(toLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        if (sound == null) return;

        player.playSound(player, sound, 1, 1);
    }


}
