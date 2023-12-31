package games.negative.mines.api.event;

import games.negative.mines.api.model.Mine;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public class MineLevelIncrementEvent extends MineEvent implements Cancellable {

    private final Player player;
    private final int before;
    private final int after;
    private boolean cancelled;
    public MineLevelIncrementEvent(@NotNull Mine mine, @NotNull Player player, int before, int after) {
        super(mine);
        this.player = player;
        this.before = before;
        this.after = after;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public int getBefore() {
        return before;
    }

    public int getAfter() {
        return after;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
