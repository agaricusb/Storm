package com.github.StormTeam.Storm.Thunder_Storm;

import com.github.StormTeam.Storm.EntityShelterer;
import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Thunder_Storm.Tasks.StrikerTask;
import com.github.StormTeam.Storm.Weather.StormWeather;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.Set;

/**
 * A thunder storm weather object.
 */

public class ThunderStormWeather extends StormWeather {

    private final GlobalVariables glob;
    private StrikerTask striker;
    private EntityShelterer shelter;
    private int killID;

    /**
     * Creates a thunder storm weather object for given world.
     *
     * @param storm The Storm plugin, for sending to StormWeather
     * @param world The world this object will be handling
     */

    public ThunderStormWeather(Storm storm, String world) {
        super(storm, world);
        this.glob = Storm.wConfigs.get(world);
        this.needRainFlag = true;
    }

    /**
     * Called when thunder storm starts for the handled world.
     */

    @Override
    public void start() {
        if (!glob.Features_Thunder__Storms_Thunder__Striking && !glob.Features_Thunder__Storms_Entity__Shelter__Pathfinding) {
            return;
        }

        Storm.util.broadcast(glob.Thunder__Storm_Messages_On__Thunder__Storm__Start, bukkitWorld);

        if (glob.Features_Thunder__Storms_Thunder__Striking) {
            striker = new StrikerTask(storm, bukkitWorld);
            striker.run();
        }

        if (glob.Features_Thunder__Storms_Entity__Shelter__Pathfinding) {
            shelter = new EntityShelterer(storm, world, "storm_thunderstorm", Storm.util.rainBiomes);
            shelter.start();
        }

        //Set the timer to kill
        killID = Storm.manager.createAutoKillWeatherTask("storm_thunderstorm", world, 7500 + Storm.random.nextInt(1024));
    }

    /**
     * Called when thunder storm ends for the handled world.
     */

    @Override
    public void end() {
        Storm.util.broadcast(glob.Thunder__Storm_Messages_On__Thunder__Storm__Stop, bukkitWorld);
        striker.stop();
        striker = null; //Remove references
        shelter.stop();
        shelter = null;
        Bukkit.getScheduler().cancelTask(killID);
    }

    /**
     * Returns the texture to be used during this event.
     *
     * @return The path to the texture
     */

    @Override
    public String getTexture() {
        return null;
    }

    /**
     * Thunder storms don't conflict anything.
     *
     * @return Collections.EMPTY_SET; an empty set
     */

    @Override
    public Set<String> getConflicts() {
        return Collections.EMPTY_SET;
    }
}
