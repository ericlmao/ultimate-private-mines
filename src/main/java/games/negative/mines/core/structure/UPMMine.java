package games.negative.mines.core.structure;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockType;
import games.negative.alumina.util.Tasks;
import games.negative.mines.UPMPlugin;
import games.negative.mines.api.BlockPalletManager;
import games.negative.mines.api.MineManager;
import games.negative.mines.api.model.*;
import games.negative.mines.api.model.schematic.PrivateMineSchematic;
import lombok.Data;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class UPMMine implements Mine {

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

    @Expose
    @SerializedName("schematic")
    private String schematicKey;

    @Expose
    @SerializedName("locked")
    private boolean locked;

    @Expose
    @SerializedName("votes")
    private List<UUID> votes;

    private WorldBorder border;
    private BlockPallet pallet;
    private PrivateMineSchematic schematic;

    private Cache<UUID, Invitation> invitations;

    public UPMMine(UUID uuid, UUID owner, MineRegion region, Position spawn, PrivateMineSchematic schematic) {
        this.uuid = uuid;
        this.owner = owner;
        this.members = Lists.newArrayList(owner);
        this.region = region;
        this.spawn = spawn;
        this.creation = Instant.now();
        this.schematic = schematic;
        this.schematicKey = schematic.key();
        this.locked = false;
        this.votes = Lists.newArrayList();
    }

    @Override
    public void onInitialization(@NotNull UPMPlugin plugin, @NotNull BlockPalletManager pallets, @NotNull MineManager manager) {
        Optional<PrivateMineSchematic> schem = manager.getSchematic(schematicKey);
        Preconditions.checkArgument(schem.isPresent(), "Schematic with key " + schematicKey + " is not present!");

        this.schematic = schem.get();

        int borderSize = schematic.borderSize();

        this.border = Bukkit.createWorldBorder();
        this.border.setCenter(region.getCenter().getLocation());
        this.border.setSize(borderSize, 0);

        if (palletKey == null) {
            this.pallet = pallets.getPallets().stream().filter(BlockPallet::defaultPallet).findFirst().orElseThrow(() -> new NullPointerException("No default pallet found!"));
            this.palletKey = pallet.key();
        } else {
            Optional<BlockPallet> pallet = pallets.getPallet(palletKey);
            if (pallet.isEmpty()) {
                this.pallet = pallets.getPallets().stream().filter(BlockPallet::defaultPallet).findFirst().orElseThrow(() -> new NullPointerException("No default pallet found!"));
                this.palletKey = this.pallet.key();
            }

            assert pallet.isPresent();
            this.pallet = pallet.get();
        }

        this.invitations = CacheBuilder.newBuilder()
                .expireAfterWrite(Duration.of(1, ChronoUnit.MINUTES))
                .build();
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
    public @NotNull PrivateMineSchematic schematic() {
        return schematic;
    }

    @Override
    public void schematic(@NotNull PrivateMineSchematic schematic) {
        this.schematic = schematic;
        this.schematicKey = schematic.key();
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
    public void reset() {
        this.lastReset = Instant.now();

        List<Player> usersInMine = Bukkit.getOnlinePlayers().stream()
                .filter(player -> region.contains(player.getLocation()))
                .collect(Collectors.toList());

        for (Player player : usersInMine) {
            spawn.teleport(player, Sound.ENTITY_ENDERMAN_TELEPORT);
        }

        Tasks.async(() -> {
            Location min = region.min();
            Location max = region.max();

            World world = BukkitAdapter.adapt(min.getWorld());

            BlockVector3 left = BlockVector3.at(min.getX(), min.getY(), min.getZ());
            BlockVector3 right = BlockVector3.at(max.getX(), max.getY(), max.getZ());

            Region region = new CuboidRegion(left, right);

            RandomPattern pattern = new RandomPattern();
            for (Map.Entry<Material, Double> entry : pallet.pallet().entrySet()) {
                BlockType type = BukkitAdapter.asBlockType(entry.getKey());
                if (type == null) continue;

                pattern.add(type, entry.getValue());
            }

            try (EditSession session = WorldEdit.getInstance().newEditSession(world)) {
                session.setBlocks(region, pattern);
                session.commit();
            }
        });
    }

    @Override
    public Optional<Invitation> getInvitation(@NotNull UUID uuid) {
        if (invitations == null) return Optional.empty();

        return Optional.ofNullable(invitations.getIfPresent(uuid));
    }

    @Override
    public void addInvitation(@NotNull Invitation invitation) {
        if (invitations == null) return;

        invitations.put(invitation.invitee(), invitation);
    }

    @Override
    public Cache<UUID, Invitation> invitations() {
        return invitations;
    }

    @Override
    public boolean locked() {
        return false;
    }

    @Override
    public void setLocked(boolean locked) {

    }

    @Override
    public @NotNull List<UUID> votes() {
        return votes;
    }

    @Override
    public void addVote(@NotNull UUID uuid) {
        this.votes.add(uuid);
    }

    @Override
    public boolean hasVoted(@NotNull UUID uuid) {
        return votes.contains(uuid);
    }

    @Override
    public @NotNull UUID uuid() {
        return uuid;
    }
}
