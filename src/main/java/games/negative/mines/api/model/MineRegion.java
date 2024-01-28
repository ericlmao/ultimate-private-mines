package games.negative.mines.api.model;

import com.google.common.base.Preconditions;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

@Data
public class MineRegion {

    @Expose
    @SerializedName("world")
    private final World world;

    @Expose
    @SerializedName("min-x")
    private int minX;

    @Expose
    @SerializedName("min-y")
    private int minY;

    @Expose
    @SerializedName("min-z")
    private int minZ;

    @Expose
    @SerializedName("max-x")
    private int maxX;

    @Expose
    @SerializedName("max-y")
    private int maxY;

    @Expose
    @SerializedName("max-z")
    private int maxZ;

    public MineRegion(@NotNull World world, @NotNull Block min, @NotNull Block max) {
        this.world = world;
        this.minX = min.getX();
        this.minY = min.getY();
        this.minZ = min.getZ();
        this.maxX = max.getX();
        this.maxY = max.getY();
        this.maxZ = max.getZ();
    }

    @NotNull
    public Block getCenter() {
        return world.getBlockAt((minX + maxX) / 2, (minY + maxY) / 2, (minZ + maxZ) / 2);
    }

    public void increase(int x, int y, int z) {
        this.maxX += x;
        this.maxY += y;
        this.maxZ += z;

        this.minX -= x;
        this.minY -= y;
        this.minZ -= z;
    }

    @NotNull
    public Location min() {
        return new Location(world, minX, minY, minZ);
    }

    @NotNull
    public Location max() {
        return new Location(world, maxX, maxY, maxZ);
    }

    public boolean contains(double x, double y, double z) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public boolean contains(@NotNull Location location) {
        World world = location.getWorld();
        Preconditions.checkNotNull(world, "World cannot be null");

        return (this.world.equals(world) && contains(location.getX(), location.getY(), location.getZ()));
    }
}
