package games.negative.mines.ui;

import games.negative.alumina.builder.ItemBuilder;
import games.negative.alumina.menu.ChestMenu;
import games.negative.alumina.menu.MenuButton;
import games.negative.alumina.util.ItemUpdater;
import games.negative.mines.UPMPlugin;
import games.negative.mines.api.model.Mine;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ControlPanelMenu extends ChestMenu {

    private final MenuButton info;
    private final Mine mine;

    public ControlPanelMenu(@NotNull Mine mine) {
        super("Private Mine", 5);

        this.mine = mine;

        this.info = MenuButton.builder().item(new ItemBuilder(Material.PLAYER_HEAD)
                .setName("&3&lPrivate Mine Information")
                .addLoreLine(" ")
                .addLoreLine("&b&nClick to view members").build())
                .slot(22)
                .action((button, player, event) -> player.sendMessage("You clicked the info button!")).build();

        addButton(info);

        new SkullAnimation().runTaskTimer(UPMPlugin.instance(), 0, 40);
    }

    private class SkullAnimation extends BukkitRunnable {

        private UUID previous = null;

        @Override
        public void run() {
            if (inventory.getViewers().isEmpty()) {
                cancel();
                return;
            }

            List<UUID> members = mine.members();

            ThreadLocalRandom random = ThreadLocalRandom.current();
            int index = random.nextInt(members.size());
            UUID randomMember = members.get(index);

            if (previous != null && previous.equals(randomMember)) return;

            previous = randomMember;

            OfflinePlayer player = Bukkit.getOfflinePlayer(randomMember);
            info.updateItem(itemStack -> {
                ItemUpdater.of(itemStack, SkullMeta.class, skullMeta -> skullMeta.setOwningPlayer(player));
                return itemStack;
            });

            refreshButton(info.getSlot());
        }
    }
}
