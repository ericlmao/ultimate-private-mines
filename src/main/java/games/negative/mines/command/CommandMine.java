package games.negative.mines.command;

import games.negative.alumina.command.Command;
import games.negative.alumina.command.Context;
import games.negative.mines.UPMPlugin;
import games.negative.mines.api.MineManager;
import games.negative.mines.api.model.Mine;
import games.negative.mines.api.model.schematic.PrivateMineSchematic;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CommandMine implements Command {

    private final MineManager mines;

    public CommandMine(@NotNull UPMPlugin plugin) {
        this.mines = plugin.api().mines();
    }

    @Override
    public void execute(@NotNull Context context) {
        Player player = context.player().orElseThrow();

        Optional<PrivateMineSchematic> schem = mines.getSchematic("default");
        if (schem.isEmpty()) {
            player.sendMessage("§cNo schematic found.");
            return;
        }

        Optional<Mine> existing = mines.getMine(player);
        if (existing.isPresent()) {
            Mine mine = existing.get();

            boolean reset = context.argument(0).isPresent();
            if (reset) {
                player.sendMessage("§aResetting mine...");
                mine.reset();
                player.sendMessage("§aMine reset.");
                return;
            }

            mine.spawn().teleport(player, null);
            return;
        }

        player.sendMessage("§aCreating mine...");
        mines.create(player.getUniqueId(), schem.get());
        player.sendMessage("§aMine created.");
    }
}
