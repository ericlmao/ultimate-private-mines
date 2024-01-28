package games.negative.mines.task;

import com.google.common.base.Preconditions;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import games.negative.mines.UPMPlugin;
import games.negative.mines.api.model.Mine;
import games.negative.mines.api.model.schematic.PrivateMineSchematic;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

@RequiredArgsConstructor
public class MinePasteTask extends BukkitRunnable {

    private final Mine mine;
    private final Location location;
    private final PrivateMineSchematic schematic;
    private final UPMPlugin plugin;

    @Override
    public void run() {
        Instant start = Instant.now();
        File file = schematic.file();

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        Preconditions.checkNotNull(format, "Clipboard format not found for file " + file.getName());

        Clipboard clipboard;

        BlockVector3 paste = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            org.bukkit.World bukkitWorld = location.getWorld();
            Preconditions.checkNotNull(bukkitWorld, "World not found for location " + location.toString());

            World world = BukkitAdapter.adapt(bukkitWorld);

            try (EditSession session = WorldEdit.getInstance().newEditSessionBuilder().world(world).build()) {
                clipboard = reader.read();

                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(session)
                        .to(paste)
                        .ignoreAirBlocks(true)
                        .copyBiomes(true)
                        .copyEntities(false)
                        .build();

                try {
                    Operations.complete(operation);
                    session.close();

                    Instant end = Instant.now();

                    Duration total = Duration.between(start, end);

                    new MinePasteEndTask(mine, total).runTaskLater(plugin, 5L);
                } catch (WorldEditException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
