package games.negative.mines.api.model.schematic;

import games.negative.alumina.model.Keyd;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public interface PrivateMineSchematic extends Keyd<String> {

    boolean isDefault();

    File file();

    int borderSize();

    ItemStack icon();

    PasteSpecifications specifications();
}
