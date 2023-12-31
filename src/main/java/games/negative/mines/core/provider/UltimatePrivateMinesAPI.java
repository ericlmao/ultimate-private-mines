package games.negative.mines.core.provider;

import games.negative.mines.UPMPlugin;
import games.negative.mines.api.BlockPallets;
import games.negative.mines.api.MinesAPI;
import org.jetbrains.annotations.NotNull;

public class UltimatePrivateMinesAPI implements MinesAPI {

    private final BlockPallets blockPallets;

    public UltimatePrivateMinesAPI(@NotNull UPMPlugin plugin) {
        this.blockPallets = new UPMBlockPallets(plugin);
    }

    @Override
    public @NotNull BlockPallets getBlockPallets() {
        return blockPallets;
    }
}
