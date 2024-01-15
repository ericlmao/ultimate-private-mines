package games.negative.mines.core.structure;

import com.google.common.collect.Lists;
import games.negative.mines.api.model.Mine;
import games.negative.mines.api.model.MineRegion;
import games.negative.mines.api.model.position.DefinedPosition;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class UPMMine implements Mine {

    private final UUID uuid;
    private UUID owner;
    private String name;
    private int level;
    private final List<UUID> members;
    private int memberLimit;
    private final MineRegion region;
    private DefinedPosition spawnPosition;
    private final Instant creation;
    private Instant lastReset;
    private boolean ready;
    private String pallet;

    public UPMMine(@NotNull UUID uuid, @NotNull UUID owner, int level, int memberLimit, @NotNull MineRegion region, @NotNull DefinedPosition spawnPosition) {
        this.uuid = uuid;
        this.owner = owner;
        this.level = level;
        this.members = Lists.newArrayList(owner);
        this.memberLimit = memberLimit;
        this.region = region;
        this.spawnPosition = spawnPosition;
        this.creation = Instant.now();
    }

    @Override
    public @NotNull UUID getOwner() {
        return owner;
    }

    @Override
    public void setOwner(@NotNull UUID owner) {
        this.owner = owner;
    }

    @Override
    public @NotNull String getName() {
        if (name == null)
            return Bukkit.getOfflinePlayer(owner).getName() + "'s Mine";
        return name;
    }

    @Override
    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public @NotNull List<UUID> getMembers() {
        return members;
    }

    @Override
    public void addMember(@NotNull UUID member) {
        this.members.add(member);
    }

    @Override
    public void removeMember(@NotNull UUID member) {
        this.members.remove(member);
    }

    @Override
    public boolean isMember(@NotNull UUID member) {
        return this.members.contains(member);
    }

    @Override
    public int getMemberLimit() {
        return memberLimit;
    }

    @Override
    public void setMemberLimit(int limit) {
        this.memberLimit = limit;
    }

    @Override
    public @NotNull MineRegion getRegion() {
        return region;
    }

    @Override
    public @NotNull DefinedPosition getSpawnPosition() {
        return spawnPosition;
    }

    @Override
    public void setSpawnPosition(@NotNull DefinedPosition position) {
        this.spawnPosition = position;
    }

    @Override
    public @NotNull WorldBorder getWorldBorder() {
        return null;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public @NotNull String getPallet() {
        return pallet;
    }

    @Override
    public void setPallet(@NotNull String pallet) {
        this.pallet = pallet;
    }

    @Override
    public @NotNull Instant getLastReset() {
        return lastReset;
    }

    @Override
    public void setLastReset(@NotNull Instant instant) {
        this.lastReset = instant;
    }

    @Override
    public @NotNull Instant getCreationTime() {
        return creation;
    }

    @Override
    public @NotNull UUID uuid() {
        return uuid;
    }
}
