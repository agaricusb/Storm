package com.github.StormTeam.Storm.Wildfire;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.StormWeather;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Collections;
import java.util.Set;

/**
 * A wildfire weather object.
 */

public class WildfireWeather extends StormWeather { //TODO: Make use of getTickedBlock(Chunk) to get better ranges of wildfires 

    private final GlobalVariables glob;
    private int killID;
    private final World affectedWorld;

    /**
     * Creates a wildfire weather object for given world.
     *
     * @param storm The Storm plugin, for sending to StormWeather
     * @param world The world this object will be handling
     */

    public WildfireWeather(Storm storm, String world) {
        super(storm, world);
        affectedWorld = Bukkit.getWorld(world);
        glob = Storm.wConfigs.get(world);
    }

    /**
     * Called when wildfire starts for the handled world.
     */

    @Override
    public void start() {

        if (!glob.Features_Wildfires) {
            return;
        }

        Block toBurn;
        int recurse = 0;
        Chunk chunk;
        while (true) {
            recurse++;
            if (recurse >= 40 || (chunk = Storm.util.pickChunk(affectedWorld)) == null)
                return;
            int x = Storm.random.nextInt(15), z = Storm.random.nextInt(15);
            toBurn = chunk.getWorld().getHighestBlockAt(chunk.getBlock(x, 4, z).getLocation()).getLocation().subtract(0, 1, 0).getBlock();

            if (!Storm.util.isBlockProtected(toBurn)
                    && Storm.util.isForest(toBurn.getBiome())
                    && Wildfire.flammable.contains(toBurn.getTypeId())) {
                break;
            }

            toBurn = toBurn.getLocation().add(0, 1, 0).getBlock();

            if (Wildfire.wildfireBlocks.containsKey((world = toBurn.getWorld().getName()))) {
                Wildfire.wildfireBlocks.get(world).add(toBurn);
                toBurn.setType(Material.FIRE);
                Storm.util.broadcast(glob.Natural__Disasters_Wildfires_Messages_On__Start.replace("%x", toBurn.getX() + "")
                        .replace("%y", toBurn.getY() + "").replace("%z", toBurn.getZ() + ""), affectedWorld);
                break;
            }
        }
        //Abusing the API once again. Once again, who cares?
        killID = Storm.manager.createAutoKillWeatherTask("storm_wildfire", world, 1);
    }

    /**
     * Called when wildfire ends for the handled world.
     */

    @Override
    public void end() {
        Bukkit.getScheduler().cancelTask(killID);
    }

    /**
     * Returns the texture to be used during this event.
     *
     * @return The path to the texture
     */

    @Override
    public String getTexture() {
        return null;
    }

    /**
     * Wildfires don't conflict anything.
     *
     * @return Collections.EMPTY_SET; an empty set
     */

    @Override
    public Set<String> getConflicts() {
        return Collections.EMPTY_SET;
    }
}
