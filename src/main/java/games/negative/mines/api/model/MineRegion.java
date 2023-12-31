package games.negative.mines.api.model;

import com.google.common.collect.Lists;
import games.negative.mines.api.model.position.Position;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.function.Predicate;

public interface MineRegion {

    @NotNull
    World world();

    @NotNull
    Position getMinimum();

    @NotNull
    Position getMaximum();

    void increase(int x, int y, int z);

    default boolean contains(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return false;

        Position minimum = getMinimum();
        Position maximum = getMaximum();

        return world.equals(world()) &&
                location.getX() >= minimum.x() && location.getX() <= maximum.x() &&
                location.getY() >= minimum.y() && location.getY() <= maximum.y() &&
                location.getZ() >= minimum.z() && location.getZ() <= maximum.z();
    }

    default List<Block> getBlocks(@Nullable Predicate<Block> filter) {
        Location minimum = new Location(world(), getMinimum().x(), getMinimum().y(), getMinimum().z());
        Location maximum = new Location(world(), getMaximum().x(), getMaximum().y(), getMaximum().z());

        List<Block> blocks = Lists.newArrayList();

        for (int x = minimum.getBlockX(); x <= maximum.getBlockX(); x++) {
            for (int y = minimum.getBlockY(); y <= maximum.getBlockY(); y++) {
                for (int z = minimum.getBlockZ(); z <= maximum.getBlockZ(); z++) {
                    Block block = world().getBlockAt(x, y, z);
                    if (filter != null && !filter.test(block)) continue;

                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    @NotNull
    default Block getCenter() {
        Position minimum = getMinimum();
        Position maximum = getMaximum();

        return world().getBlockAt((minimum.x() + maximum.x()) / 2, (minimum.y() + maximum.y()) / 2, (minimum.z() + maximum.z()) / 2);
    }

}
