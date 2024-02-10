package games.negative.mines.core;

import games.negative.alumina.logger.Logs;
import games.negative.alumina.message.Message;
import games.negative.mines.UPMPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

public enum Locale {

    ADMIN_HELP_PROMPT(
            "&8&m----------------------------------------",
            "&3&lUPM &8&l| &7Admin Commands",
            "&8&m----------------------------------------",
            "&b/upm reload &7- &fReload configurations.",
            "&8&m----------------------------------------"
    ),

    RELOAD("&3&lUPM &8> &7Reloaded configurations!"),

    HELP_PROMPT(
            "&8&m----------------------------------------",
            "&3&lUltimate Private Mines",
            "&8&m----------------------------------------",
            "&b/mine create &7- &fCreate a new Private Mine.",
            "&b/mine join &7- &fJoin a Private Mine.",
            "&b/mine leave &7- &fLeave a Private Mine.",
            "&b/mine visit <player> &7- &fVisit a player's Private Mine.",
            "&b/mine home &7- &fTravel to your Private Mine",
            "&b/mine invite <player> &7- &fInvite a player to your Private Mine.",
            "&b/mine kick <player> &7- &fKick a player from your Private Mine.",
            "&b/mine disband &7- &fDisband your Private Mine.",
            "&b/mine vote &7- &fVote for a Private Mine.",
            "&b/mine setspawn &7- &fSet the spawn of your Private Mine.",
            "&b/mine list &7- &fList all Private Mines.",
            "&8&m----------------------------------------"
    ),
    MINE_EXISTS("&3&lUPM &8> &7You already have a Private Mine!"),

    MINE_DOES_NOT_EXIST("&3&lUPM &8> &7You do not have a Private Mine!",
            " &8&l >> &7Create one with &b/mine create&7!"),

    MINE_TELEPORTED("&3&lUPM &8> &7You have been teleported to your Private Mine!"),
    ;

    private final String[] defMessage;
    private Message message;

    Locale(@NotNull String... defMessage) {
        this.defMessage = defMessage;
    }

    public static void init(@NotNull UPMPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        validateFile(plugin, file);

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        boolean changed = false;
        for (Locale entry : values()) {
            if (config.isSet(entry.name())) continue;

            List<String> message = List.of(entry.defMessage);
            config.set(entry.name(), message);
            changed = true;
        }

        if (changed) saveFile(plugin, file, config);

        for (Locale entry : values()) {
            entry.message = Message.of(config.getStringList(entry.name()));
        }
    }

    private static void saveFile(@NotNull UPMPlugin plugin, @NotNull File file, @NotNull FileConfiguration config) {
        try {
            config.save(file);
        } catch (IOException e) {
            Logs.SEVERE.print("Could not save messages.yml file!", true);
        }
    }

    private static void validateFile(@NotNull UPMPlugin plugin, @NotNull File file) {
        if (!file.exists()) {
            boolean dirSuccess = file.getParentFile().mkdirs();
            if (dirSuccess) Logs.INFO.print("Created new plugin directory file!");

            try {
                boolean success = file.createNewFile();
                if (!success) return;

                Logs.INFO.print("Created messages.yml file!");
            } catch (IOException e) {
                Logs.SEVERE.print("Could not create messages.yml file!", true);
            }
        }
    }

    public void send(CommandSender sender) {
        message.send(sender);
    }

    public <T extends Iterable<? extends CommandSender>> void send(T iterable) {
        message.send(iterable);
    }

    public void broadcast() {
        message.broadcast();
    }

    public Message replace(String placeholder, String replacement) {
        return message.replace(placeholder, replacement);
    }
}