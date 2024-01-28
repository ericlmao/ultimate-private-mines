package games.negative.mines.core.structure;

import games.negative.mines.api.model.schematic.PasteSpecifications;
import games.negative.mines.api.model.schematic.PrivateMineSchematic;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@RequiredArgsConstructor
public class UPMSchematic implements PrivateMineSchematic {

    private final String key;
    private final File file;
    private final boolean def;
    private final int borderSize;
    private final ItemStack icon;
    private final PasteSpecifications specifications;

    @Override
    public boolean isDefault() {
        return def;
    }

    @Override
    public File file() {
        return file;
    }

    @Override
    public int borderSize() {
        return borderSize;
    }

    @Override
    public ItemStack icon() {
        return icon;
    }

    @Override
    public PasteSpecifications specifications() {
        return specifications;
    }

    @Override
    public @NotNull String key() {
        return key;
    }
}
