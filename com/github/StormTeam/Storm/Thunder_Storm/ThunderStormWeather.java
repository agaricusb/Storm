package com.github.StormTeam.Storm.Thunder_Storm;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Thunder_Storm.Tasks.StrikerTask;
import com.github.StormTeam.Storm.Weather.StormWeather;
import java.util.Collections;

import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ThunderStormWeather extends StormWeather {

    private GlobalVariables glob;
    private StrikerTask striker;
    private World bWorld;

    public ThunderStormWeather(Storm storm, String world) {
        super(storm, world);
        bWorld = Bukkit.getWorld(world);
        this.glob = Storm.wConfigs.get(bWorld);
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public void start() {
        if (!glob.Features_Thunder__Storms) {
            return;
        }
        for (Player p : bWorld.getPlayers()) {
            Storm.util.message(p, glob.Thunder__Storm_Message__On__Thunder__Storm__Start);
        }
        striker = new StrikerTask(storm, bWorld);
        striker.run();
        Storm.util.setStormNoEvent(bWorld, true);
    }

    @Override
    public void end() {
        try {
            striker.stop();
            Storm.util.setStormNoEvent(bWorld, false);
        } catch (Exception e) {
        }
    }

    @Override
    public Set<String> getConflicts() {
        return Collections.EMPTY_SET;
    }
}