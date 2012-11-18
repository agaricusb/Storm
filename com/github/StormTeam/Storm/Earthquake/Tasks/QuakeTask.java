package com.github.StormTeam.Storm.Earthquake.Tasks;

import com.github.StormTeam.Storm.Earthquake.Quake;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Task to shake every non-creative player
 *
 * @author Giant, Icyene
 */
public class QuakeTask implements Runnable {

    private Quake quake;
    private boolean toggle;
    private int id;

    public QuakeTask(Quake quake) {
        this.quake = quake;
    }

    @Override
    public void run() {
        for (Player p : quake.world.getPlayers()) {
            if (p.getGameMode() == GameMode.CREATIVE || !quake.isQuaking(p.getLocation()))
                continue;

            //int a = (int) Math.sqrt(100 / ((Math.abs(quake.epicenter.getBlockX() - p.getLocation().getBlockX()) + Math.abs(quake.epicenter.getBlockZ() - p.getLocation().getBlockZ())) / 2)) * 2;
            if (toggle) {
                p.setWalkSpeed(0.1F);
            } else {
                p.setWalkSpeed(0.3F);
            }
            toggle = !toggle;
        }
    }

    public void start() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Storm.instance, this, 0, 2);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
