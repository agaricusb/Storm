package com.github.StormTeam.Storm.Thunder_Storm.Tasks;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.github.StormTeam.Storm.BlockTickSelector;
import com.github.StormTeam.Storm.GlobalVariables;
import com.github.StormTeam.Storm.Storm;

/**
 * @author Tudor
 */

public class StrikerTask {

    private int id;
    private Storm storm;
    private GlobalVariables glob;
    private BlockTickSelector ticker;
    private World affectedWorld;

    public StrikerTask(Storm storm, World affectedWorld) {
        this.storm = storm;
        this.glob = Storm.wConfigs.get(affectedWorld);
        this.affectedWorld = affectedWorld;
        try {
            ticker = new BlockTickSelector(affectedWorld, glob.Thunder__Storm_Strike__Chance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {

        id = Bukkit.getScheduler()
                .scheduleSyncRepeatingTask(
                storm,
                new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Block> bloks = new ArrayList<Block>();

                        try {
                            bloks = ticker.getRandomTickedBlocks();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        for (Block b : bloks) {
                            Block tran = b.getRelative(BlockFace.DOWN);
                            if (Storm.biomes.isRainy(tran.getBiome())) 
                                affectedWorld.strikeLightning(tran.getLocation());
                            
                        }
                    }
                },
                0,
                glob.Thunder__Storm_Scheduler_Striker__Calculation__Intervals__In__Ticks);

    }

    public void stop() {
        Bukkit.getScheduler().cancelTask(id);
    }
}
