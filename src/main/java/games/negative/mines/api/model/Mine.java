package games.negative.mines.api.model;

import games.negative.alumina.event.Events;
import games.negative.alumina.model.Unique;
import games.negative.mines.api.event.MineLevelIncrementEvent;
import games.negative.mines.api.model.position.DefinedPosition;
import games.negative.mines.core.util.UPMUtil;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This interface represents a Mine, which is a unique area in a world that players can mine resources from.
 * It extends the Unique interface, meaning each Mine has a unique identifier.
 *
 * The Mine interface defines various methods to interact with the Mine, such as getting and setting its owner, name,
 * level, members, spawn position, member limit, region, world border, pallet, reset status, and timestamps.
 *
 * In addition, the interface provides some default methods for convenient interaction, such as adding levels, checking
 * online players, and saving the Mine to a file.
 */
public interface Mine extends Unique {

    /**
     * Returns the owner of the Mine.
     *
     * @return The UUID of the Mine owner.
     */
    @NotNull
    UUID getOwner();

    /**
     * Sets the owner of the Mine.
     *
     * @param owner the UUID of the new owner
     */
    void setOwner(@NotNull UUID owner);

    /**
     * Retrieves the name of the Mine.
     *
     * @return The name of the Mine.
     */
    @NotNull
    String getName();

    /**
     * Sets the name of the Mine.
     *
     * @param name the new name for the Mine
     */
    void setName(@NotNull String name);

    /**
     * Retrieves the level of the Mine.
     *
     * @return The level of the Mine
     */
    int getLevel();

    /**
     * Sets the level of the Mine.
     *
     * @param level the new level of the Mine
     */
    void setLevel(int level);

    /**
     * Adds the specified amount to the current level of the Mine. This method triggers the MineLevelIncrementEvent
     * before updating the level.
     *
     * @param player the player who triggered the level increment
     * @param amount the amount to increment the level by
     */
    default void addLevel(@NotNull Player player, int amount) {
        int before = getLevel();
        int after = before + amount;

        MineLevelIncrementEvent event = new MineLevelIncrementEvent(this, player, before, after);
        Events.call(event);

        if (event.isCancelled()) return;

        setLevel(after);
    }

    /**
     * Retrieves the list of members associated with the Mine.
     *
     * @return A list of UUIDs representing the members of the Mine.
     */
    @NotNull
    List<UUID> getMembers();

    /**
     * Adds a member to the Mine.
     *
     * @param member the UUID of the member to add
     */
    void addMember(@NotNull UUID member);

    /**
     * Removes a member from the Mine.
     *
     * @param member the UUID of the member to remove
     */
    void removeMember(@NotNull UUID member);

    /**
     * Checks if the given UUID is a member of the Mine.
     *
     * @param member the UUID to check
     * @return true if the UUID is a member, false otherwise
     */
    boolean isMember(@NotNull UUID member);

    /**
     * Returns a list of online players who are members of the Mine.
     *
     * @return the list of online players who are members of the Mine
     */
    default List<Player> getOnlinePlayers() {
        return getMembers().stream().filter(UPMUtil::isOnline)
                .map(Bukkit::getPlayer).collect(Collectors.toList());
    }

    /**
     * Returns the member limit of the Mine.
     *
     * @return The member limit of the Mine.
     */
    int getMemberLimit();

    /**
     * Sets the member limit of the mine.
     *
     * @param limit The new member limit for the mine.
     */
    void setMemberLimit(int limit);

    /**
     * Retrieves the region associated with the Mine.
     *
     * @return The region of the Mine.
     */
    @NotNull
    MineRegion getRegion();

    /**
     * Retrieves the spawn position of the Mine.
     *
     * @return the spawn position of the Mine
     */
    @NotNull
    DefinedPosition getSpawnPosition();

    /**
     * Sets the spawn position of the Mine.
     *
     * @param position the defined position of the spawn
     */
    void setSpawnPosition(@NotNull DefinedPosition position);

    /**
     * Retrieves the world border associated with this Mine.
     *
     * @return The world border of the Mine.
     */
    @NotNull
    WorldBorder getWorldBorder();

    /**
     * This method checks if the Mine is ready for use.
     *
     * @return true if the Mine is ready, false otherwise.
     */
    boolean isReady();

    /**
     * Sets the readiness status of the Mine.
     *
     * @param ready the readiness status to set
     */
    void setReady(boolean ready);

    /**
     * Resets the Mine to its initial state.
     */
    void reset();

    /**
     * Retrieves the pallet for the Mine.
     *
     * @return The BlockPallet for the Mine.
     */
    @NotNull
    BlockPallet getPallet();

    /**
     *
     * Sets the pallet for the Mine.
     *
     * @param pallet The BlockPallet to set for the Mine.
     */
    void setPallet(@NotNull BlockPallet pallet);

    /**
     * Retrieves the timestamp of the last reset.
     *
     * @return The {@link Instant} representing the timestamp of the last reset.
     * @throws NullPointerException if the last reset timestamp is null.
     */
    @NotNull
    Instant getLastReset();

    /**
     * Sets the last reset time of the mine.
     *
     * @param instant the instant representing the last reset time
     */
    void setLastReset(@NotNull Instant instant);

    /**
     * Retrieves the creation time of the Mine.
     *
     * @return The creation time of the Mine.
     */
    @NotNull
    Instant getCreationTime();

    /**
     * Retrieves the file associated with this object.
     *
     * @return The associated file. Will never be null.
     */
    @NotNull
    File getFile();

    /**
     * Saves the data to a file.
     *
     * @throws IOException if there is an error while saving the data.
     */
    void save() throws IOException;
}
