package games.negative.mines.command;

import games.negative.alumina.command.Command;
import games.negative.alumina.command.CommandProperties;
import games.negative.alumina.command.Context;
import games.negative.mines.api.MineManager;
import games.negative.mines.api.model.Mine;
import games.negative.mines.core.Locale;
import games.negative.mines.ui.ControlPanelMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class CommandMine extends Command {

    private final MineManager mines;

    public CommandMine(@NotNull MineManager mines) {
        super(CommandProperties.builder().name("mine").aliases(List.of("mines", "pmine", "pmines"))
                .description("Primary command for the Private Mine system!").smartTabComplete(true).build());

        this.mines = mines;

        // /mine help
        injectSubCommand(CommandProperties.builder().name("help").build(), context -> Locale.HELP_PROMPT.send(context.sender()));

        // /mine list
        injectSubCommand(CommandProperties.builder().name("list").build(), context -> {
            Player player = context.player().orElseThrow();

            // open mine list menu
            player.sendMessage("Mine List Menu");
        });

        // /mine create
        injectSubCommand(CommandProperties.builder().name("create").playerOnly(true).build(), context -> {
            Player player = context.player().orElseThrow();

            Optional<Mine> existing = getMine(player);
            if (existing.isPresent()) {
                Locale.MINE_EXISTS.send(player);
                return;
            }

            //todo: Open Mine Creation Menu
            player.sendMessage("Create Mine Menu");
        });

        // /mine home
        injectSubCommand(CommandProperties.builder().name("home").aliases(List.of("spawn")).build(), context -> {
            Player player = context.player().orElseThrow();

            Optional<Mine> existing = getMine(player);
            if (existing.isEmpty()) {
                Locale.MINE_DOES_NOT_EXIST.send(player);
                return;
            }

            Mine mine = existing.get();
            mine.spawn().teleport(player, Sound.ENTITY_ENDERMAN_TELEPORT);

            Locale.MINE_TELEPORTED.send(player);
        });
    }

    @Override
    public void execute(@NotNull Context context) {
        Player player = context.player().orElseThrow();

        Optional<Mine> existing = getMine(player);
        if (existing.isEmpty()) {
            Locale.MINE_DOES_NOT_EXIST.send(player);
            return;
        }

        new ControlPanelMenu(existing.get()).open(player);
    }

    public Optional<Mine> getMine(@NotNull Player player) {
        return mines.getMine(player);
    }
}
