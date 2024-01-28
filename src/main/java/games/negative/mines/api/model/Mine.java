package games.negative.mines.api.model;

import com.google.common.cache.Cache;
import games.negative.alumina.model.Unique;
import games.negative.alumina.util.ColorUtil;
import games.negative.mines.UPMPlugin;
import games.negative.mines.api.BlockPalletManager;
import games.negative.mines.api.MineManager;
import games.negative.mines.api.model.schematic.PrivateMineSchematic;
import games.negative.mines.core.util.UPMUtil;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public interface Mine extends Unique {

    /**
     * Performs the initialization process for the private mine.
     *
     * @param plugin   The UPMPlugin instance.
     * @param pallets  The BlockPalletManager instance.
     * @param manager  The MineManager instance.
     */
    void onInitialization(@NotNull UPMPlugin plugin, @NotNull BlockPalletManager pallets, @NotNull MineManager manager);

    /**
     * Returns the owner of the method.
     *
     * @return the UUID of the owner
     */
    @NotNull
    UUID owner();

    /**
     * Sets the owner of the private mine.
     *
     * @param owner the UUID of the owner
     */
    void setOwner(@NotNull UUID owner);

    /**
     * Retrieves the name of the object.
     *
     * @return the name, or null if it is not set
     */
    @Nullable
    String name();

    /**
     * Sets the name of the object.
     *
     * @param name the name to be set (nullable)
     */
    void name(@Nullable String name);

    /**
     * Returns the name of the PrivateMine.
     *
     * @return The name of the PrivateMine.
     */
    @NotNull
    default String getName() {
        String name = name();
        if (name == null) return Bukkit.getOfflinePlayer(owner()).getName() + "'s mine";

        return ColorUtil.translate(Arrays.toString(Base64.getDecoder().decode(name)));
    }

    /**
     * Sets the name of the private mine.
     *
     * @param name the name to set, or null to remove the name
     */
    default void setName(@Nullable String name) {
        if (name == null) {
            name(null);
            return;
        }

        String encoded = Base64.getEncoder().encodeToString(name.getBytes());
        name(encoded);
    }

    /**
     * Returns the level of the PrivateMine.
     *
     * @return The level of the PrivateMine.
     */
    int level();

    /**
     * Sets the level of the private mine.
     *
     * @param level the level of the private mine
     */
    void level(int level);

    /**
     * Returns a list of UUIDs representing the members of a private mine.
     *
     * @return a list of UUIDs representing the members of a private mine
     */
    @NotNull
    List<UUID> members();

    /**
     * Retrieves a list of online players who are members of the PrivateMine.
     *
     * @return A list of online players who are members of the PrivateMine.
     */
    @NotNull
    default List<Player> online() {
        return members().stream().filter(UPMUtil::isOnline)
                .map(Bukkit::getPlayer)
                .collect(Collectors.toList());
    }

    /**
     * Adds a member to the private mine.
     *
     * @param member the UUID of the member to add
     */
    void addMember(@NotNull UUID member);

    /**
     * Removes a member from the private mine.
     *
     * @param member The UUID of the member to be removed.
     */
    void removeMember(@NotNull UUID member);

    /**
     * Checks if a member is part of the PrivateMine.
     * @param member The UUID of the member.
     * @return true if the member is part of the PrivateMine, false otherwise.
     */
    boolean isMember(@NotNull UUID member);

    /**
     * Returns the member limit of the private mine.
     *
     * @return The member limit.
     */
    int memberLimit();

    /**
     * Sets the limit of members allowed in the private mine.
     *
     * @param limit the maximum number of members allowed
     */
    void memberLimit(int limit);

    /**
     * Returns the MineRegion of the current object.
     *
     * @return The MineRegion of the current object.
     */
    @NotNull
    MineRegion region();

    /**
     * Spawns a position.
     *
     * @return the spawned position.
     * @throws NullPointerException if the position is null.
     */
    @NotNull
    Position spawn();

    /**
     * Spawns an entity or object at the given position.
     *
     * @param position the position where the entity or object will be spawned
     */
    void spawn(@NotNull Position position);

    /**
     * Creates and returns an Instant representing the current time.
     *
     * @return an Instant representing the current time
     */
    Instant creation();

    /**
     * Returns the instant of the last reset for the PrivateMine.
     *
     * @return The instant of the last reset.
     */
    Instant lastReset();

    /**
     * Sets the instant of the last reset for the private mine.
     *
     * @param instant the instant of the last reset
     */
    void lastReset(@NotNull Instant instant);

    /**
     * Retrieves the pallet used in the private mine.
     *
     * @return The block pallet used in the private mine.
     */
    @NotNull
    BlockPallet pallet();

    /**
     * Associates a pallet with a BlockPallet instance.
     *
     * @param pallet the BlockPallet instance to associate
     */
    void pallet(@NotNull BlockPallet pallet);

    /**
     * Retrieves the PrivateMineSchematic associated with this PrivateMine.
     *
     * @return The PrivateMineSchematic.
     */
    @NotNull
    PrivateMineSchematic schematic();

    /**
     * Sets the schematic for the private mine.
     *
     * @param schematic the schematic to set
     */
    void schematic(@NotNull PrivateMineSchematic schematic);

    /**
     * Checks if the object is ready.
     *
     * @return true if the object is ready, false otherwise.
     */
    boolean ready();

    /**
     * Sets the ready state of the private mine.
     *
     * @param ready the ready state of the private mine
     */
    void setReady(boolean ready);

    /**
     * Returns the world border of the private mine.
     *
     * @return the world border of the private mine
     */
    @NotNull
    WorldBorder border();

    /**
     * Resets the private mine to its initial state.
     * This method will reset all the settings and attributes of the private mine,
     * including the mine's name, owner, members, level, spawn point, region, schematic, and last reset time.
     * After resetting, the mine will be ready for use again.
     */
    void reset();

    /**
     * Gets an invitation from the cache.
     * @param uuid the uuid
     * @return the invitation
     */
    Optional<Invitation> getInvitation(@NotNull UUID uuid);

    /**
     * Adds an invitation to the cache.
     * @param invitation the invitation
     */
    void addInvitation(@NotNull Invitation invitation);

    /**
     * Gets the invitation cache.
     * @return the invitation cache
     * @throws NullPointerException if the cache is not initialized
     */
    @Nullable
    Cache<UUID, Invitation> invitations();

    /**
     * Gets whether the private mine is locked to the public (except mine members and staff members).
     * @return true if the private mine is locked, false otherwise
     */
    boolean locked();

    /**
     * Sets whether the private mine is locked to the public (except mine members and staff members).
     * @param locked true if the private mine is locked, false otherwise
     */
    void setLocked(boolean locked);

    /**
     * Returns a list of UUIDs representing the votes for the private mine.
     *
     * @return a list of UUIDs representing the votes for the private mine
     */
    @NotNull
    List<UUID> votes();

    /**
     * Adds a vote for the given UUID.
     *
     * @param uuid the UUID of the voter
     */
    void addVote(@NotNull UUID uuid);

    /**
     * Checks if a player with the given UUID has voted.
     *
     * @param uuid the UUID of the player to check
     * @return true if the player has voted, false otherwise
     */
    boolean hasVoted(@NotNull UUID uuid);
}
