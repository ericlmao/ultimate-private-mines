package games.negative.mines.ui.create;

import games.negative.alumina.builder.ItemBuilder;
import games.negative.alumina.menu.MenuButton;
import games.negative.alumina.menu.PaginatedMenu;
import games.negative.mines.api.MineManager;
import games.negative.mines.core.Locale;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class MineCreateSchematicOptionMenu extends PaginatedMenu {

    private final static String PERM_PREFIX = "upm.schematic.";

    public MineCreateSchematicOptionMenu(@NotNull MineManager mines) {
        super("Choose a Theme", 5);
        setCancelClicks(true);

        List<Integer> fillers = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
                17, 18,
                26, 27,
                35, 36, 37, 38, 39, 40, 41, 42, 43, 44);

        fillers.forEach(index -> {
            MenuButton button = MenuButton.builder().slot(index).item(new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(" ").build()).build();
            addButton(button);
        });

        Collection<MenuButton> buttons = generatePaginatedButtons(mines.getSchematics(), input -> MenuButton.builder().item(input.icon())
                .viewCondition(player -> input.isDefault() || player.hasPermission(PERM_PREFIX + input.key().toLowerCase()))
                .action((menuButton, player, event) -> {
                    player.closeInventory();
                    mines.create(player.getUniqueId(), input);

                    Locale.MINE_CREATING.send(player);
                })
                .build());

        setPaginatedButtons(buttons);
        setPaginatedSlots(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34
        );

        MenuButton next = MenuButton.builder().slot(26).item(new ItemBuilder(Material.ARROW)
                .setName("&a&lNext Page").build()).action((button, player, event) -> {
                    changePage(player, page + 1);
        }).build();

        MenuButton previous = MenuButton.builder().slot(18).item(new ItemBuilder(Material.ARROW)
                .setName("&c&lPrevious Page").build()).action((button, player, event) -> {
                    changePage(player, page - 1);
        }).build();

        setNextPageButton(next);
        setPreviousPageButton(previous);
    }
}
