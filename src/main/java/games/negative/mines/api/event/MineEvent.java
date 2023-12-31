package games.negative.mines.api.event;

import games.negative.alumina.event.PluginEvent;
import games.negative.mines.api.model.Mine;
import org.jetbrains.annotations.NotNull;

public abstract class MineEvent extends PluginEvent {

    private final Mine mine;

    public MineEvent(@NotNull Mine mine) {
        this.mine = mine;
    }

    @NotNull
    public Mine getMine() {
        return mine;
    }

}
