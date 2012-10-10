package com.github.StormTeam.Storm.Wildfire;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Weather.StormWeather;
import java.util.Collections;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * @author Tudor
 */
public class WildfireWeather extends StormWeather { //TODO: Make use of getTickedBlock(Chunk) to get better ranges of wildfires 

    private GlobalVariables glob;
    private int killID;
    private World affectedWorld;

    public WildfireWeather(Storm storm, String world) {
        super(storm, world);
        this.affectedWorld = Bukkit.getWorld(world);
    }

    @Override
    public void start() {

        if (!glob.Features_Wildfires) {
            return;
        }

        Block toBurn;
        while (true) {
            Chunk chunk = Storm.util.pickChunk(affectedWorld);

            int x = Storm.random.nextInt(15), z = Storm.random.nextInt(15);

            toBurn = chunk.getWorld().getHighestBlockAt(chunk.getBlock(x, 4, z).getLocation()).getLocation().subtract(0, 1, 0).getBlock();

            if (!Storm.util.isBlockProtected(toBurn)
                    && Storm.util.isForest(toBurn.getBiome())
                    && Wildfire.flammableList.contains(toBurn.getTypeId())) {
                break;
            }

            toBurn = toBurn.getLocation().add(0, 1, 0).getBlock();

            if (Wildfire.wildfireBlocks.containsKey((world = toBurn.getWorld().getName()))) {
                Wildfire.wildfireBlocks.get(world).add(toBurn);
                toBurn.setType(Material.FIRE);
                for (Player player : affectedWorld.getPlayers()) {
                    Storm.util.message(player,
                            glob.Natural__Disasters_Wildfires_Messages_On__Start.replace("%x", toBurn.getX() + "")
                            .replace("%y", toBurn.getY() + "").replace("%z", toBurn.getZ() + ""));
                }
            }
        }
        //Abusing the API once again. Who cares?
        killID = Storm.manager.createAutoKillWeatherTask("storm_wildfire", world, 1);
    }

    @Override
    public void end() {
        Bukkit.getScheduler().cancelTask(killID);
    }

    @Override
    public String getTexture() {
        return null;
    }

    @Override
    public Set<String> getConflicts() {
        return Collections.EMPTY_SET;
    }
}
