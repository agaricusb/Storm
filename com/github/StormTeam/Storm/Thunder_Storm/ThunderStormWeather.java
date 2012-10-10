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
    private int killID;

    public ThunderStormWeather(Storm storm, String world) {
        super(storm, world);
        this.glob = Storm.wConfigs.get(world);
        this.needRainFlag = true;
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

        World temp = Bukkit.getWorld(world);

        for (Player p : temp.getPlayers()) {
            Storm.util.message(p, glob.Thunder__Storm_Messages_On__Thunder__Storm__Start);
        }
        striker = new StrikerTask(storm, temp);
        striker.run();

        //Set the timer to kill
        killID = Storm.manager.createAutoKillWeatherTask("storm_thunderstorm", world, 7500 + Storm.random.nextInt(1024));
    }

    @Override
    public void end() {        
        for (Player p : Bukkit.getWorld(world).getPlayers()) {
            Storm.util.message(p, glob.Thunder__Storm_Messages_On__Thunder__Storm__Stop);
        }
        striker.stop();
        striker = null; //Remove references        
        Bukkit.getScheduler().cancelTask(killID);
    }

    @Override
    public Set<String> getConflicts() {
        return Collections.EMPTY_SET;
    }
}