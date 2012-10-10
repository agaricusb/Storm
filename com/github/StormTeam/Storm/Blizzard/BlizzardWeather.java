package com.github.StormTeam.Storm.Blizzard;

import com.github.StormTeam.Storm.Blizzard.Tasks.DamagerTask;
import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.StormWeather;
import java.util.HashSet;

import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class BlizzardWeather extends StormWeather {

    private GlobalVariables glob;
    private DamagerTask damager;
    private int killID;

    public BlizzardWeather(Storm storm, String world) {
        super(storm, world);
        this.glob = Storm.wConfigs.get(world);
    }

    @Override
    public String getTexture() {
        return glob.Textures_Blizzard__Texture__Path;
    }

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

    @Override
    public Set<String> getConflicts() {
        HashSet<String> set = new HashSet();
        set.add("storm_acidrain");
        return set; //Yay.
    }
}