package com.github.StormTeam.Storm.Blizzard;

import com.github.StormTeam.Storm.Blizzard.Tasks.DamagerTask;
import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.StormWeather;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

public class BlizzardWeather extends StormWeather {

    private GlobalVariables glob;
    private DamagerTask damager;
    private int killID;

    /**
     * Creates a blizzard weather object for given world.
     *
     * @param storm The Storm plugin, for sending to StormWeather
     * @param world The world this object will be handling
     */

    public BlizzardWeather(Storm storm, String world) {
        super(storm, world);
        glob = Storm.wConfigs.get(world);
    }

    /**
     * Called when acid rain starts for the handled world.
     */

    @Override
    public void start() {
        if (!glob.Features_Blizzards_Player__Damaging
                && !glob.Features_Blizzards_Slowing__Snow) {
            return;
        }

        World temp = Bukkit.getWorld(world);

        Storm.util.broadcast(glob.Blizzard_Messages_On__Blizzard__Start, temp);

        if (glob.Features_Blizzards_Slowing__Snow) {
            Blizzard.modder.modBestFit();
        }

        damager = new DamagerTask(storm, world);
        damager.run();

        Storm.util.setRainNoEvent(temp, true);

        //Set the timer to kill
        killID = Storm.manager.createAutoKillWeatherTask("storm_blizzard", world, 7500 + Storm.random.nextInt(1024));
    }

    /**
     * Called when acid rain ends for the handled world.
     */

    @Override
    public void end() {
        if (glob.Features_Blizzards_Slowing__Snow) {
            Blizzard.modder.reset();
        }
        Storm.util.broadcast(glob.Blizzard_Messages_On__Blizzard__Stop, world);

        Storm.util.setRainNoEvent(Bukkit.getWorld(world), false);
        damager.stop();
        damager = null; //Remove references        
        Bukkit.getScheduler().cancelTask(killID);
    }


    /**
     * Returns the texture to be used during this event.
     *
     * @return The path to the texture
     */

    @Override
    public String getTexture() {
        return glob.Textures_Blizzard__Texture__Path;
    }

    /**
     * Blizzards conflicts with acid rain because of textures: don't allow to run at same time.
     *
     * @return A set containing the String "storm_acidrain"
     */

    @Override
    public Set<String> getConflicts() {
        HashSet<String> set = new HashSet<String>();
        set.add("storm_acidrain");
        return set; //Yay.
    }
}
