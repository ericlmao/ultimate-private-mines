package games.negative.mines.api;

import games.negative.mines.api.model.BlockPallet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface BlockPallets {

    Optional<BlockPallet> getPallet(@NotNull String key);

    void addPallet(@NotNull BlockPallet pallet);

    void removePallet(@NotNull String key);

    @NotNull
    Collection<BlockPallet> pallets();

}
