package games.negative.mines.command;

import games.negative.alumina.command.Command;
import games.negative.alumina.command.Context;
import games.negative.mines.UPMPlugin;
import games.negative.mines.api.MineManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandMine implements Command {

    private final MineManager mines;

    public CommandMine(@NotNull UPMPlugin plugin) {
        this.mines = plugin.api().mines();
    }

    @Override
    public void execute(@NotNull Context context) {
        Player player = context.player().orElseThrow();
    }
}
