package com.github.StormTeam.Storm.Acid_Rain;

import com.github.StormTeam.Storm.Acid_Rain.Tasks.BlockDissolverTask;
import com.github.StormTeam.Storm.Acid_Rain.Tasks.EntityDamagerTask;
import com.github.StormTeam.Storm.Acid_Rain.Tasks.EntityShelteringTask;
import com.github.StormTeam.Storm.Acid_Rain.Tasks.PlayerDamagerTask;
import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.StormWeather;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

/**
 * An acidic weather object.
 */

public class AcidRainWeather extends StormWeather {

    private final GlobalVariables glob;
    private PlayerDamagerTask pDamager;
    private EntityDamagerTask enDamager;
    private EntityShelteringTask shelter;
    private BlockDissolverTask dissolver;
    private int killID;

    /**
     * Creates a acid rain weather object for given world.
     *
     * @param storm The Storm plugin, for sending to StormWeather
     * @param world The world this object will be handling
     */

    public AcidRainWeather(Storm storm, String world) {
        super(storm, world);
        glob = Storm.wConfigs.get(world);
        needRainFlag = true;
    }

    /**
     * Called when acid rain starts for the handled world.
     */

    @Override
    public void start() {
        if (!glob.Features_Acid__Rain_Dissolving__Blocks && !glob.Features_Acid__Rain_Player__Damaging && !glob.Features_Acid__Rain_Entity__Damaging && !glob.Features_Acid__Rain_Entity__Shelter__Pathfinding) {
            return;
        }

        Storm.util.broadcast(glob.Acid__Rain_Messages_On__Acid__Rain__Start, world);

        if (glob.Features_Acid__Rain_Player__Damaging) {
            pDamager = new PlayerDamagerTask(storm, world);
            pDamager.run();
        }

        if (glob.Features_Acid__Rain_Entity__Damaging) {
            enDamager = new EntityDamagerTask(storm, world);
            enDamager.run();
        }

        if (glob.Features_Acid__Rain_Dissolving__Blocks) {
            dissolver = new BlockDissolverTask(storm, world);
            dissolver.run();
        }

        if (glob.Features_Acid__Rain_Entity__Shelter__Pathfinding) {
            shelter = new EntityShelteringTask(storm, world);
            shelter.run();
        }

        killID = Storm.manager.createAutoKillWeatherTask("storm_acidrain", world, 7500 + Storm.random.nextInt(1024));
    }

    /**
     * Called when acid rain ends for the handled world.
     */

    @Override
    public void end() {
        try {
            Storm.util.broadcast(glob.Acid__Rain_Messages_On__Acid__Rain__Stop, world);
            pDamager.stop();
            pDamager = null;
            enDamager.stop();
            enDamager = null;
            shelter.stop();
            shelter = null;
            dissolver.stop();
            dissolver = null;
            Bukkit.getScheduler().cancelTask(killID);
        } catch (Exception e) {
        }
    }

    /**
     * Returns the texture to be used during this event.
     *
     * @return The path to the texture
     */

    @Override
    public String getTexture() {
        return glob.Textures_Acid__Rain__Texture__Path;
    }

    /**
     * Acid rain conflicts with blizzards because of textures: don't allow to run at same time.
     *
     * @return A set containing the String "storm_blizzard"
     */

    @Override
    public Set<String> getConflicts() {
        HashSet<String> temp = new HashSet<String>();
        temp.add("storm_blizzard");
        return temp;
    }
}
