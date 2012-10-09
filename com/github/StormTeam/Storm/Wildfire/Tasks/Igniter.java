package com.github.StormTeam.Storm.Wildfire.Tasks;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;
import com.github.StormTeam.Storm.Wildfire.Wildfire;
import static com.github.StormTeam.Storm.Wildfire.Wildfire.wildfireBlocks;

public class Igniter {

    private int id;
    private World affectedWorld;
    private Storm storm;
    private GlobalVariables glob;

    public Igniter(Storm storm, World spawnWorld) {
        this.storm = storm;
        this.affectedWorld = spawnWorld;
        glob = Storm.wConfigs.get(spawnWorld);
    }

    public void run() {

        id = Bukkit.getScheduler().scheduleSyncRepeatingTask(
                storm,
                new Runnable() {
                    @Override
                    public void run() {
                        if (Storm.random.nextInt(100) < glob.Natural__Disasters_Meteor_Chance__To__Spawn) {
                            Block toBurn;
                            while (true) {
                                Chunk chunk = Storm.util.pickChunk(affectedWorld);

                                if (chunk == null) {
                                    Storm.util.log("Selected chunk is null. Aborting wildfire.");
                                    return;
                                }

                                final int x = Storm.random.nextInt(16);
                                final int z = Storm.random.nextInt(16);

                                toBurn = chunk.getWorld().getHighestBlockAt(chunk.getBlock(x, 4, z).getLocation()).getLocation().subtract(0, 1, 0).getBlock();

                                if (!Storm.util.isBlockProtected(toBurn)
                                        && Storm.util.isForest(toBurn.getBiome())
                                        && Wildfire.flammableList.contains(toBurn.getTypeId())) {
                                    break;
                                }

                                toBurn = toBurn.getLocation().add(0, 1, 0)
                                        .getBlock();
                                toBurn.setType(Material.FIRE);
                                World world;
                                if (wildfireBlocks.containsKey((world = toBurn.getWorld()))) {
                                    wildfireBlocks.get(world).add(toBurn);
                                }

                                for (Player p : affectedWorld.getPlayers()) {
                                    Storm.util.message(
                                            p,
                                            glob.Natural__Disasters_Wildfires_Message__On__Start
                                            .replace(
                                            "%x",
                                            toBurn.getX()
                                            + "")
                                            .replace(
                                            "%y",
                                            toBurn.getY()
                                            + "")
                                            .replace(
                                            "%z",
                                            toBurn.getZ()
                                            + ""));
                                }
                            }
                        }
                    }
                },
                glob.Natural__Disasters_Wildfires_Scheduler__Recalculation__Intervals__In__Ticks,
                glob.Natural__Disasters_Wildfires_Scheduler__Recalculation__Intervals__In__Ticks);

    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
