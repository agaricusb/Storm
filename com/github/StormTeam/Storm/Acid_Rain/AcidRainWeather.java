package com.github.StormTeam.Storm.Acid_Rain;

import com.github.StormTeam.Storm.Acid_Rain.Tasks.DamagerTask;
import com.github.StormTeam.Storm.Acid_Rain.Tasks.DissolverTask;
import java.util.*;

import org.bukkit.*;

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
        this.needRainFlag = true;
    }

    @Override
    public void start() {
        if (!glob.Features_Acid__Rain_Dissolving__Blocks && !glob.Features_Acid__Rain_Player__Damaging) {
            return;
        }

        Storm.util.broadcast(glob.Acid__Rain_Messages_On__Acid__Rain__Start, world);

        if (glob.Features_Acid__Rain_Dissolving__Blocks) {
            dissolver = new DissolverTask(storm, world);
            dissolver.run();
        }

        if (glob.Features_Acid__Rain_Player__Damaging) {
            damager = new DamagerTask(storm, world);
            damager.run();
        }

        killID = Storm.manager.createAutoKillWeatherTask("storm_acidrain", world, 7500 + Storm.random.nextInt(1024));
    }

    @Override
    public void end() {
        try {
            Storm.util.broadcast(glob.Acid__Rain_Messages_On__Acid__Rain__Stop, world);
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