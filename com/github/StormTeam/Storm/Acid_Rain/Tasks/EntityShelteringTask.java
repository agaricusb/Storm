package com.github.StormTeam.Storm.Acid_Rain.Tasks;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.MobPathfinder;
import com.github.StormTeam.Storm.Storm;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class EntityShelteringTask {

    private int id;
    private World affectedWorld;
    private Storm storm;
    private GlobalVariables glob;

    public EntityShelteringTask(Storm storm, String affectedWorld) {
        this.storm = storm;
        this.affectedWorld = Bukkit.getWorld(affectedWorld);
        glob = Storm.wConfigs.get(affectedWorld);
    }

    public void run() {
        id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(storm, new Runnable() {
            public void run() {
                for (Entity en : affectedWorld.getEntities()) {
                    if (Storm.util.isEntityUnderSky(en)) {
                        new MobPathfinder(en).setTarget(Storm.util.getSafeLocation(en, 32));
                    }
                }
            }
        }
                , 0, 60);
    }

}
