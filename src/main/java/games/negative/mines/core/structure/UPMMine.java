package games.negative.mines.core.structure;

import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import games.negative.mines.UPMPlugin;
import games.negative.mines.api.model.BlockPallet;
import games.negative.mines.api.model.MineRegion;
import games.negative.mines.api.model.Position;
import games.negative.mines.api.model.PrivateMine;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class UPMMine implements PrivateMine {

    @Expose
    @SerializedName("uuid")
    private final UUID uuid;

    @Expose
    @SerializedName("owner")
    private UUID owner;

    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("level")
    private int level;

    @Expose
    @SerializedName("members")
    private List<UUID> members;

    @Expose
    @SerializedName("member-limit")
    private int memberLimit;

    @Expose
    @SerializedName("region")
    private final MineRegion region;

    @Expose
    @SerializedName("spawn")
    private Position spawn;

    @Expose
    @SerializedName("block-pallet")
    private String palletKey;

    @Expose
    @SerializedName("creation")
    private final Instant creation;

    @Expose
    @SerializedName("last-reset")
    private Instant lastReset;

    @Expose
    @SerializedName("ready")
    private boolean ready;

    private WorldBorder border;
    private BlockPallet pallet;

    public UPMMine(UUID uuid, UUID owner, MineRegion region, Position spawn) {
        this.uuid = uuid;
        this.owner = owner;
        this.members = Lists.newArrayList(owner);
        this.region = region;
        this.spawn = spawn;
        this.creation = Instant.now();
    }

    @Override
    public void onInitialization(@NotNull UPMPlugin plugin) {
        int borderSize = 200; //todo make configurable!

        this.border = Bukkit.createWorldBorder();
        this.border.setCenter(region.getCenter().getLocation());
        this.border.setSize(borderSize, 0);

    }

    @Override
    public @NotNull UUID owner() {
        return owner;
    }

    @Override
    public void setOwner(@NotNull UUID owner) {
        this.owner = owner;
    }

    @Override
    public @Nullable String name() {
        return name;
    }

    @Override
    public void name(@Nullable String name) {
        this.name = name;
    }

    @Override
    public int level() {
        return level;
    }

    @Override
    public void level(int level) {
        this.level = level;
    }

    @Override
    public @NotNull List<UUID> members() {
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
        return members.contains(member);
    }

    @Override
    public int memberLimit() {
        return memberLimit;
    }

    @Override
    public void memberLimit(int limit) {
        this.memberLimit = limit;
    }

    @Override
    public @NotNull MineRegion region() {
        return region;
    }

    @Override
    public @NotNull Position spawn() {
        return spawn;
    }

    @Override
    public void spawn(@NotNull Position position) {
        this.spawn = position;
    }

    @Override
    public Instant creation() {
        return creation;
    }

    @Override
    public Instant lastReset() {
        return lastReset;
    }

    @Override
    public void lastReset(@NotNull Instant instant) {
        this.lastReset = instant;
    }

    @Override
    public @NotNull BlockPallet pallet() {
        return pallet;
    }

    @Override
    public void pallet(@NotNull BlockPallet pallet) {
        this.pallet = pallet;
        this.palletKey = pallet.key();
    }

    @Override
    public boolean ready() {
        return ready;
    }

    @Override
    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public @NotNull WorldBorder border() {
        return border;
    }

    @Override
    public @NotNull UUID uuid() {
        return uuid;
    }
}
