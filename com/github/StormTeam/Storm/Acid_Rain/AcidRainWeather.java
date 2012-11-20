package com.github.StormTeam.Storm.Acid_Rain;

import com.github.StormTeam.Storm.Acid_Rain.Tasks.BlockDissolverTask;
import com.github.StormTeam.Storm.Acid_Rain.Tasks.EntityDamagerTask;
import com.github.StormTeam.Storm.EntityShelterer;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.StormUtil;
import com.github.StormTeam.Storm.Weather.StormWeather;
import com.github.StormTeam.Storm.WorldVariables;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

/**
 * An acidic weather object.
 */

public class AcidRainWeather extends StormWeather {

    private final WorldVariables glob;
    private EntityDamagerTask enDamager;
    private EntityShelterer shelter;
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
        this.needRainFlag = true;
    }

    @Override
    public boolean canStart() {
        return glob.Weathers__Enabled_Acid__Rain;
    }

    /**
     * Called when acid rain starts for the handled world.
     */

    @Override
    public void start() {
        StormUtil.broadcast(glob.Acid__Rain_Messages_On__Acid__Rain__Start, bukkitWorld);

        if (glob.Acid__Rain_Features_Entity__Damaging || glob.Blizzard_Features_Player__Damaging) {
            enDamager = new EntityDamagerTask(storm, world);
            enDamager.start();
        }

        if (glob.Acid__Rain_Features_Dissolving__Blocks) {
            dissolver = new BlockDissolverTask(storm, world);
            dissolver.start();
        }

        if (glob.Acid__Rain_Features_Entity__Shelter__Pathfinding) {
            shelter = new EntityShelterer(storm, world, "storm_acidrain", StormUtil.rainBiomes);
            shelter.start();
        }

        killID = Storm.manager.createAutoKillWeatherTask("storm_acidrain", world, 7500 + Storm.random.nextInt(1024));
    }

    /**
     * Called when acid rain ends for the handled world.
     */

    @Override
    public void end() {
        try {
            StormUtil.broadcast(glob.Acid__Rain_Messages_On__Acid__Rain__Stop, bukkitWorld);
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
