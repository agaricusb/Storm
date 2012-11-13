package com.github.StormTeam.Storm.Earthquake.Tasks;

import com.github.StormTeam.Storm.Earthquake.Quake;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Task to shake every non-creative player
 *
 * @author Giant
 */
public class QuakeTask implements Runnable {

    private Quake quake;
    private boolean toggle;

    public QuakeTask(Quake q) {
        this.quake = q;
    }


    @Override
    public void run() {
        for (Player p : quake.getWorld().getPlayers()) {
            // Don't bother creative players
            if (p.getGameMode() == GameMode.CREATIVE)
                continue;

            // Player isn't quaking...
            if (!quake.isQuaking(p.getLocation()))
                continue;

            int x = quake.getEpicenter().LEFT - p.getLocation().getBlockX() + 1;
            int z = quake.getEpicenter().RIGHT - p.getLocation().getBlockZ() + 1;

            int a = 5 / ((x + z) / 2);

            if (toggle) {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, a), true);
            } else {
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2, a), true);
            }
            toggle = !toggle;
        }
    }
}
