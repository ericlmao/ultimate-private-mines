package games.negative.mines.api;

import games.negative.alumina.future.BukkitFuture;
import games.negative.mines.api.model.PrivateMine;
import games.negative.mines.api.model.schematic.PrivateMineSchematic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface MineManager {

    @NotNull
    PrivateMine create(@NotNull UUID owner, @NotNull PrivateMineSchematic schematic);

    @NotNull
    Stream<PrivateMine> stream();

    @NotNull
    Optional<PrivateMine> getMine(@NotNull UUID uuid);

    @NotNull
    Optional<PrivateMine> getMine(@NotNull Player player);

    BukkitFuture<Void> save(@NotNull PrivateMine mine);

    void saveSync(@NotNull PrivateMine mine);

    Optional<PrivateMineSchematic> getSchematic(@NotNull String key);

    @NotNull
    Collection<PrivateMine> getMines();

    @NotNull
    Collection<PrivateMineSchematic> getSchematics();

}
