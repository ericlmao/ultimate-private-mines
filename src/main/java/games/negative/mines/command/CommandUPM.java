package games.negative.mines.command;

import games.negative.alumina.command.Command;
import games.negative.alumina.command.CommandProperties;
import games.negative.alumina.command.Context;
import games.negative.mines.UPMPlugin;
import games.negative.mines.config.UPMConfiguration;
import games.negative.mines.core.Locale;
import games.negative.mines.core.Perm;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandUPM extends Command {

    public CommandUPM(@NotNull UPMPlugin plugin) {
        super(CommandProperties.builder().name("upm").aliases(List.of("ultimateprivatemines", "mineadmin", "pmineadmin"))
                .smartTabComplete(true).permissions(List.of(Perm.ADMIN))
                .description("Administrator command for UltimatePrivateMines!").build());

        injectSubCommand(CommandProperties.builder().name("reload").aliases(List.of("rl")).build(), context -> {
            UPMConfiguration.reloadAll();
            Locale.init(plugin);

            Locale.RELOAD.send(context.sender());
        });
    }

    @Override
    public void execute(@NotNull Context context) {
        Locale.ADMIN_HELP_PROMPT.send(context.sender());
    }
}
