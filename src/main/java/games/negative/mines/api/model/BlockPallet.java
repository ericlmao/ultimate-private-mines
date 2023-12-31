package games.negative.mines.api.model;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface BlockPallet {

    void addEntry(@NotNull Material material, double chance);

    double getEntry(@NotNull Material material);

    @NotNull
    Map<Material, Double> getPallet();

}
