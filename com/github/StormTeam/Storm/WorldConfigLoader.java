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

    /**
     * Creates a WorldConfigLoader object to handle config for worlds (un)loaded by plugins a la MultiVerse.
     *
     * @param storm The storm plugin: used for GlobalVariables loading
     */

    public WorldConfigLoader(Storm storm) {
        this.storm = storm;
    }

    /**
     * Loads a config file for world when it is loaded
     *
     * @param e The WorldLoadEvent
     */

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        String world = e.getWorld().getName();
        GlobalVariables config = new GlobalVariables(storm, world);
        config.load();
        Storm.wConfigs.put(world, config);
    }

    /**
     * Removes the config object on unload; prevents world leaks
     *
     * @param e The WorldUnloadEvent
     */

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        Storm.wConfigs.remove(e.getWorld().getName());
    }
}
