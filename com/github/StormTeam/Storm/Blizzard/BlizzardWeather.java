package com.github.StormTeam.Storm.Blizzard;

import com.github.StormTeam.Storm.Blizzard.Tasks.PlayerTask;
import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.StormWeather;
import java.util.HashSet;
import java.util.Random;

import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class BlizzardWeather extends StormWeather {

    private GlobalVariables glob;
    private PlayerTask damager;
    private Random rand = new Random();
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

        for (Player p : temp.getPlayers()) {
            Storm.util.message(p, glob.Blizzard_Message__On__Blizzard__Start);
        }

        if (glob.Features_Blizzards_Slowing__Snow) {
            ModSnow.mod(true);
        }

        damager = new PlayerTask(storm, world);
        damager.run();

        Storm.util.setStormNoEvent(temp, true);

        //Set the timer to kill
        killID = Bukkit.getScheduler()
                .scheduleAsyncDelayedTask(
                storm,
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Storm.manager.stopWeather("storm_blizzard", world);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 7500 + rand.nextInt(1024));

    }

    @Override
    public void end() {
        if (glob.Features_Blizzards_Slowing__Snow) {
            ModSnow.mod(false);
        }

        damager.stop();
        Storm.util.setStormNoEvent(Bukkit.getWorld(world), false);
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