package games.negative.mines.api.model.position;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public record DefinedPosition(@NotNull World world, double x, double y, double z, float yaw, float pitch) implements Locatable {

    @Override
    public @NotNull Location toLocation() {
        return new Location(world, x, y, z, yaw, pitch);
    }

    @Override
    public String toString() {
        return "DefinedPosition{" +
                "world=" + world.getName() +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                '}';
    }
}
