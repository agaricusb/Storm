package com.github.StormTeam.Storm.Volcano.Tasks;

import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Volcano.VolcanoControl;
import com.github.StormTeam.Storm.Volcano.VolcanoWorker;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public class LifeTask implements Runnable {

    private int id;

    @Override
    public void run() {
        Set<VolcanoWorker> toDelete = new HashSet<VolcanoWorker>(), toStart = new HashSet<VolcanoWorker>(), toStop = new HashSet<VolcanoWorker>();
        for (VolcanoWorker vulc : VolcanoControl.volcanoes)
            if (Storm.random.nextInt(100) > 90)
                toDelete.add(vulc);
            else if (Storm.random.nextInt(100) > 70 && !vulc.active)
                toStart.add(vulc);
            else if (Storm.random.nextInt(100) > 40)
                toStop.add(vulc);
        for (VolcanoWorker vulc : toDelete)
            vulc.delete();
        for (VolcanoWorker vulc : toStart)
            vulc.start();
        for (VolcanoWorker vulc : toStop)
            vulc.stop();
    }

    public void start() {
        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Storm.instance, this, 36000, 36000);
    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
