package games.negative.mines.core.structure;

import games.negative.mines.api.model.BlockPallet;
import org.bukkit.Material;
import org.bukkit.permissions.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record UPMBlockPallet(String key, Permission permission, Map<Material, Double> pallet, boolean def) implements BlockPallet {

    @Override
    public double getEntry(@NotNull Material material) {
        return pallet.getOrDefault(material, 0D);
    }

    @Override
    public @NotNull Map<Material, Double> pallet() {
        return pallet;
    }

    @Override
    public boolean isDefaultPallet() {
        return def;
    }

    @Override
    public @NotNull String key() {
        return key;
    }

    @Override
    public Permission getPermission() {
        return permission;
    }

}
