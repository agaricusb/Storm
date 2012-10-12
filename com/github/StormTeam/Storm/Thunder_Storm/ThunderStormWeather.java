package com.github.StormTeam.Storm.Thunder_Storm;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Thunder_Storm.Tasks.StrikerTask;
import com.github.StormTeam.Storm.Weather.StormWeather;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Collections;
import java.util.Set;

/**
 * A thunder storm weather object.
 */

public class ThunderStormWeather extends StormWeather {

    private GlobalVariables glob;
    private StrikerTask striker;
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
        if (!glob.Features_Thunder__Storms) {
            return;
        }

        World temp = Bukkit.getWorld(world);

        Storm.util.broadcast(glob.Thunder__Storm_Messages_On__Thunder__Storm__Start, world);

        striker = new StrikerTask(storm, temp);
        striker.run();

        //Set the timer to kill
        killID = Storm.manager.createAutoKillWeatherTask("storm_thunderstorm", world, 7500 + Storm.random.nextInt(1024));
    }

    /**
     * Called when thunder storm ends for the handled world.
     */

    @Override
    public void end() {
        Storm.util.broadcast(glob.Thunder__Storm_Messages_On__Thunder__Storm__Stop, world);
        striker.stop();
        striker = null; //Remove references        
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
