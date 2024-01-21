package games.negative.mines.api.model;

import games.negative.alumina.model.Keyd;
import games.negative.alumina.permission.Permissible;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface BlockPallet extends Keyd<String>, Permissible {

    double getEntry(@NotNull Material material);

    @NotNull
    Map<Material, Double> pallet();

    boolean isDefaultPallet();

}
