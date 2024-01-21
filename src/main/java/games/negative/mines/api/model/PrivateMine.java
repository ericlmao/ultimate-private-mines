package games.negative.mines.api.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import games.negative.alumina.model.Unique;
import games.negative.alumina.util.ColorUtil;
import games.negative.mines.UPMPlugin;
import games.negative.mines.core.util.UPMUtil;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public interface PrivateMine extends Unique {

    void onInitialization(@NotNull UPMPlugin plugin);

    @NotNull
    UUID owner();

    void setOwner(@NotNull UUID owner);

    @Nullable
    String name();

    void name(@Nullable String name);

    @NotNull
    default String getName() {
        String name = name();
        if (name == null) return Bukkit.getOfflinePlayer(owner()).getName() + "'s mine";

        return ColorUtil.translate(Arrays.toString(Base64.getDecoder().decode(name)));
    }

    default void setName(@Nullable String name) {
        if (name == null) {
            name(null);
            return;
        }

        String encoded = Base64.getEncoder().encodeToString(name.getBytes());
        name(encoded);
    }

    int level();

    void level(int level);

    @NotNull
    List<UUID> members();

    @NotNull
    default List<Player> online() {
        return members().stream().filter(UPMUtil::isOnline)
                .map(Bukkit::getPlayer)
                .collect(Collectors.toList());
    }

    void addMember(@NotNull UUID member);

    void removeMember(@NotNull UUID member);

    boolean isMember(@NotNull UUID member);

    int memberLimit();

    void memberLimit(int limit);

    @NotNull
    MineRegion region();

    @NotNull
    Position spawn();

    void spawn(@NotNull Position position);

    Instant creation();

    Instant lastReset();

    void lastReset(@NotNull Instant instant);

    @NotNull
    BlockPallet pallet();

    void pallet(@NotNull BlockPallet pallet);

    boolean ready();

    void setReady(boolean ready);

    @NotNull
    WorldBorder border();
}
