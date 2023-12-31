package games.negative.mines.core.structure;

import games.negative.mines.api.model.BlockPallet;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record UPMBlockPallet(String key, Map<Material, Double> pallet) implements BlockPallet {

    public UPMBlockPallet(@NotNull String key, @NotNull Map<Material, Double> pallet) {
        this.key = key;
        this.pallet = pallet;
    }

    @Override
    public void addEntry(@NotNull Material material, double chance) {
        pallet.put(material, chance);
    }

    @Override
    public double getEntry(@NotNull Material material) {
        return pallet.getOrDefault(material, 0.0D);
    }

    @Override
    public @NotNull Map<Material, Double> pallet() {
        return pallet;
    }

    @Override
    public @NotNull String key() {
        return key;
    }
}
