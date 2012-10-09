package com.github.StormTeam.Storm.Acid_Rain;

import com.github.StormTeam.Storm.Acid_Rain.Tasks.DamagerTask;
import com.github.StormTeam.Storm.Acid_Rain.Tasks.DissolverTask;
import java.util.*;

import org.bukkit.*;
import org.bukkit.entity.Player;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.StormWeather;

public class AcidRainWeather extends StormWeather {

    private GlobalVariables glob;
    private DissolverTask dissolver;
    private DamagerTask damager; 
    private int killID;

    public AcidRainWeather(Storm storm, String world) {
        super(storm, world);
        this.glob = Storm.wConfigs.get(world);
    }

    @Override
    public void start() {
        if (!glob.Features_Acid__Rain_Dissolving__Blocks && !glob.Features_Acid__Rain_Player__Damaging) {
            return;
        }
      
        for (Player p : Bukkit.getWorld(world).getPlayers()) {
            Storm.util.message(p, glob.Acid__Rain_Message__On__Acid__Rain__Start);
        }

        if (glob.Features_Acid__Rain_Dissolving__Blocks) {
            dissolver = new DissolverTask(storm, world);
            dissolver.run();
        }

        if (glob.Features_Acid__Rain_Player__Damaging) {
            damager = new DamagerTask(storm, world);
            damager.run();
        }

        killID = Bukkit.getScheduler().scheduleAsyncDelayedTask(
                storm,
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Storm.manager.stopWeather("storm_acidrain", world);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }, 7500 + Storm.random.nextInt(1024));
    }

    @Override
    public void end() {
        try {
            dissolver.stop();
            damager.stop();
            dissolver = null;
            damager = null;
            Bukkit.getScheduler().cancelTask(killID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTexture() {
        return glob.Textures_Acid__Rain__Texture__Path;
    }

    @Override
    public Set<String> getConflicts() {
        HashSet<String> temp = new HashSet();
        temp.add("storm_blizzard");
        return temp;
    }
}