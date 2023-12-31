package games.negative.mines.core.provider;

import games.negative.alumina.model.registry.Registry;
import games.negative.alumina.model.registry.SimpleRegistry;
import games.negative.mines.api.BlockPallets;
import games.negative.mines.api.model.BlockPallet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class UPMBlockPallets implements BlockPallets {

    private final Registry<String, BlockPallet> pallets;

    public UPMBlockPallets() {
        this.pallets = new SimpleRegistry<>();
    }

    @Override
    public Optional<BlockPallet> getPallet(@NotNull String key) {
        return Optional.ofNullable(this.pallets.get(key));
    }

    @Override
    public void addPallet(@NotNull BlockPallet pallet) {
        this.pallets.put(pallet.key(), pallet);
    }

    @Override
    public void removePallet(@NotNull String key) {
        this.pallets.remove(key);
    }

    @Override
    public @NotNull Collection<BlockPallet> pallets() {
        return pallets.values();
    }
}
