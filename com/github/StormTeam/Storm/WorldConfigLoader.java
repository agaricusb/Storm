package com.github.StormTeam.Storm;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

/**
 * @author Tudor
 */
class WorldConfigLoader implements Listener {

    private final Storm storm;

    public WorldConfigLoader(Storm storm) {
        this.storm = storm;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        String world = e.getWorld().getName();
        GlobalVariables config = new GlobalVariables(storm, world);
        config.load();
        Storm.wConfigs.put(world, config);
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        Storm.wConfigs.remove(e.getWorld().getName());
    }
}
