package games.negative.mines.api.model;

import games.negative.alumina.model.Keyd;
import games.negative.alumina.permission.Permissible;
import org.bukkit.Material;
import org.bukkit.permissions.Permission;

import java.util.Map;

public record BlockPallet(String key, Map<Material, Double> pallet, Permission permission, boolean defaultPallet) implements Keyd<String>, Permissible {
    @Override
    public Permission getPermission() {
        return null;
    }
}
