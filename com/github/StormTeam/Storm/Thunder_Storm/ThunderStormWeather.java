package com.github.StormTeam.Storm.Thunder_Storm;

import com.github.StormTeam.Storm.EntityShelterer;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.StormUtil;
import com.github.StormTeam.Storm.Thunder_Storm.Tasks.StrikerTask;
import com.github.StormTeam.Storm.Weather.StormWeather;
import com.github.StormTeam.Storm.WorldVariables;
import org.bukkit.Bukkit;

/**
 * A thunder storm weather object.
 */

public class ThunderStormWeather extends StormWeather {

    private final WorldVariables glob;
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
    public boolean canStart() {
        return glob.Weathers__Enabled_Thunder__Storms;
    }

    @Override
    public void start() {
        StormUtil.broadcast(glob.Thunder__Storm_Messages_On__Thunder__Storm__Start, bukkitWorld);

        if (glob.Thunder__Storm_Features_Thunder__Striking) {
            striker = new StrikerTask(storm, bukkitWorld);
            striker.start();
        }

        if (glob.Thunder__Storm_Features_Entity__Shelter__Pathfinding) {
            shelter = new EntityShelterer(storm, world, "storm_thunderstorm", StormUtil.rainBiomes);
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
        StormUtil.broadcast(glob.Thunder__Storm_Messages_On__Thunder__Storm__Stop, bukkitWorld);
        striker.stop();
        striker = null; //Remove references
        shelter.stop();
        shelter = null;
        Bukkit.getScheduler().cancelTask(killID);
    }
}
