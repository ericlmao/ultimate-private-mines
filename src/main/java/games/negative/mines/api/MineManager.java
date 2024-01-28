package games.negative.mines.api;

import games.negative.alumina.future.BukkitFuture;
import games.negative.mines.api.model.Mine;
import games.negative.mines.api.model.schematic.PrivateMineSchematic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface MineManager {

    @NotNull
    Mine create(@NotNull UUID owner, @NotNull PrivateMineSchematic schematic);

    @NotNull
    Stream<Mine> stream();

    @NotNull
    Optional<Mine> getMine(@NotNull UUID uuid);

    @NotNull
    Optional<Mine> getMine(@NotNull Player player);

    BukkitFuture<Void> save(@NotNull Mine mine);

    void saveSync(@NotNull Mine mine);

    Optional<PrivateMineSchematic> getSchematic(@NotNull String key);

    @NotNull
    Collection<Mine> getMines();

    @NotNull
    Collection<PrivateMineSchematic> getSchematics();

}
