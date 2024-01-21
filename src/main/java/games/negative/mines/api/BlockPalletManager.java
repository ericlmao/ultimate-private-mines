package games.negative.mines.api;

import games.negative.mines.api.model.BlockPallet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public interface BlockPalletManager {

    Optional<BlockPallet> getPallet(@NotNull String key);

    Collection<BlockPallet> getPallets();

}
