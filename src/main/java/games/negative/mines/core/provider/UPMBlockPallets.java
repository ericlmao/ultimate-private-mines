package games.negative.mines.core.provider;

import com.google.common.collect.Maps;
import games.negative.alumina.model.registry.Registry;
import games.negative.alumina.model.registry.SimpleRegistry;
import games.negative.alumina.util.FileLoader;
import games.negative.mines.UPMPlugin;
import games.negative.mines.api.BlockPallets;
import games.negative.mines.api.model.BlockPallet;
import games.negative.mines.core.structure.UPMBlockPallet;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class UPMBlockPallets implements BlockPallets {

    private final Registry<String, BlockPallet> pallets;

    public UPMBlockPallets(@NotNull UPMPlugin plugin) {
        this.pallets = loadPallets(plugin);
    }

    @Override
    public Optional<BlockPallet> getPallet(@NotNull String key) {
        return Optional.ofNullable(this.pallets.get(key));
    }

    @Override
    public void addPallet(@NotNull BlockPallet pallet) {
        this.pallets.put(pallet.key(), pallet);
    }

    @Override
    public void removePallet(@NotNull String key) {
        this.pallets.remove(key);
    }

    @Override
    public @NotNull Collection<BlockPallet> pallets() {
        return pallets.values();
    }

    private Registry<String, BlockPallet> loadPallets(@NotNull UPMPlugin plugin) {
        Registry<String, BlockPallet> pallets = new SimpleRegistry<>();

        FileConfiguration config = FileLoader.loadFileConfiguration(plugin, "block-pallets.yml");
        if (config == null) return pallets;

        for (String key : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(key);
            if (section == null) continue;

            String name = key.toLowerCase();

            Map<Material, Double> pallet = Maps.newHashMap();
            for (String mat : section.getKeys(false)) {
                Material material = Material.getMaterial(mat);
                if (material == null) continue;

                double chance = section.getDouble(mat);

                pallet.put(material, chance);
            }

            pallets.put(name, new UPMBlockPallet(name, pallet));
        }

        return pallets;
    }
}
