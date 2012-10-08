package com.github.StormTeam.Storm;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import static com.github.StormTeam.Storm.Storm.wConfigs;
import org.bukkit.World;

/**
 *
 * @author Tudor
 */
public class WorldMemoryManager implements Listener {

    private Storm storm;

    public WorldMemoryManager(Storm storm) {
        this.storm = storm;
    }

    @EventHandler
    public void onLoad(WorldLoadEvent e) {
        //Simply loads config file on world load - the one-size-fit-all-config-manager
        World world = e.getWorld();
        GlobalVariables config = new GlobalVariables(storm, world.getName());
        config.load();
        wConfigs.put(world.getName(), config);
    }
}
