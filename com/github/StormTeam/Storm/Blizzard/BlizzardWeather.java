package com.github.StormTeam.Storm.Blizzard;

import com.github.StormTeam.Storm.Blizzard.Tasks.EntityDamagerTask;
import com.github.StormTeam.Storm.EntityShelterer;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.StormUtil;
import com.github.StormTeam.Storm.Weather.StormWeather;
import com.github.StormTeam.Storm.WorldVariables;

import java.util.HashSet;
import java.util.Set;

/**
 * A blizzard weather object.
 */

public class BlizzardWeather extends StormWeather {

    private final WorldVariables glob;
    private EntityDamagerTask enDamager;
    private EntityShelterer shelter;

    /**
     * Creates a blizzard weather object for given world.
     *
     * @param storm The Storm plugin, for sending to StormWeather
     * @param world The world this object will be handling
     */

    public BlizzardWeather(Storm storm, String world) {
        super(storm, world);
        glob = Storm.wConfigs.get(world);
        needRainFlag = true;
        autoKillTicks = 7500 + Storm.random.nextInt(1024);
    }

    @Override
    public boolean canStart() {
        return glob.Weathers__Enabled_Blizzards;
    }

    /**
     * Called when a blizzard starts for the handled world.
     */

    @Override
    public void start() {
        StormUtil.broadcast(glob.Blizzard_Messages_On__Blizzard__Start, bukkitWorld);

        if (glob.Blizzard_Features_Slowing__Snow) {
            try {
                Blizzard.modder.mod();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (glob.Blizzard_Features_Entity__Damaging || glob.Blizzard_Features_Player__Damaging) {
            enDamager = new EntityDamagerTask(storm, world);
            enDamager.start();
        }
        if (glob.Blizzard_Features_Entity__Shelter__Pathfinding) {
            shelter = new EntityShelterer(storm, world, "storm_blizzard", StormUtil.snowyBiomes);
            shelter.start();
        }
    }

    /**
     * Called when acid rain ends for the handled world.
     */

    @Override
    public void end() {
        if (glob.Blizzard_Features_Slowing__Snow) {
            Blizzard.modder.reset();
        }
        StormUtil.broadcast(glob.Blizzard_Messages_On__Blizzard__Stop, bukkitWorld);
        StormUtil.setRainNoEvent(bukkitWorld, false);
        if (enDamager != null)
            enDamager.stop();
        enDamager = null;
        if (shelter != null)
            shelter.stop();
        shelter = null;
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
