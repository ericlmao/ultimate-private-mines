package games.negative.mines.core.provider;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Sets;
import games.negative.alumina.builder.ItemBuilder;
import games.negative.alumina.event.Events;
import games.negative.alumina.future.BukkitCompletableFuture;
import games.negative.alumina.future.BukkitFuture;
import games.negative.alumina.logger.Logs;
import games.negative.alumina.util.NBTEditor;
import games.negative.alumina.util.Tasks;
import games.negative.mines.UPMPlugin;
import games.negative.mines.api.BlockPalletManager;
import games.negative.mines.api.MineManager;
import games.negative.mines.api.event.ConfigurationReloadEvent;
import games.negative.mines.api.model.Mine;
import games.negative.mines.api.model.MineRegion;
import games.negative.mines.api.model.Position;
import games.negative.mines.api.model.schematic.PasteSpecifications;
import games.negative.mines.api.model.schematic.PrivateMineSchematic;
import games.negative.mines.config.MineConfiguration;
import games.negative.mines.core.structure.UPMMine;
import games.negative.mines.core.structure.UPMPasteSpecifications;
import games.negative.mines.core.structure.UPMSchematic;
import games.negative.mines.core.util.MineLoader;
import games.negative.mines.task.MinePasteTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class UPMMineManagerProvider implements MineManager {

    private final UPMPlugin plugin;
    private final Map<UUID, Mine> mines;
    private final Set<PrivateMineSchematic> schematics;
    private final LoadingCache<UUID, Mine> cache;

    private final World world;

    private final NamespacedKey GRID_X;
    private final NamespacedKey GRID_Z;

    private final BlockPalletManager pallets;

    public UPMMineManagerProvider(@NotNull UPMPlugin plugin, @NotNull BlockPalletManager pallets) {
        this.plugin = plugin;
        this.pallets = pallets;

        this.schematics = Sets.newHashSet();
        loadSchematics();

        this.mines = MineLoader.loadMines(plugin);
        mines.values().forEach(privateMine -> privateMine.onInitialization(plugin, pallets, this));

        this.cache = CacheBuilder.newBuilder().expireAfterWrite(Duration.of(30, ChronoUnit.SECONDS))
                .build(new CacheLoader<>() {
                    @Override
                    public @NotNull Mine load(@NotNull UUID key) throws Exception {
                        for (Mine mine : mines.values()) {
                            if (!mine.isMember(key)) continue;

                            return mine;
                        }
                        throw new Exception("No mine found for UUID " + key);
                    }
                });

        this.world = MineConfiguration.getWorld();

        this.GRID_X = new NamespacedKey(plugin, "grid-x");
        this.GRID_Z = new NamespacedKey(plugin, "grid-z");

        if (!NBTEditor.has(world, GRID_X, PersistentDataType.INTEGER)) {
            NBTEditor.set(world, GRID_X, PersistentDataType.INTEGER, 0);
            Logs.INFO.print("Created GRID_X key for world " + world.getName());
        }

        if (!NBTEditor.has(world, GRID_Z, PersistentDataType.INTEGER)) {
            NBTEditor.set(world, GRID_Z, PersistentDataType.INTEGER, 0);
            Logs.INFO.print("Created GRID_Z key for world " + world.getName());
        }

        Events.listen(PlayerTeleportEvent.class, event -> {
            if (event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN || event.getTo() == null) return;

            Player player = event.getPlayer();

            Location to = event.getTo();

            World toWorld = to.getWorld();
            if (toWorld == null || !toWorld.equals(world)) {
                player.setWorldBorder(null);
                return;
            }

            for (Mine mine : getMines()) {
                WorldBorder border = mine.border();
                if (!border.isInside(to)) continue;

                player.setWorldBorder(border);
                break;
            }
        });

        Events.listen(PlayerJoinEvent.class, event -> {
            Player player = event.getPlayer();
            Location location = player.getLocation();

            World world = location.getWorld();
            if (world == null || !world.equals(this.world)) return;

            for (Mine mine : getMines()) {
                WorldBorder border = mine.border();
                if (!border.isInside(location)) continue;

                Tasks.run(() -> player.setWorldBorder(border), 2);
                break;
            }
        });

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

            int border = section.getInt("border-size", 200);

            ConfigurationSection iconSection = section.getConfigurationSection("icon");
            Preconditions.checkNotNull(iconSection, "'icon' cannot be null in schematic `" + key + "` (mines.yml)");

            String display = iconSection.getString("display-name", "&a" + key);
            Material material = Material.valueOf(iconSection.getString("material", "GRASS_BLOCK"));
            List<String> lore = iconSection.getStringList("lore");

            ItemStack icon = new ItemBuilder(material).setName(display).setLore(lore).build();

            ConfigurationSection pasteSection = section.getConfigurationSection("paste");
            Preconditions.checkNotNull(pasteSection, "'paste' cannot be null in schematic `" + key + "` (mines.yml)");

            int pasteY = pasteSection.getInt("paste-y", 60);

            ConfigurationSection regionSection = pasteSection.getConfigurationSection("mine-region-relative");
            Preconditions.checkNotNull(regionSection, "'mine-region-relative' cannot be null in schematic `" + key + "` (mines.yml)");

            int minX = regionSection.getInt("min-x");
            int minY = regionSection.getInt("min-y");
            int minZ = regionSection.getInt("min-z");
            int maxX = regionSection.getInt("max-x");
            int maxY = regionSection.getInt("max-y");
            int maxZ = regionSection.getInt("max-z");

            PasteSpecifications.MineLocationRelative min = new PasteSpecifications.MineLocationRelative(minX, minY, minZ);
            PasteSpecifications.MineLocationRelative max = new PasteSpecifications.MineLocationRelative(maxX, maxY, maxZ);

            ConfigurationSection spawnSection = pasteSection.getConfigurationSection("mine-spawn-relative");
            Preconditions.checkNotNull(spawnSection, "'mine-spawn-relative' cannot be null in schematic `" + key + "` (mines.yml)");

            int spawnX = spawnSection.getInt("x");
            int spawnY = spawnSection.getInt("y");
            int spawnZ = spawnSection.getInt("z");
            float spawnYaw = (float) spawnSection.getDouble("yaw");
            float spawnPitch = (float) spawnSection.getDouble("pitch");

            PasteSpecifications.MinePositionRelative spawn = new PasteSpecifications.MinePositionRelative(spawnX, spawnY, spawnZ, spawnYaw, spawnPitch);

            PasteSpecifications specifications = new UPMPasteSpecifications(pasteY, min, max, spawn);
            PrivateMineSchematic schematic = new UPMSchematic(key, file, def, border, icon, specifications);
            schematics.add(schematic);

            Logs.INFO.print("Loaded Private Mine Schematic: " + key);
        }
    }

    @Override
    public @NotNull Mine create(@NotNull UUID owner, @NotNull PrivateMineSchematic schematic) {
        for (Mine search : mines.values()) {
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
        int x = gridX + range;
        int z = gridZ;
        if (x > distance){
            x = 0;
            z += range;

            if (z > distance) throw new IllegalStateException("No more space for mines!");
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

        Position spawn = new Position(world, spawnBlock.getX() + 0.5, spawnBlock.getY(), spawnBlock.getZ() + 0.5, spawnRelative.yaw(), spawnRelative.pitch());

        Mine mine = new UPMMine(unique, owner, region, spawn, schematic);
        mine.setReady(false);
        mine.onInitialization(plugin, pallets, this);

        mines.put(unique, mine);

        Logs.INFO.print("Create new mine for owner " + owner + " with schematic " + schematic.key() + " with mine-id " + unique);

        new MinePasteTask(mine, paste, schematic, plugin).runTaskAsynchronously(plugin);

        return mine;
    }

    @Override
    public @NotNull Stream<Mine> stream() {
        return mines.values().stream();
    }

    @Override
    public @NotNull Optional<Mine> getMine(@NotNull UUID uuid) {
        return Optional.ofNullable(mines.get(uuid));
    }

    @Override
    public @NotNull Optional<Mine> getMine(@NotNull Player player) {
        try {
            Mine mine = cache.get(player.getUniqueId());
            return Optional.of(mine);
        } catch (ExecutionException e) {
            Logs.WARNING.print("Could not find mine for player " + player.getName());
            return Optional.empty();
        }
    }

    @Override
    public BukkitFuture<Void> save(@NotNull Mine mine) {
        BukkitFuture<Void> future = new BukkitCompletableFuture<>();
        future.supplyAsync(() -> {
            MineLoader.saveMine(plugin, mine);
            return null;
        });
        return future;
    }

    @Override
    public void saveSync(@NotNull Mine mine) {
        MineLoader.saveMine(plugin, mine);
    }

    @Override
    public Optional<PrivateMineSchematic> getSchematic(@NotNull String key) {
        return schematics.stream().filter(schematic -> schematic.key().equalsIgnoreCase(key)).findFirst();
    }

    @Override
    public @NotNull Collection<Mine> getMines() {
        return mines.values();
    }

    @Override
    public @NotNull Collection<PrivateMineSchematic> getSchematics() {
        return schematics;
    }
}
