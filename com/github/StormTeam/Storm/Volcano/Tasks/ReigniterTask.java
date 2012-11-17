package com.github.StormTeam.Storm.Volcano.Tasks;

import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Volcano.VolcanoControl;
import com.github.StormTeam.Storm.Volcano.VolcanoWorker;
import org.bukkit.Bukkit;

public class ReigniterTask implements Runnable {

    private int id;

    @Override
    public void run() {
        for (VolcanoWorker vulc : VolcanoControl.volcanoes) {
            if (Storm.random.nextInt(100) > 90 && !vulc.active)
                vulc.spawn();
        }
    }

    public void start() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Storm.instance, this, 0, 36000);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
