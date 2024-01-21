package games.negative.mines.core.provider;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import games.negative.alumina.event.Events;
import games.negative.alumina.future.BukkitCompletableFuture;
import games.negative.alumina.future.BukkitFuture;
import games.negative.alumina.util.NBTEditor;
import games.negative.alumina.util.Tasks;
import games.negative.mines.UPMPlugin;
import games.negative.mines.api.MineManager;
import games.negative.mines.api.event.ConfigurationReloadEvent;
import games.negative.mines.api.model.MineRegion;
import games.negative.mines.api.model.Position;
import games.negative.mines.api.model.PrivateMine;
import games.negative.mines.api.model.schematic.PasteSpecifications;
import games.negative.mines.api.model.schematic.PrivateMineSchematic;
import games.negative.mines.config.MineConfiguration;
import games.negative.mines.core.structure.UPMMine;
import games.negative.mines.core.util.MineLoader;
import games.negative.mines.task.MinePasteTask;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class UPMMineManagerProvider implements MineManager {

    private final UPMPlugin plugin;
    private final Map<UUID, PrivateMine> mines;
    private final Set<PrivateMineSchematic> schematics;
    private final LoadingCache<UUID, PrivateMine> cache;

    private final World world;

    private final NamespacedKey GRID_X;
    private final NamespacedKey GRID_Z;

    public UPMMineManagerProvider(@NotNull UPMPlugin plugin) {
        this.plugin = plugin;

        this.mines = MineLoader.loadMines(plugin);
        mines.values().forEach(privateMine -> privateMine.onInitialization(plugin));

        this.cache = CacheBuilder.newBuilder().expireAfterWrite(Duration.of(30, ChronoUnit.SECONDS))
                .build(new CacheLoader<>() {
                    @Override
                    public @NotNull PrivateMine load(@NotNull UUID key) throws Exception {
                        for (PrivateMine mine : mines.values()) {
                            if (!mine.isMember(key)) continue;

                            return mine;
                        }
                        throw new NullPointerException("No mine found for UUID " + key);
                    }
                });

        // Debug for local cache
        Logger logger = plugin.getLogger();
        Tasks.run(() -> {
            StringBuilder builder = new StringBuilder();
            builder.append("Local Mine Cache:").append("\n");
            for (Map.Entry<UUID, PrivateMine> entry : cache.asMap().entrySet()) {
                builder.append("UUID: ").append(entry.getKey()).append(" Mine: ").append(entry.getValue().getName()).append("\n");
            }

            logger.info(builder.toString());
        }, 20 * 10, 20);
//        for (PrivateMine mine : mines.values()) {
//
//            for (UUID uuid : mine.members()) {
//                cache.put(uuid, mine);
//            }
//        }

        this.world = MineConfiguration.getWorld();

        this.GRID_X = new NamespacedKey(plugin, "grid-x");
        this.GRID_Z = new NamespacedKey(plugin, "grid-z");

        if (!NBTEditor.has(world, GRID_X, PersistentDataType.INTEGER)) {
            NBTEditor.set(world, GRID_X, PersistentDataType.INTEGER, 0);
            logger.info("Created GRID_X key for world " + world.getName());
        }

        if (!NBTEditor.has(world, GRID_Z, PersistentDataType.INTEGER)) {
            NBTEditor.set(world, GRID_Z, PersistentDataType.INTEGER, 0);
            logger.info("Created GRID_Z key for world " + world.getName());
        }

        this.schematics = Sets.newHashSet();
        loadSchematics();

        Events.listen(ConfigurationReloadEvent.class, event -> {
            if (!(event.config() instanceof MineConfiguration)) return;

            loadSchematics();
        });
    }

    private void loadSchematics() {
        this.schematics.clear();

        FileConfiguration config = MineConfiguration.getConfig();
        ConfigurationSection schematicsSection = config.getConfigurationSection("schematics");
        Preconditions.checkNotNull(schematicsSection, "`schematics` section cannot be null! (mines.yml)");

        File dir = new File(plugin.getDataFolder(), "schematics");
        if (!dir.exists()) dir.mkdirs();

        for (String key : schematicsSection.getKeys(false)) {
            ConfigurationSection section = schematicsSection.getConfigurationSection(key);
            if (section == null) continue;

            boolean def = section.getBoolean("default", false);

            String fileName = section.getString("file");
            Preconditions.checkNotNull(fileName, "'file' cannot be null in schematic `" + key + "` (mines.yml)");
            Preconditions.checkArgument(fileName.endsWith(".schem") || fileName.endsWith(".schematic"), "'file' must end with .schem or .schematic in schematic `" + key + "` (mines.yml)");

            File file = new File(dir, fileName);
            Preconditions.checkArgument(file.exists(), "File " + fileName + " does not exist in schematic `" + key + "` (mines.yml)");

            
        }
    }

    @Override
    public @NotNull PrivateMine create(@NotNull UUID owner, @NotNull PrivateMineSchematic schematic) {
        for (PrivateMine search : mines.values()) {
            if (search.isMember(owner)) throw new IllegalStateException("Player " + owner + " already has a mine or is a member of one");
        }

        // Generate a unique id.
        UUID unique = UUID.randomUUID();
        while (mines.containsKey(unique)) {
            unique = UUID.randomUUID();
        }

        // Find the next grid position
        int range = MineConfiguration.getDistanceBetweenMines();
        int distance = MineConfiguration.getGridSize();

        int gridX = NBTEditor.get(world, GRID_X, PersistentDataType.INTEGER);
        int gridZ = NBTEditor.get(world, GRID_Z, PersistentDataType.INTEGER);

        // Calculate the next grid positions
        int x = gridX + distance;
        int z = gridZ;
        if (x > range){
            x = 0;
            z += distance;

            if (z > range) throw new IllegalStateException("No more space for mines!");
        }

        // Update grid positions in the world
        NBTEditor.set(world, GRID_X, PersistentDataType.INTEGER, x);
        NBTEditor.set(world, GRID_Z, PersistentDataType.INTEGER, z);

        // Aline specifications
        PasteSpecifications specifications = schematic.specifications();
        int y = specifications.y();

        PasteSpecifications.MineLocationRelative min = specifications.regionRelativeMinimum();
        PasteSpecifications.MineLocationRelative max = specifications.regionRelativeMaximum();

        Location paste = new Location(world, x, y, z);
        Block pasteBlock = paste.getBlock();

        Block minBlock = pasteBlock.getRelative(min.x(), min.y(), min.z());
        Block maxBlock = pasteBlock.getRelative(max.x(), max.y(), max.z());

        MineRegion region = new MineRegion(world, minBlock, maxBlock);

        PasteSpecifications.MinePositionRelative spawnRelative = specifications.spawnRelative();
        Block spawnBlock = pasteBlock.getRelative((int) spawnRelative.x(), (int) spawnRelative.y(), (int) spawnRelative.z());

        Position spawn = new Position(world, spawnBlock.getX(), spawnBlock.getY(), spawnBlock.getZ(), spawnRelative.yaw(), spawnRelative.pitch());

        PrivateMine mine = new UPMMine(unique, owner, region, spawn);
        mine.setReady(false);

        mines.put(unique, mine);

        new MinePasteTask(mine, paste, schematic, plugin).runTaskAsynchronously(plugin);

        return mine;
    }

    @Override
    public @NotNull Stream<PrivateMine> stream() {
        return mines.values().stream();
    }

    @Override
    public @NotNull Optional<PrivateMine> getMine(@NotNull UUID uuid) {
        return Optional.ofNullable(mines.get(uuid));
    }

    @Override
    public @NotNull Optional<PrivateMine> getMine(@NotNull Player player) {
        try {
            PrivateMine privateMine = cache.get(player.getUniqueId());
            return Optional.of(privateMine);
        } catch (ExecutionException e) {
            plugin.getLogger().severe("Failed to get mine for player " + player.getName());
            return Optional.empty();
        }
    }

    @Override
    public BukkitFuture<Void> save(@NotNull PrivateMine mine) {
        BukkitFuture<Void> future = new BukkitCompletableFuture<>();
        future.supplyAsync(() -> {
            MineLoader.saveMine(plugin, mine);
            return null;
        });
        return future;
    }

    @Override
    public Optional<PrivateMineSchematic> getSchematic(@NotNull String key) {
        return Optional.empty();
    }

    @Override
    public @NotNull Collection<PrivateMine> getMines() {
        return mines.values();
    }

    @Override
    public @NotNull Collection<PrivateMineSchematic> getSchematics() {
        return null;
    }
}
