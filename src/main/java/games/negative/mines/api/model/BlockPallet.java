package games.negative.mines.api.model;

import games.negative.alumina.model.Keyd;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface BlockPallet extends Keyd<String> {

    void addEntry(@NotNull Material material, double chance);

    double getEntry(@NotNull Material material);

    @NotNull
    Map<Material, Double> getPallet();

}
