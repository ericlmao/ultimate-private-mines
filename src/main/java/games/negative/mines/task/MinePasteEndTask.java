package games.negative.mines.task;

import games.negative.alumina.util.TimeUtil;
import games.negative.mines.api.model.Mine;
import games.negative.mines.core.Locale;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;

@RequiredArgsConstructor
public class MinePasteEndTask extends BukkitRunnable {

    private final Mine mine;
    private final Duration total;

    @Override
    public void run() {
        mine.setReady(true);

        Player player = Bukkit.getPlayer(mine.owner());
        if (player == null) {
            cancel();
            return;
        }

        String time = TimeUtil.format(total, true);
        Locale.MINE_CREATED.replace("%time%", time).send(player);
    }
}
