package com.github.StormTeam.Storm.Wildfire;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.StormUtil;
import com.github.StormTeam.Storm.Weather.StormWeather;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

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
            if (recurse >= 40 || (chunk = StormUtil.pickChunk(affectedWorld)) == null)
                return;
            int x = Storm.random.nextInt(15), z = Storm.random.nextInt(15);
            toBurn = chunk.getWorld().getHighestBlockAt(chunk.getBlock(x, 4, z).getLocation()).getLocation().subtract(0, 1, 0).getBlock();

            if (!StormUtil.isBlockProtected(toBurn)
                    && StormUtil.isForest(toBurn.getBiome())
                    && Storm.wConfigs.get(toBurn.getWorld().getName()).Natural__Disasters_Wildfires_Flammable__Blocks.contains(toBurn.getTypeId())) {

                toBurn = toBurn.getLocation().add(0, 1, 0).getBlock();

                if (Wildfire.wildfireBlocks.containsKey((world = toBurn.getWorld().getName()))) {
                    Wildfire.wildfireBlocks.get(world).add(toBurn);
                    toBurn.setType(Material.FIRE);
                    StormUtil.broadcast(glob.Natural__Disasters_Wildfires_Messages_On__Start.replace("%x", toBurn.getX() + "")
                            .replace("%y", toBurn.getY() + "").replace("%z", toBurn.getZ() + ""), affectedWorld);
                }
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
}
