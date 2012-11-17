package com.github.StormTeam.Storm.Volcano.Tasks;

import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Volcano.VolcanoControl;
import com.github.StormTeam.Storm.Volcano.VolcanoWorker;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;

public class GrowthTask implements Runnable {

    private int id;
    private final VolcanoWorker volcano;
    private int heightCap;

    public GrowthTask(VolcanoWorker volcano) {
        this.volcano = volcano;
        this.heightCap = volcano.radius * 2 + volcano.y;
    }

    @Override
    public void run() {
        if (!volcano.active)
            return;
        if (volcano.layer >= heightCap) {
            stop();
            return;
        }

        volcano.recalculateLayer();
        Block set = volcano.center.clone().add(0, volcano.layer, 0).getBlock();
        if (!volcano.area.contains(set)) {
            stop();
            return;
        }
        volcano.area.setBlockFast(set, 11);
        volcano.area.sendClientChanges();
        volcano.layer++;
        VolcanoControl.dumpVolcanoes();
    }

    public void start() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Storm.instance, this, 0, 15000);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
