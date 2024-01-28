package games.negative.mines.core.provider;

import games.negative.mines.UPMPlugin;
import games.negative.mines.api.BlockPalletManager;
import games.negative.mines.api.MineManager;
import games.negative.mines.api.UPMApi;
import org.jetbrains.annotations.NotNull;

public class UltimatePrivateMinesAPI implements UPMApi {

    private final BlockPalletManager pallets;
    private final MineManager mines;

    public UltimatePrivateMinesAPI(@NotNull UPMPlugin plugin) {
        this.pallets = new UPMBlockPalletManagerProvider();
        this.mines = new UPMMineManagerProvider(plugin, pallets);
    }

    @Override
    public @NotNull BlockPalletManager pallets() {
        return pallets;
    }

    @Override
    public @NotNull MineManager mines() {
        return mines;
    }
}
