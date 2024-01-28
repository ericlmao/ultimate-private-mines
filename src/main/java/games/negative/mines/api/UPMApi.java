package games.negative.mines.api;

import org.jetbrains.annotations.NotNull;

public interface UPMApi {

    @NotNull
    BlockPalletManager pallets();

    @NotNull
    MineManager mines();

}
