package com.github.StormTeam.Storm.Volcano.Tasks;

import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Volcano.VolcanoControl;
import com.github.StormTeam.Storm.Volcano.VolcanoWorker;
import org.bukkit.Bukkit;

public class LifeTask implements Runnable {

    private int id;

    @Override
    public void run() {
        for (VolcanoWorker vulc : VolcanoControl.volcanoes) {
            vulc.area.sendClientChanges();
            if (Storm.random.nextInt(100) > 90)
                vulc.delete();
            else if (Storm.random.nextInt(100) > 70 && !vulc.active)
                vulc.start();
            else if (Storm.random.nextInt(100) > 40)
                vulc.stop();
        }
    }

    public void start() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Storm.instance, this, 36000, 36000);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
