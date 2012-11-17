package com.github.StormTeam.Storm.Volcano.Tasks;

import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Volcano.VolcanoWorker;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * @author Icyene, xiaomao
 */

public class EruptTask implements Runnable {

    private int id;
    private final VolcanoWorker volcano;

    public EruptTask(VolcanoWorker volcano) {
        this.volcano = volcano;
    }

    @Override
    public void run() {
        if (!volcano.active || (volcano.layer < 30 && !(Storm.random.nextInt(100) > 70)))
            return;

        volcano.recalculateLayer();
        Location er = volcano.center.clone();
        er.setY(volcano.center.getBlockY() + volcano.layer);

        if (Storm.random.nextInt(100) > 95)
            volcano.explode(er, volcano.layer / 10 + 2);

        for (int i = 0; i != Storm.random.nextInt(5, 15); i++)
            volcano.world.spawnFallingBlock(er, 10, (byte) 0).setVelocity(new Vector(Math.random() - 0.5, 0.3, Math.random() - 0.5));
    }

    public void start() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Storm.instance, this, 0, 20);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
