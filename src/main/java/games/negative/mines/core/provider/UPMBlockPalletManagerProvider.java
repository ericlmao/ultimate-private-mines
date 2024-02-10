package games.negative.mines.core.provider;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import games.negative.alumina.event.Events;
import games.negative.alumina.logger.Logs;
import games.negative.mines.api.BlockPalletManager;
import games.negative.mines.api.event.ConfigurationReloadEvent;
import games.negative.mines.api.model.BlockPallet;
import games.negative.mines.config.BlockPalletConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class UPMBlockPalletManagerProvider implements BlockPalletManager {

    private final Set<BlockPallet> pallets;

    public UPMBlockPalletManagerProvider() {
        this.pallets = Sets.newHashSet();
        loadPallets();

        Events.listen(ConfigurationReloadEvent.class, event -> {
            if (!(event.config() instanceof BlockPalletConfiguration)) return;

            loadPallets();
        });
    }

    public void loadPallets() {
        this.pallets.clear();

        FileConfiguration config = BlockPalletConfiguration.getConfig();
        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;

            boolean defaultPallet = section.getBoolean("default", false);
            String rawPermission = section.getString("permission", "upm.pallet." + key.toLowerCase().replaceAll(" ", "-"));

            Permission permission = new Permission(rawPermission);

            PluginManager manager = Bukkit.getPluginManager();
            if (manager.getPermission(permission.getName()) == null)
                manager.addPermission(permission);

            Map<Material, Double> pallet = Maps.newHashMap();

            ConfigurationSection palletSection = section.getConfigurationSection("pallet");
            Preconditions.checkNotNull(palletSection, "Pallet section cannot be null");

            for (String mat : palletSection.getKeys(false)) {
                Material material = Material.valueOf(mat.toUpperCase());
                double entry = palletSection.getDouble(mat);

                pallet.put(material, entry);
            }

            BlockPallet blockPallet = new BlockPallet(key, pallet, permission, defaultPallet);
            pallets.add(blockPallet);

            Logs.INFO.print("Loaded block pallet: " + key);
        }
    }

    @Override
    public Optional<BlockPallet> getPallet(@NotNull String key) {
        return pallets.stream().filter(blockPallet -> blockPallet.key().equalsIgnoreCase(key)).findFirst();
    }

    @Override
    public Collection<BlockPallet> getPallets() {
        return pallets;
    }
}
